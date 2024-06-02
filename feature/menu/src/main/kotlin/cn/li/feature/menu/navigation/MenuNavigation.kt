package cn.li.feature.menu.navigation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import cn.li.feature.menu.navigation.ChooseShopNavigation.navigateToChooseShop
import cn.li.feature.menu.ui.MenuScreen
import cn.li.feature.menu.ui.MenuViewModel
import cn.li.model.NavigationRoute
import cn.li.model.constant.DEEP_LINK_PREFIX

object MenuNavigation : NavigationRoute {
    override val routePrefix: String
        get() = "menu/display"
    override val deepLinkPrefix: String
        get() = "$DEEP_LINK_PREFIX/$routePrefix"


    /**
     * 导航到 **点单** 界面
     * */
    fun NavHostController.navigateToMenu(navOptions: NavOptions) {
        navigate(this@MenuNavigation.route, navOptions)
    }

    @SuppressLint("UnrememberedGetBackStackEntry")
    fun NavGraphBuilder.menuScreen(
        navController: NavHostController,
        onChooseAddressNavigate: () -> Unit,
        onSettlementNavigate: (shopId: Long, addressId: Long?) -> Unit,
        parentRoute: String,
    ) {
        composable(
            route = this@MenuNavigation.route,
            arguments = this@MenuNavigation.arguments,
            deepLinks = this@MenuNavigation.deepLinks
        ) {
            val backStackEntry = remember {
                navController.getBackStackEntry(parentRoute)
            }
            val viewModel: MenuViewModel = hiltViewModel(backStackEntry)

            it.savedStateHandle.get<Long>("selected")?.let { selectedAddressId ->
                viewModel.selectedAddressId = selectedAddressId
            }

            MenuRoute(
                onChooseShopNavigate = {
                    navController.navigateToChooseShop(
                        shopIdKey = null,
                        navOptions = navOptions { launchSingleTop = true })
                },
                onChooseAddressNavigate = onChooseAddressNavigate,
                onSettlementNavigate = onSettlementNavigate,
                viewModel = viewModel,

                )
        }
    }
}


@Composable
fun MenuRoute(
    onSettlementNavigate: (shopId: Long, addressId: Long?) -> Unit,
    onChooseShopNavigate: () -> Unit,
    onChooseAddressNavigate: () -> Unit,
    viewModel: MenuViewModel
) {
    val selectedShopInfo by viewModel.selectedShopInfo.collectAsStateWithLifecycle()

    val uiState by viewModel.menuUiState.collectAsStateWithLifecycle()

    val commodityDetailUiState by viewModel.commodityDetailUIState.collectAsStateWithLifecycle()

    if (selectedShopInfo == null) {
        // 没有选择商店，导航到选择门店
        if (viewModel.cachedShopId == 0L) {
            Log.d("MenuRoute", "MenuRoute: cachedShopId: 0")
            onChooseShopNavigate()
        } else {
            viewModel.getShopDetailById(viewModel.cachedShopId)
        }
    }

    LaunchedEffect(key1 = selectedShopInfo) {
        Log.d("MenuInfo", "MenuRoute: fetch info(${selectedShopInfo})")
        viewModel.getShopGoodsWithCartInfo()
    }

    MenuScreen(
        uiState = uiState,
        commodityDetailUiState = commodityDetailUiState,
        onSettlementNavigate = { shopId: Long ->
            onSettlementNavigate(shopId, viewModel.selectedAddressId)
        },
        onSearchNavigation = {

        },
        onChooseShopNavigate = onChooseShopNavigate,
        onCommodityDetailShow = {
            viewModel.getCommodityDetail(it)
        },
        onCommodityDetailDismiss = {
            viewModel.hideCommodityDetail()
        },
        onAddCommodityToCart = { commodityId, addCount ->
            viewModel.addCommodityToCart(commodityId, addCount)
        },
        onChooseAddressNavigate = {
            if (viewModel.selectedAddressId == null) {
                onChooseAddressNavigate()
            }
        },
        onCartCommodityCountIncrease = { itemId: Long, number: Int ->
            viewModel.changeCartItemCount(itemId, number)
        },
        onCartCommodityCountDecrease = { itemId: Long, number: Int ->
            viewModel.changeCartItemCount(itemId, number)
        },
        onCartCommodityDeleteRequest = { itemId: Long ->
            viewModel.changeCartItemCount(itemId, 0)
        },
        onCartClearRequest = viewModel::clearCart
    )
}