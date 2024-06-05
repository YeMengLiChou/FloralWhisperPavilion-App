package cn.li.feature.userorder.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import cn.li.feature.userorder.UserOrderViewModel
import cn.li.feature.userorder.ui.UserOrderDetailScreen
import cn.li.model.NavigationRoute
import cn.li.model.constant.DEEP_LINK_PREFIX

object UserOrderDetailNavigation : NavigationRoute {
    override val routePrefix: String
        get() = "order_detail"
    override val deepLinkPrefix: String
        get() = "$DEEP_LINK_PREFIX/${routePrefix}"

    fun NavHostController.navigateToUserOrderDetail(navOptions: NavOptions) =
        navigate(route, navOptions)

    fun NavGraphBuilder.userOrderDetailScreen(
        navHostController: NavHostController,
    ) {
        composable(
            route = this@UserOrderDetailNavigation.route,
            arguments = arguments,
            deepLinks = deepLinks
        ) {
            val backStackEntry = remember(it) {
                navHostController.previousBackStackEntry!!
            }
            val viewModel: UserOrderViewModel = hiltViewModel(backStackEntry)

            UserOrderDetailNavRoute(
                onBackClick = navHostController::popBackStack,
                viewModel = viewModel
            )
        }
    }
}


@Composable
private fun UserOrderDetailNavRoute(
    onBackClick: () -> Unit,
    viewModel: UserOrderViewModel = hiltViewModel()
) {

    UserOrderDetailScreen(
        shouldSettlement = false,
        onSettlementClick = { /*TODO*/ },
        item = viewModel.getClickOrderItem()!!,
        onBackClick = onBackClick
    )
}