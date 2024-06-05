package cn.li.feature.menu.ui

import android.util.Log
import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.li.data.repository.ShopRepository
import cn.li.data.repository.UserShopCartRepository
import cn.li.datastore.FwpCachedDataStore
import cn.li.datastore.FwpPreferencesDataStore
import cn.li.datastore.proto.UserPreferences
import cn.li.model.CommodityItemDetailVO
import cn.li.model.CommodityItemVO
import cn.li.model.ShopCommodityItemVO
import cn.li.network.dto.ApiResult
import cn.li.network.dto.onError
import cn.li.network.dto.onSuccess
import cn.li.network.dto.user.ShopGoodsDTO
import cn.li.network.dto.user.ShopItemDTO
import cn.li.network.dto.user.ShoppingCartAddDTO
import cn.li.network.dto.user.ShoppingCartDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Deferred
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
    private val userDataStore: FwpPreferencesDataStore

) : ViewModel() {
    companion object {
        const val TAG = "MenuViewModel"
    }

    /**
     * 主界面的状态
     * */
    private val _menuUiState = MutableStateFlow<MenuUiState>(MenuUiState.Loading)
    val menuUiState = _menuUiState.asStateFlow()


    val userLogined
        get() = userDataStore.userLogined

    /**
     * 抽离出来，用于挂起获取数据， 请求商店商品列表+购物车数据
     * */
    private suspend fun suspendGetShopGoodsWithCartInfo(shopId: Long): Pair<ApiResult<List<ShopGoodsDTO>>, ApiResult<List<ShoppingCartDTO>>> {
        val goodsList =
            viewModelScope.async { shopRepository.getCommodityItemList(shopId = shopId) }.await()
        val cartInfo =
            viewModelScope.async { cartRepository.getShoppingCartList(shopId = shopId) }.await()
        return Pair(goodsList, cartInfo)
    }

    /**
     * 将商品列表中的数据加上购物车中的数据
     * */
    private fun transformToCommodityItemVO(
        goodsList: List<ShopGoodsDTO>,
        cartInfo: List<ShoppingCartDTO>
    ): List<ShopCommodityItemVO> {
        // 处理数据
        return goodsList.map { categoryItem ->
            // 将这部分转成 VO
            ShopCommodityItemVO(
                categoryId = categoryItem.categoryId,
                categoryName = categoryItem.categoryName,
                categoryStatus = categoryItem.categoryStatus,
                categoryType = categoryItem.categoryType,
                sort = categoryItem.sort,
                // 关联起购物车数据
                items = categoryItem.items.map { commodityItem ->
                    val cartItem = cartInfo.firstOrNull { cartItem ->
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
    }


    /**
     * 缓存的商品列表
     * */
    private var cachedShopCommodityItemList: List<ShopGoodsDTO> = emptyList()

    private var cachedCartCommodityItemList: List<ShoppingCartDTO> = emptyList()

    private var getShopGoodsWithCartInfoJob: Job? = null

    /**
     * 请求商店商品列表+购物车数据
     * */
    fun getShopGoodsWithCartInfo() {
        _menuUiState.value = MenuUiState.Loading
        getShopGoodsWithCartInfoJob?.cancel()

        // 没有id不请求
        if (selectedShopInfo.value == null) return

        getShopGoodsWithCartInfoJob = viewModelScope.launch(Dispatchers.IO) {
            val shopId = checkNotNull(selectedShopInfo.value?.id)

            if (userLogined) {
                runCatching {
                    suspendGetShopGoodsWithCartInfo(shopId)
                }.onSuccess {
                    val (goodsList, cartInfo) = it

                    // 缓存数据
                    cachedShopCommodityItemList = goodsList.data!!
                    cachedCartCommodityItemList = cartInfo.data!!

                    _menuUiState.value = MenuUiState.Success(
                        shopInfo = _selectedShopInfo.value!!,
                        goods = transformToCommodityItemVO(
                            goodsList = goodsList.data!!,
                            cartInfo = cartInfo.data ?: emptyList()
                        ),
                        cartInfo = cartInfo.data ?: emptyList()
                    )
                }
            } else {
                runCatching {
                    shopRepository.getCommodityItemList(shopId = shopId)
                }.onSuccess { apiResult ->
                    // 缓存数据
                    cachedShopCommodityItemList = apiResult.data!!
                    cachedCartCommodityItemList = emptyList()

                    _menuUiState.value = MenuUiState.Success(
                        shopInfo = _selectedShopInfo.value!!,
                        goods = transformToCommodityItemVO(
                            goodsList = cachedShopCommodityItemList,
                            cartInfo = cachedCartCommodityItemList
                        ),
                        cartInfo = cachedCartCommodityItemList
                    )
                }
            }.onFailure {
                if (it is CancellationException) return@onFailure
                _menuUiState.value =
                    MenuUiState.Failed(it.message ?: "获取信息失败，请稍后再试")
            }
        }
    }

    /**
     * 选择过的配送地址id
     * */
    var selectedAddressId: Long? = null


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

    /**
     * 关闭商品详细界面
     * */
    fun hideCommodityDetail() {
        _commodityDetailUiState.value = CommodityDetailUiState.Hide
    }


    private var getCommodityDetailJob: Job? = null

    /***
     * 获取商品详细信息
     * @param id 商品id
     * */
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
                                isSetmeal = true,
                                mainName = it.mainName,
                                mainColor = it.mainColor,
                                mainStyle = it.mainStyle,
                                mainDecorate = it.mainDecorate,
                                mainNumber = it.mainNumber,
                                mainOrigin = it.mainOrigin,
                                mainPeople = it.mainPeople
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


    private var addCommodityToCartDeferred: Deferred<Result<*>>? = null

    /**
     * 添加商品到购物车
     * */
    suspend fun addCommodityToCart(commodityId: Long, number: Int): Boolean {
        addCommodityToCartDeferred?.cancel()
        // 是否成功添加到购物车
        var addResult = false
        addCommodityToCartDeferred = viewModelScope.async(Dispatchers.IO) {
            runCatching {
                cartRepository.addCommodityToShoppingCart(
                    dto = ShoppingCartAddDTO(
                        dishId = commodityId,
                        setmealId = null,
                        number = number,
                        shopId = selectedShopInfo.value!!.id
                    )
                )
            }.onSuccess { apiResult ->
                apiResult.onSuccess { _ ->
                    // 同步购物车信息，因为是新增，需要知道对应的id
                    launch {
                        val cartInfo =
                            cartRepository.getShoppingCartList(shopId = selectedShopInfo.value!!.id).data!!

                        cachedCartCommodityItemList = cartInfo

                        _menuUiState.value = MenuUiState.Success(
                            shopInfo = _selectedShopInfo.value!!,
                            goods = transformToCommodityItemVO(
                                goodsList = cachedShopCommodityItemList,
                                cartInfo = cartInfo,
                            ),
                            cartInfo = cartInfo
                        )
                    }

                    addResult = true
                }?.onError {
                    _commodityDetailUiState.value = CommodityDetailUiState.Failed(it)
                }
            }.onFailure {
                if (it is CancellationException) return@onFailure
                _commodityDetailUiState.value =
                    CommodityDetailUiState.Failed(it.message ?: "添加失败！请重试！")
            }
        }
        addCommodityToCartDeferred?.await()
        return addResult
    }


    private var changeCartItemJob: Job? = null

    /**
     * 改变购物车中商品的数量
     * */
    fun changeCartItemCount(itemId: Long, count: Int) {
        changeCartItemJob?.cancel()

        changeCartItemJob = viewModelScope.launch {
            runCatching {
                cartRepository.updateShoppingCartCommodityCount(
                    ids = listOf(itemId),
                    counts = listOf(count)
                )
            }.onSuccess {
                // 因为是修改数量，不会改变id，本地修改即可，无需同步

                // count = 0 将其删除
                cachedCartCommodityItemList = if (count == 0) {
                    cachedCartCommodityItemList.filter { it.id != itemId }
                } else {
                    cachedCartCommodityItemList.map {
                        if (it.id == itemId) {
                            it.copy(number = count)
                        } else {
                            it
                        }
                    }
                }

                _menuUiState.value = MenuUiState.Success(
                    shopInfo = _selectedShopInfo.value!!,
                    goods = transformToCommodityItemVO(
                        goodsList = cachedShopCommodityItemList,
                        cartInfo = cachedCartCommodityItemList,
                    ),
                    cartInfo = cachedCartCommodityItemList
                )

                // 空了就得不显示详情了
                if (cachedCartCommodityItemList.isEmpty()) {
                    hideCommodityDetail()
                }
            }
                .onFailure {
                    // TODO 错误处理
                }
        }
    }

    private var clearCartItemJob: Job? = null

    /**
     * 删除购物车的某个商品
     * */
    fun clearCart() {
        clearCartItemJob?.cancel()

        clearCartItemJob = viewModelScope.launch {
            runCatching {
                cartRepository.clearShoppingCart(shopId = selectedShopInfo.value!!.id)
            }.onSuccess {
                cachedCartCommodityItemList = emptyList()
                _menuUiState.value = MenuUiState.Success(
                    shopInfo = _selectedShopInfo.value!!,
                    goods = transformToCommodityItemVO(
                        goodsList = cachedShopCommodityItemList,
                        cartInfo = cachedCartCommodityItemList,
                    ),
                    cartInfo = cachedCartCommodityItemList
                )
            }.onFailure {
                // TODO 错误处理
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