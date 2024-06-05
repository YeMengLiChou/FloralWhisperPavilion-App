package cn.li.feature.userorder

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import cn.li.common.ext.sumAmountOf
import cn.li.data.repository.AddressRepository
import cn.li.data.repository.ShopRepository
import cn.li.data.repository.UserOrderRepository
import cn.li.data.repository.UserRepository
import cn.li.data.repository.UserShopCartRepository
import cn.li.feature.userorder.vo.UserOrderItemVo
import cn.li.feature.userorder.vo.UserOrderSettlementVo
import cn.li.model.CommodityItemVO
import cn.li.network.dto.onError
import cn.li.network.dto.onSuccess
import cn.li.network.dto.user.OrderDetailDTO
import cn.li.network.dto.user.OrderRecordDTO
import cn.li.network.dto.user.OrderSubmitDTO
import cn.li.network.dto.user.OrderSubmitResultDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserOrderViewModel
@Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val addressRepository: AddressRepository,
    private val shopRepository: ShopRepository,
    private val userOrderRepository: UserOrderRepository,
    private val cartRepository: UserShopCartRepository,
) : ViewModel() {

    private val _userOrderUiState = MutableStateFlow<UserOrderUiState>(UserOrderUiState.Loading)
    val userOrderUiState = _userOrderUiState.asStateFlow()


    private fun transformOrderVo(record: OrderRecordDTO): UserOrderItemVo {
        return UserOrderItemVo(
            orderId = record.orders.id,
            orderNumber = record.orders.number,
            orderTotalAmount = record.orders.amount.toString(),
            completedOrderTime = record.orders.deliveryTime,
            commodityCount = record.orderDetailList.sumOf { it.number },
            shopName = record.shop.name,
            shopAddress = record.shop.address,
            submitOrderTime = record.orders.orderTime,
            statusText = when (record.orders.status) {
                1 -> "待付款"
                2 -> "待接单"
                3 -> "已接单"
                4 -> "派送中"
                5 -> "已完成"
                6 -> "已取消"
                else -> throw IllegalStateException("order-status has a illegal value: ${record.orders.status}")
            },
            remark = record.orders.remark ?: "",
            status = record.orders.status,
            deliveryText = when (record.orders.deliveryStatus) {
                2 -> "自取"
                else -> "门店配送"
            },
            deliveryAddress = record.addressBook?.let {
                "${it.provinceName}${it.cityName}${it.districtName}${it.detail}"
            },
            consignee = record.addressBook?.consignee,
            phone = record.addressBook?.phone,
            commodityList = record.orderDetailList.map {
                CommodityItemVO(
                    id = it.dishId,
                    name = it.name,
                    imageUrl = it.image,
                    price = it.amount,
                    description = "",
                    status = 0,
                    cartItemId = 0,
                    isSetmeal = 0,
                    cartCount = it.number
                )
            }
        )
    }


    val uncompletedOrder
        get() = userOrderRepository.getUncompletedOrderPagingData(pageSize = 25)
            .map {
                it.map { record -> transformOrderVo(record) }
            }
            .cachedIn(viewModelScope)


    val completedOrder
        get() = userOrderRepository.getCompletedOrderPagingData(pageSize = 25)
            .map {
                Log.d(TAG, "completed: $it")
                it.map { record ->
                    Log.d(TAG, "completed: $record")
                    transformOrderVo(record)
                }
            }
            .cachedIn(viewModelScope)


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
                val orderInfo = userOrderRepository.submitOrder(dto = submitInfo)
                userOrderRepository.payOrder(orderInfo.data!!.orderNumber)
                orderInfo
            }.onSuccess { apiResult ->
                apiResult.onSuccess {
                    _userOrderSettlementUiState.value =
                        UserOrderSettlementUiState.OrderSubmitSuccess(it!!)
                }?.onError { msg ->
                    _userOrderSettlementUiState.value =
                        UserOrderSettlementUiState.Failed(msg.takeIf { it.isNotEmpty() }
                            ?: "网络错误，请重试！")
                }
            }.onFailure {
                if (it is CancellationException) return@onFailure
                _userOrderSettlementUiState.value =
                    UserOrderSettlementUiState.Failed(it.message ?: "网络错误，请重试！")
            }
        }
    }

    private val _userOrderDetailUiState =
        MutableStateFlow<UserOrderDetailUiState>(UserOrderDetailUiState.Loading)
    val userOrderDetailUiState = _userOrderDetailUiState.asStateFlow()

    private var submitUserOrderJob: Job? = null

    private var clickOrderItem: UserOrderItemVo? = null

    fun setClickOrderItem(orderItem: UserOrderItemVo) {
        clickOrderItem = orderItem
        _userOrderDetailUiState.value = UserOrderDetailUiState.Success(orderItem)
    }

    fun getClickOrderItem(): UserOrderItemVo? {
        return clickOrderItem
    }

    private var getOrderDetailJob: Job? = null

    fun getOrderDetailByOrderId(orderId: Long) {
        _userOrderDetailUiState.value = UserOrderDetailUiState.Loading
        getOrderDetailJob?.cancel()

        getOrderDetailJob = viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                userOrderRepository.getOrderDetail(id = orderId)
            }.onSuccess { apiResult ->
                apiResult.onSuccess {
                    checkNotNull(it)
                    _userOrderDetailUiState.value =
                        UserOrderDetailUiState.Success(transformOrderVo(it))
                }?.onError {
                    _userOrderDetailUiState.value =
                        UserOrderDetailUiState.Failed(it.takeIf { it.isNotEmpty() }
                            ?: "网络错误，请重试！")
                }
            }.onFailure {
                if (it is CancellationException) return@onFailure
                Log.d(TAG, "getOrderDetailByOrderId: $it")
                _userOrderDetailUiState.value =
                    UserOrderDetailUiState.Failed(it.message ?: "网络错误，请重试！")
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

    data class OrderSubmitSuccess(val order: OrderSubmitResultDTO) : UserOrderSettlementUiState

    data class Failed(val msg: String) : UserOrderSettlementUiState
}

sealed interface UserOrderDetailUiState {
    data object Loading : UserOrderDetailUiState
    data class Success(val data: UserOrderItemVo) : UserOrderDetailUiState

    data class Failed(val msg: String) : UserOrderDetailUiState
}