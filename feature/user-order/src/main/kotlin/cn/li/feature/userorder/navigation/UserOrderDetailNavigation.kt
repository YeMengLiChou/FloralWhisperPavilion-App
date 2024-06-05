package cn.li.feature.userorder.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import cn.li.feature.userorder.UserOrderViewModel
import cn.li.feature.userorder.ui.UserOrderDetailScreen
import cn.li.model.NavigationRoute
import cn.li.model.constant.DEEP_LINK_PREFIX

object UserOrderDetailNavigation : NavigationRoute {

    data object Arguments {
        const val ORDER_ID = "orderId"
    }

    override val routePrefix: String
        get() = "order_detail"
    override val deepLinkPrefix: String
        get() = "$DEEP_LINK_PREFIX/${routePrefix}"


    override val arguments: List<NamedNavArgument>
        get() = listOf(
            navArgument(Arguments.ORDER_ID) {
                defaultValue = -1L
                type = NavType.LongType
            }
        )

    fun NavHostController.navigateToUserOrderDetail(orderId: Long? = null, navOptions: NavOptions) {
        val route =
            mapOf(Arguments.ORDER_ID to orderId).toRoute(this@UserOrderDetailNavigation.routePrefix)
        navigate(route, navOptions)
    }

    fun NavGraphBuilder.userOrderDetailScreen(
        navHostController: NavHostController,
    ) {
        composable(
            route = this@UserOrderDetailNavigation.route,
            arguments = arguments,
            deepLinks = deepLinks
        ) {
            val backStackEntry = remember(it) {
                navHostController.previousBackStackEntry
            }
            val viewModel: UserOrderViewModel =
                if (backStackEntry != null) hiltViewModel(backStackEntry) else hiltViewModel()
            val orderId = it.arguments?.getLong(Arguments.ORDER_ID)!!
            if (orderId != -1L) {
                viewModel.getOrderDetailByOrderId(orderId = orderId)
            }

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

    val uiState by viewModel.userOrderDetailUiState.collectAsState()

    UserOrderDetailScreen(
        uiState = uiState,
        onBackClick = onBackClick
    )
}