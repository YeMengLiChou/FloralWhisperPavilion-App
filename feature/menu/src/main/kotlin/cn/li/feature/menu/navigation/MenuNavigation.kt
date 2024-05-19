package cn.li.feature.menu.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import cn.li.feature.menu.ui.MenuScreen

const val MENU_ROUTE = "menu_route"

/**
 * 导航到 **点单** 界面
 * */
fun NavController.navigateToMenu(navOptions: NavOptions) = navigate(MENU_ROUTE, navOptions)


fun NavGraphBuilder.menuScreen() {
    composable(
        route = MENU_ROUTE
    ) {
        MenuRoute()
    }
}

@Composable
fun MenuRoute() {
    MenuScreen()
}