package cn.li.feature.userorder

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import cn.li.common.ext.sumAmountOf
import cn.li.data.repository.AddressRepository
import cn.li.data.repository.ShopRepository
import cn.li.data.repository.UserOrderRepository
import cn.li.data.repository.UserShopCartRepository
import cn.li.feature.userorder.vo.UserOrderSettlementVo
import cn.li.network.dto.user.OrderDetailDTO
import cn.li.network.dto.user.OrderSubmitDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserOrderViewModel
@Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val addressRepository: AddressRepository,
    private val shopRepository: ShopRepository,
    private val userOrderRepository: UserOrderRepository,
    private val cartRepository: UserShopCartRepository
) : ViewModel() {

    private val _userOrderUiState = MutableStateFlow<UserOrderUiState>(UserOrderUiState.Loading)
    val userOrderUiState = _userOrderUiState.asStateFlow()

    private val _uncompletedOrder = MutableStateFlow<PagingData<OrderDetailDTO>>(PagingData.empty())
    val uncompletedOrder = _uncompletedOrder.asStateFlow()

    private var getUnCompletedOrderJob: Job? = null

    fun getUncompletedOrder() {
        _userOrderUiState.value = UserOrderUiState.Loading
        getUnCompletedOrderJob?.cancel()

        getUnCompletedOrderJob = viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                userOrderRepository.getUncompletedOrderPagingData()
                    .collectLatest { _uncompletedOrder.value = it }
            }.onFailure {
                if (it is CancellationException) return@onFailure
                Log.d(TAG, "getUncompletedOrder-failed: $it")
                _userOrderUiState.value = UserOrderUiState.Failure(it.message ?: "出现问题，请刷新")
            }
        }
    }

    private val _completedOrder = MutableStateFlow<PagingData<OrderDetailDTO>>(PagingData.empty())
    val completedOrder = _completedOrder.asStateFlow()

    private var getCompletedOrderJob: Job? = null

    fun getCompletedOrder() {
        _userOrderUiState.value = UserOrderUiState.Loading
        getCompletedOrderJob?.cancel()

        getCompletedOrderJob = viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                userOrderRepository.getCompletedOrderPagingData()
                    .collectLatest { _completedOrder.value = it }
            }.onFailure {
                if (it is CancellationException) return@onFailure
                Log.d(TAG, "getCompletedOrder-failed: $it")
                _userOrderUiState.value = UserOrderUiState.Failure(it.message ?: "出现问题，请刷新")
            }
        }
    }


    /**
     * 用户下单界面的状态
     *
     * */
    private val _userOrderSettlementUiState =
        MutableStateFlow<UserOrderSettlementUiState>(UserOrderSettlementUiState.Loading)

    val userOrderSettlementUiState = _userOrderSettlementUiState.asStateFlow()

    private var getCartInfoJob: Job? = null

    /**
     * 获取 购物车信息
     * */
    fun getOrderSettlementInfo(shopId: Long, addressId: Long?) {
        _userOrderSettlementUiState.value = UserOrderSettlementUiState.Loading
        getCartInfoJob?.cancel()

        getCartInfoJob = viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                // 获取这3部分的内容：购物车信息，门店信息，用户地址信息
                val cartListDeferred = async {
                    cartRepository.getShoppingCartList(shopId = shopId)
                }
                val shopInfoDeferred = async { shopRepository.getShopList() }
                val addressInfo =
                    addressId?.let { async { addressRepository.getAddressBookById(id = it) } }
                        ?.await()?.data
                val cartList = cartListDeferred.await().data
                val shopInfo = shopInfoDeferred.await().data!!.first { it.id == shopId }
                // 转换为 vo
                return@runCatching UserOrderSettlementVo(
                    shopId = shopInfo.id,
                    shopName = shopInfo.name,
                    shopLocation = shopInfo.address,
                    deliveryStatus = if (addressId == null) UserOrderSettlementVo.DELIVERY_STATUS_SELF_PICKUP else UserOrderSettlementVo.DELIVERY_STATUS_DELIVERY,
                    userAddressId = addressId,
                    userAddress = addressInfo?.let { "${it.provinceName ?: ""}${it.cityName ?: ""}${it.districtName ?: ""}${it.detail}" },
                    userName = addressInfo?.consignee,
                    userPhoneNumber = addressInfo?.phone,
                    orderAmount = cartList!!.sumAmountOf { it.number * it.amount }.toString(),
                    cartDTO = cartList,
                )
            }
                .onSuccess {
                    _userOrderSettlementUiState.value = UserOrderSettlementUiState.Success(it)
                }
                .onFailure {
                    if (it is CancellationException) return@onFailure
                    if (it is NoSuchElementException) {
                        Log.e(TAG, "getOrderSettlementInfo: not find shopId: $shopId")
                    }
                    Log.e(TAG, "getOrderSettlementInfo: ${it.message}", it)
                    _userOrderSettlementUiState.value =
                        UserOrderSettlementUiState.Failed(it.message ?: "网络错误，请重试！")
                }

        }
    }


    private var submitUserOrderJob: Job? = null

    /**
     * 提交订单
     *
     * */
    fun submitUserOrder(
        submitInfo: OrderSubmitDTO
    ) {
        _userOrderSettlementUiState.value = UserOrderSettlementUiState.Loading
        submitUserOrderJob?.cancel()

        submitUserOrderJob = viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                userOrderRepository.submitOrder(dto = submitInfo)
            }.onSuccess {
                _userOrderSettlementUiState.value = UserOrderSettlementUiState.OrderSubmitSuccess
            }.onFailure {
                if (it is CancellationException) return@onFailure
                _userOrderSettlementUiState.value =
                    UserOrderSettlementUiState.Failed(it.message ?: "网络错误，请重试！")
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

    companion object {
        private const val TAG = "UserOrderViewModel"
    }
}


sealed interface UserOrderUiState {
    data object Loading : UserOrderUiState
    data class Success(val userOrder: List<OrderDetailDTO>) : UserOrderUiState

    data class Failure(val error: String) : UserOrderUiState
}

/**
 * 用户下单界面的界面状态
 *
 * */
sealed interface UserOrderSettlementUiState {

    /**
     * 加载中，从接口中获取订单信息
     * */
    data object Loading : UserOrderSettlementUiState

    data class Success(val data: UserOrderSettlementVo) : UserOrderSettlementUiState

    data object OrderSubmitSuccess : UserOrderSettlementUiState

    data class Failed(val msg: String) : UserOrderSettlementUiState
}

