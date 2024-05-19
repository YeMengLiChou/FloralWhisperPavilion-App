package cn.li.feature.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val HOME_ROUTE ="home_route"

/**
 * 导航到 **首页** 界面
 * */
fun NavController.navigateToHome(navOptions: NavOptions) = navigate(HOME_ROUTE, navOptions)

/**
 * 声明主页的路由
 * */
fun NavGraphBuilder.homeScreen(
) {
    composable(
        route = HOME_ROUTE,
    ) {
        HomeRoute()
    }
}

