package cn.li.feature.menu.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.navigation
import cn.li.feature.menu.navigation.ChooseShopNavigation.chooseShopScreen
import cn.li.feature.menu.navigation.MenuNavigation.menuScreen
import cn.li.model.NavigationRoute
import cn.li.model.constant.DEEP_LINK_PREFIX

/**
 * 点单 界面的导航图
 * */
object MenuNestedNavGraph : NavigationRoute {
    override val routePrefix: String
        get() = "menu_screen"
    override val deepLinkPrefix: String
        get() = "$DEEP_LINK_PREFIX/$routePrefix"


    fun NavGraphBuilder.menuNestedNavGraph(
        navController: NavHostController,
        onChooseAddressNavigate: () -> Unit,
        onLoginNavigate: () -> Unit,
        onSettlementNavigate: (shopId: Long, addressId: Long?) -> Unit
    ) {
        navigation(
            startDestination = MenuNavigation.route,
            route = this@MenuNestedNavGraph.route,
            arguments = this@MenuNestedNavGraph.arguments,
            deepLinks = this@MenuNestedNavGraph.deepLinks
        ) {

            menuScreen(
                navController = navController,
                parentRoute = this@MenuNestedNavGraph.route,
                onChooseAddressNavigate = onChooseAddressNavigate,
                onSettlementNavigate = onSettlementNavigate,
                onLoginNavigate = onLoginNavigate
            )
            chooseShopScreen(
                navController = navController,
                parentRoute = this@MenuNestedNavGraph.route
            )

        }

    }
}