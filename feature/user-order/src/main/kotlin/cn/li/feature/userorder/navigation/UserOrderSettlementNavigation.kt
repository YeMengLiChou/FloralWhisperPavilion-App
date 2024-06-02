package cn.li.feature.userorder.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import cn.li.feature.userorder.UserOrderViewModel
import cn.li.feature.userorder.ui.UserSettlementOrderScreen
import cn.li.model.NavigationRoute
import cn.li.model.constant.DEEP_LINK_PREFIX

object UserOrderSettlementNavigation : NavigationRoute {

    const val LONG_NULL = 1L

    data object Arguments {
        const val SHOP_ID = "shopId"
        const val ADDRESS_ID = "addressId"
    }

    override val routePrefix: String
        get() = "user_order_settle"
    override val deepLinkPrefix: String
        get() = "${DEEP_LINK_PREFIX}/${routePrefix}"


    override val arguments: List<NamedNavArgument>
        get() = listOf(
            // 商店id
            navArgument(Arguments.SHOP_ID) {
                nullable = false
                type = NavType.LongType
            },
            // 地址id
            navArgument(Arguments.ADDRESS_ID) {
                nullable = false
                defaultValue = LONG_NULL
                type = NavType.LongType
            }
        )

    fun NavHostController.navigateToOrderSettlement(
        shopId: Long,
        addressId: Long = LONG_NULL,
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
        ) { entry ->
            val shopId = entry.arguments?.getLong(Arguments.SHOP_ID)!!
            val addressId =
                entry.arguments?.getLong(Arguments.ADDRESS_ID).takeIf { it != LONG_NULL }

            UserOrderSettlementRoute(
                shopId = shopId,
                addressId = addressId,
                onBackClick = navController::popBackStack
            )
        }
    }
}


@Composable
internal fun UserOrderSettlementRoute(
    shopId: Long,
    addressId: Long?,
    onBackClick: () -> Unit,
    viewModel: UserOrderViewModel = hiltViewModel()
) {
    val uiState by viewModel.userOrderSettlementUiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = shopId, key2 = addressId) {
        // 获取数据
        viewModel.getOrderSettlementInfo(shopId = shopId, addressId = addressId)
    }
    UserSettlementOrderScreen(
        uiState = uiState,
        onSettlementClick = {
            viewModel.submitUserOrder(it)
        },
        onBackClick = onBackClick,
        onOrderSubmitNavigate = { /*TODO*/}
    )
}