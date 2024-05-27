package cn.li.feature.menu.ui

import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.li.data.repository.ShopRepository
import cn.li.data.repository.UserShopCartRepository
import cn.li.network.dto.onError
import cn.li.network.dto.onSuccess
import cn.li.network.dto.user.ShopGoodsDTO
import cn.li.network.dto.user.ShopItemDTO
import cn.li.network.dto.user.ShoppingCartDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@Stable
class MenuViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val shopRepository: ShopRepository,
    private val cartRepository: UserShopCartRepository,

    ) : ViewModel() {
    companion object {
        const val TAG = "MenuViewModel"
    }

    private val _menuUiState = MutableStateFlow<MenuUiState>(MenuUiState.Loading)
    val menuUiState = _menuUiState.asStateFlow()


    private var getShopGoodsWithCartInfoJob: Job? = null
    fun getShopGoodsWithCartInfo() {
        _menuUiState.value = MenuUiState.Loading
        getShopGoodsWithCartInfoJob?.cancel()

        // 没有id不请求
        if (selectedShopInfo.value == null) return

        getShopGoodsWithCartInfoJob = viewModelScope.launch(Dispatchers.IO) {
            val shopId = checkNotNull(selectedShopInfo.value?.id)
            runCatching {
                val goodsList = async { shopRepository.getGoodsItemList(shopId = shopId) }.await()
                val cartInfo = async { cartRepository.getShoppingCartList(shopId = shopId) }.await()
                Pair(goodsList, cartInfo)
            }.onSuccess {
                val (goodsList, cartInfo) = it
                if (goodsList.code == 200 && cartInfo.code == 200) {


                    _menuUiState.value = MenuUiState.Success(
                        shopInfo = _selectedShopInfo.value!!,
                        goods = checkNotNull(goodsList.data).let { data ->
                            data // TODO: transform
                        },
                        cartInfo = checkNotNull(cartInfo.data).let { data ->
                            data
                        }
                    )

                } else {
                    _menuUiState.value = MenuUiState.Failed(
                        "获取信息失败，请稍后再试"
                    )
                }
            }.onFailure {
                if (it is CancellationException) return@onFailure
                _menuUiState.value =
                    MenuUiState.Failed(it.message ?: "获取信息失败，请稍后再试")
            }
        }
    }


    private val _selectedShopInfo = MutableStateFlow<ShopItemDTO?>(null)
    val selectedShopInfo = _selectedShopInfo.asStateFlow()

    /**
     * 设置已选中的地址信息
     * */
    fun setSelectedShopInfo(shopInfo: ShopItemDTO) {
        this._selectedShopInfo.value = shopInfo
    }


    /**
     * "选择门店" 界面的UI状态
     * */
    private val _chooseShopUiState: MutableStateFlow<ChooseShopUiState> =
        MutableStateFlow(ChooseShopUiState.Loading)
    val chooseShopUiState = _chooseShopUiState


    private var getShopListJob: Job? = null

    /**
     * @param delay 用于延迟发送数据，放置加载过快动画闪烁
     * */
    fun getShopList(delay: Long) {
        _chooseShopUiState.value = ChooseShopUiState.Loading
        getShopListJob?.cancel()

        getShopListJob = viewModelScope.launch(Dispatchers.IO) {
            // 请求
            runCatching {
                // 延迟发送时间
                if (delay > 0) {
                    delay(delay)
                }
                shopRepository.getShopList()
            }
                .onSuccess { apiResult ->
                    apiResult
                        .onSuccess {
                            _chooseShopUiState.value = ChooseShopUiState.Success(
                                shopList = checkNotNull(apiResult.data)
                            )
                        }
                        ?.onError {
                            _chooseShopUiState.value = ChooseShopUiState.Failed(it)
                        }
                }
                .onFailure {
                    if (it is CancellationException) return@onFailure
                    _chooseShopUiState.value =
                        ChooseShopUiState.Failed(it.message ?: "获取失败！请刷新！")
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}

/**
 * 展示商品的界面 UI状态
 * */
sealed interface MenuUiState {
    data object Loading : MenuUiState

    @Stable
    data class Success(
        val shopInfo: ShopItemDTO,
        val goods: List<ShopGoodsDTO>,
        val cartInfo: List<ShoppingCartDTO>
    ) : MenuUiState

    @Stable
    data class Failed(val error: String) : MenuUiState
}


/**
 * 选择门店界面的 UI状态
 * */
sealed interface ChooseShopUiState {
    data object Loading : ChooseShopUiState

    data class Success(
        val shopList: List<ShopItemDTO>
    ) : ChooseShopUiState

    data class Failed(
        val error: String
    ) : ChooseShopUiState
}