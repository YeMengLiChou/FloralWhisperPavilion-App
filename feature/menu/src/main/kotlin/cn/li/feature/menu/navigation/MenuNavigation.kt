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

            MenuRoute(
                onChooseShopNavigate = {
                    navController.navigateToChooseShop(
                        shopIdKey = null,
                        navOptions = navOptions { launchSingleTop = true })
                },
                viewModel = viewModel,
            )
        }
    }
}


@Composable
fun MenuRoute(
    onChooseShopNavigate: () -> Unit,
    viewModel: MenuViewModel
) {
    val selectedShopInfo by viewModel.selectedShopInfo.collectAsStateWithLifecycle()

    val uiState by viewModel.menuUiState.collectAsStateWithLifecycle()

    // 没有选择商店，导航到选择门店
    if (selectedShopInfo == null) {
        onChooseShopNavigate()
    }

    SideEffect {
        Log.d("Test", "MenuRoute: ${viewModel.selectedShopInfo.value}")
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.getShopGoodsWithCartInfo()
    }

    MenuScreen(
        uiState = uiState,
        onSettlementNavigate = {
            // TODO：下单界面
        },
        onSearchNavigation = {

        },
        onChooseShopNavigate = onChooseShopNavigate
    )
}