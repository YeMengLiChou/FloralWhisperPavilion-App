package cn.li.feature.mine.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import cn.li.feature.mine.ui.MineScreen

const val MINE_ROUTE = "mine_route"

/**
 * 导航到 **我的** 界面
 * */
fun NavController.navigateToMine(navOptions: NavOptions) = navigate(MINE_ROUTE, navOptions)


fun NavGraphBuilder.mineScreen() {
    composable(
        route = MINE_ROUTE
    ) {
        MineRoute()
    }
}

@Composable
fun MineRoute() {
    MineScreen()
}