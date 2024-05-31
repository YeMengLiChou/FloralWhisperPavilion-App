package cn.li.feature.userorder.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import cn.li.feature.userorder.UserOrderViewModel
import cn.li.feature.userorder.ui.UserSettlementOrderScreen
import cn.li.model.NavigationRoute
import cn.li.model.constant.DEEP_LINK_PREFIX

object UserOrderSettlementNavigation : NavigationRoute {

    data object Arguments {
        const val SHOP_ID = "shopId"
        const val ADDRESS_ID = "addressId"
//        const val
    }

    override val routePrefix: String
        get() = "user_order_settle"
    override val deepLinkPrefix: String
        get() = "${DEEP_LINK_PREFIX}/${routePrefix}"


    override val arguments: List<NamedNavArgument>
        get() = listOf(
            navArgument(Arguments.SHOP_ID) {
                nullable = false
            },
            navArgument(Arguments.ADDRESS_ID) {
                nullable = false
            }
        )

    fun NavHostController.navigateToOrderSettlement(
        shopId: Long,
        addressId: Long,
        navOptions: NavOptions
    ) {
        val route = mapOf(
            Arguments.SHOP_ID to shopId,
            Arguments.ADDRESS_ID to addressId
        ).toRoute(this@UserOrderSettlementNavigation.routePrefix)
        navigate(route, navOptions)
    }

    fun NavGraphBuilder.userOrderSettlementScreen(
        navController: NavHostController
    ) {
        composable(
            route = this@UserOrderSettlementNavigation.route,
            arguments = arguments,
            deepLinks = deepLinks
        ) {

        }
    }

}


@Composable
internal fun UserOrderSettlementRoute(
    viewModel: UserOrderViewModel = hiltViewModel()
) {
    val uiState by viewModel.userOrderSettlementUiState.collectAsStateWithLifecycle()
    UserSettlementOrderScreen(uiState = uiState, onSettlementClick = {
//        viewModel.submitOrder()
    })

}