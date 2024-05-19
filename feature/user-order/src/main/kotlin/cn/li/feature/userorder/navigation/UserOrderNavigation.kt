package cn.li.feature.userorder.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import cn.li.feature.userorder.ui.UserOrderScreen

const val USER_ORDER_ROUTE = "user_order_route"

fun NavController.navigateToUserOrder(navOptions: NavOptions) = navigate(USER_ORDER_ROUTE, navOptions)

fun NavGraphBuilder.userOrderScreen() {
    composable(route = USER_ORDER_ROUTE) {
        UserOrderRoute()
    }
}
@Composable
fun UserOrderRoute() {
    UserOrderScreen()
}