package cn.li.feature.userorder.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.paging.compose.collectAsLazyPagingItems
import cn.li.feature.userorder.UserOrderViewModel
import cn.li.feature.userorder.ui.UserOrderScreen
import cn.li.model.NavigationRoute
import cn.li.model.constant.DEEP_LINK_PREFIX

object UserOrderNavigation : NavigationRoute {
    override val routePrefix: String
        get() = "user_order"
    override val deepLinkPrefix: String
        get() = "$DEEP_LINK_PREFIX/$routePrefix"


    fun NavHostController.navigateToUserOrder(navOptions: NavOptions) = navigate(route, navOptions)

    fun NavGraphBuilder.userOrderScreen(
        navController: NavHostController
    ) {
        composable(
            route = this@UserOrderNavigation.route,
            arguments = arguments,
            deepLinks = deepLinks
        ) {

            UserOrderRoute()
        }
    }
}

@Composable
fun UserOrderRoute(
    viewModel: UserOrderViewModel = hiltViewModel()
) {
    val uiState by viewModel.userOrderUiState.collectAsState()


    val uncompletedOrderItems by rememberUpdatedState(newValue = viewModel.uncompletedOrder.collectAsLazyPagingItems())

    val completedOrderItems by rememberUpdatedState(newValue = viewModel.completedOrder.collectAsLazyPagingItems())

    var completedFetched by remember {
        mutableStateOf(false)
    }
    var uncompletedFetched by remember {
        mutableStateOf(false)
    }

    UserOrderScreen(
        uiState = uiState,
        completeItems = completedOrderItems,
        uncompletedItems = uncompletedOrderItems,
        onUncompletedOrderSwitch = {
            if (!completedFetched) {
                viewModel.getCompletedOrder()
                completedFetched = true
            }
        },
        onHistoryOrderSwitch = {
            if (!uncompletedFetched) {
                viewModel.getUncompletedOrder()
                uncompletedFetched = true
            }
        },
    )
}