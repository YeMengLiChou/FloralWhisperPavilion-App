package cn.li.feature.mine.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.navigation
import cn.li.feature.mine.navigation.MineNavigationRoute.mineScreen
import cn.li.feature.mine.navigation.UserAddressAddNavigation.addAddressScreen
import cn.li.feature.mine.navigation.UserAddressManageNavigation.addressManageScreen
import cn.li.feature.mine.navigation.UserInfoNavRoute.userInfoScreen
import cn.li.model.NavigationRoute
import cn.li.model.constant.DEEP_LINK_PREFIX


/**
 * 嵌套路由声明
 * */
object MineNestedNavRoute : NavigationRoute {
    override val routePrefix: String
        get() = "mine_screen"
    override val deepLinkPrefix: String
        get() = "${DEEP_LINK_PREFIX}/mine_screen"

    fun NavHostController.navigateToMineGraph(navOptions: NavOptions) =
        navigate(this@MineNestedNavRoute.route, navOptions)

    fun NavGraphBuilder.nestedMineNavGraph(
        navHostController: NavHostController,
        onLoginNavigate: () -> Unit,
    ) {
        navigation(
            startDestination = MineNavigationRoute.route,
            route = this@MineNestedNavRoute.route,
            arguments = this@MineNestedNavRoute.arguments,
            deepLinks = this@MineNestedNavRoute.deepLinks
        ) {

            mineScreen(
                navController = navHostController,
                parentRoute = this@MineNestedNavRoute.route,
                onLoginNavigate = onLoginNavigate
            )
            userInfoScreen(
                navController = navHostController,
                parentRoute = this@MineNestedNavRoute.route
            )
            addressManageScreen(
                navHostController = navHostController,
                parentRoute = this@MineNestedNavRoute.route
            )
            addAddressScreen(
                controller = navHostController,
                parentRoute = this@MineNestedNavRoute.route
            )
        }
    }
}




