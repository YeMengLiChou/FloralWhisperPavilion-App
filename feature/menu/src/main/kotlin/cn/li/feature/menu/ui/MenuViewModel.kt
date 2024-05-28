package cn.li.feature.menu.ui

import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.li.data.repository.ShopRepository
import cn.li.data.repository.UserShopCartRepository
import cn.li.datastore.FwpCachedDataStore
import cn.li.model.CommodityItemDetailVO
import cn.li.model.CommodityItemVO
import cn.li.model.ShopCommodityItemVO
import cn.li.network.dto.onError
import cn.li.network.dto.onSuccess
import cn.li.network.dto.user.ShopItemDTO
import cn.li.network.dto.user.ShoppingCartAddDTO
import cn.li.network.dto.user.ShoppingCartDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
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
    private val cachedDataStore: FwpCachedDataStore,
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
                val goodsList =
                    async { shopRepository.getCommodityItemList(shopId = shopId) }.await()
                val cartInfo = async { cartRepository.getShoppingCartList(shopId = shopId) }.await()
                Pair(goodsList, cartInfo)
            }.onSuccess {
                val (goodsList, cartInfo) = it
                if (goodsList.code == 200 && cartInfo.code == 200) {
                    // 处理数据
                    val goods =
                        checkNotNull(goodsList.data)
                            .map { categoryItem ->
                                // 将这部分转成 VO
                                ShopCommodityItemVO(
                                    categoryId = categoryItem.categoryId,
                                    categoryName = categoryItem.categoryName,
                                    categoryStatus = categoryItem.categoryStatus,
                                    categoryType = categoryItem.categoryType,
                                    sort = categoryItem.sort,
                                    // 关联起购物车数据
                                    items = categoryItem.items.map { commodityItem ->
                                        val cartItem = cartInfo.data?.firstOrNull { cartItem ->
                                            cartItem.dishId == commodityItem.id
                                        }
                                        CommodityItemVO(
                                            id = commodityItem.id,
                                            name = commodityItem.name,
                                            imageUrl = commodityItem.image,
                                            description = commodityItem.description,
                                            price = commodityItem.price,
                                            cartCount = cartItem?.number ?: 0,
                                            cartItemId = cartItem?.id,
                                            status = commodityItem.status,
                                            isSetmeal = commodityItem.isSetmeal
                                        )
                                    }
                                )
                            }
                            .sortedWith(Comparator.comparingInt { a -> a.sort })

                    _menuUiState.value = MenuUiState.Success(
                        shopInfo = _selectedShopInfo.value!!,
                        goods = goods,
                        cartInfo = checkNotNull(cartInfo.data)
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
    fun setSelectedShopInfo(shopInfo: ShopItemDTO?) {
        this._selectedShopInfo.value = shopInfo
        setChosenShopId(shopInfo?.id ?: 0)
    }


    /**
     * "选择门店" 界面的UI状态
     * */
    private val _chooseShopUiState: MutableStateFlow<ChooseShopUiState> =
        MutableStateFlow(ChooseShopUiState.Loading)
    val chooseShopUiState = _chooseShopUiState


    /**
     * 缓存得到的门店id
     * */
    val cachedShopId get() = cachedDataStore.data.lastChosenShopId

    private fun setChosenShopId(shopId: Long) {
        cachedDataStore.updateChosenShopId(shopId)
    }


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


    private var getShopDetailByIdJob: Job? = null

    /**
     *
     * 查询当前的门店详情
     * */
    fun getShopDetailById(shopId: Long) {
        getShopDetailByIdJob?.cancel()
        getShopDetailByIdJob = viewModelScope.launch {
            runCatching {
                shopRepository.getShopList()
            }.onSuccess { apiResult ->
                apiResult.onSuccess { data ->
                    val shop = checkNotNull(data).firstOrNull { it.id == shopId }
                    setSelectedShopInfo(shop)
                    _menuUiState.value = MenuUiState.FetchedShopInfo
                }?.onError {

                }
            }.onFailure {
                if (it is CancellationException) return@onFailure
            }
        }
    }


    /**
     * 商品详细界面的
     * */
    private val _commodityDetailUiState =
        MutableStateFlow<CommodityDetailUiState>(CommodityDetailUiState.Hide)

    val commodityDetailUIState = _commodityDetailUiState.asStateFlow()

    fun hideCommodityDetail() {
        _commodityDetailUiState.value = CommodityDetailUiState.Hide
    }

    private var getCommodityDetailJob: Job? = null

    fun getCommodityDetail(id: Long) {
        _commodityDetailUiState.value = CommodityDetailUiState.Loading
        getCommodityDetailJob?.cancel()

        getCommodityDetailJob = viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                shopRepository.getCommodityDetailById(id)
            }.onSuccess { apiResult ->
                apiResult.onSuccess { data ->
                    _commodityDetailUiState.value = CommodityDetailUiState.Success(
                        checkNotNull(data).let {
                            CommodityItemDetailVO(
                                id = it.dishId,
                                name = it.dishName,
                                imageUrl = it.image,
                                description = it.description,
                                price = it.price,
                                status = it.status,
                                isSetmeal = true
                            )
                        }
                    )
                }?.onError {
                    _commodityDetailUiState.value = CommodityDetailUiState.Failed(it)
                }
            }.onFailure {
                if (it is CancellationException) return@onFailure
                _commodityDetailUiState.value =
                    CommodityDetailUiState.Failed(it.message ?: "获取失败！请刷新！")
            }
        }
    }


    private var addCommodityToCartJob: Job? = null

    fun addCommodityToCart(commodityId: Long, number: Int, isSetmeal: Boolean) {
        addCommodityToCartJob?.cancel()

        addCommodityToCartJob = viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                cartRepository.addCommodityToShoppingCart(
                    dto = ShoppingCartAddDTO(
                        dishId = if (isSetmeal) null else commodityId,
                        setmealId = if (isSetmeal) commodityId else null,
                        number = number,
                        shopId = selectedShopInfo.value!!.id
                    )
                )
            }.onSuccess { apiResult ->
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

    data object FetchedShopInfo : MenuUiState

    @Stable
    data class Success(
        val shopInfo: ShopItemDTO,
        val goods: List<ShopCommodityItemVO>,
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

sealed interface CommodityDetailUiState {

    data object Hide : CommodityDetailUiState

    data object Loading : CommodityDetailUiState
    data class Success(
        val commodityDetail: CommodityItemDetailVO
    ) : CommodityDetailUiState

    data class Failed(
        val error: String
    ) : CommodityDetailUiState
}