package cn.li.feature.menu.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
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
                viewModel = viewModel,
            )
        }
    }
}


@Composable
fun MenuRoute(
    viewModel: MenuViewModel
) {
    MenuScreen(onSettlementNavigate = {}, onSearchNavigation = {})
}