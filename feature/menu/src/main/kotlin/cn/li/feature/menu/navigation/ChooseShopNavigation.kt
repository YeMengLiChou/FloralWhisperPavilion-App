package cn.li.feature.menu.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import cn.li.feature.menu.ui.MenuViewModel
import cn.li.feature.menu.ui.shop.ChooseShopScreen
import cn.li.model.NavigationRoute
import cn.li.model.constant.DEEP_LINK_PREFIX
import cn.li.network.dto.user.ShopItemDTO

object ChooseShopNavigation : NavigationRoute {
    data object Arguments {
        const val SHOP_ID_KEY = "shop_id_key"
    }

    override val routePrefix: String
        get() = "choose_shop"
    override val deepLinkPrefix: String
        get() = "$DEEP_LINK_PREFIX/$routePrefix"

    override val arguments: List<NamedNavArgument>
        get() = listOf(
            navArgument(Arguments.SHOP_ID_KEY) {
                nullable = true
            }
        )


    fun NavHostController.navigateToChooseShop(shopIdKey: String? = null, navOptions: NavOptions) {
        val route =
            mapOf(Arguments.SHOP_ID_KEY to shopIdKey).toRoute(this@ChooseShopNavigation.routePrefix)
        navigate(
            route = route,
            navOptions = navOptions
        )
    }

    @SuppressLint("UnrememberedGetBackStackEntry")
    fun NavGraphBuilder.chooseShopScreen(
        navController: NavHostController,
        parentRoute: String,
    ) {
        composable(
            route = this@ChooseShopNavigation.route,
            arguments = arguments,
            deepLinks = deepLinks
        ) {
            val backStackEntry = remember {
                navController.getBackStackEntry(parentRoute)
            }
            val viewModel: MenuViewModel = hiltViewModel(backStackEntry)

            ChooseShopRoute(
                onBackClick = navController::popBackStack,
                onSettlementClick = { shopInfo ->
                    viewModel.setSelectedShopInfo(shopInfo)
                    navController.popBackStack()
                },
                viewModel = viewModel
            )
        }
    }
}


@Composable
fun ChooseShopRoute(
    initSelectedShopId: Long? = null,
    onBackClick: () -> Unit,
    onSettlementClick: (shopInfo: ShopItemDTO) -> Unit,
    viewModel: MenuViewModel,
) {
    LaunchedEffect(key1 = initSelectedShopId) {
        viewModel.getShopList(0L)
    }

    val uiState by viewModel.chooseShopUiState.collectAsStateWithLifecycle()

    var selectedShopId by remember(initSelectedShopId) {
        mutableStateOf(initSelectedShopId)
    }

    ChooseShopScreen(
        uiState = uiState,
        selectedShopId = selectedShopId,
        onBackClick = onBackClick,
        onShopSelected = {
            if (selectedShopId == it) {
                selectedShopId = null
            } else {
                selectedShopId = it
            }
        },
        onRefresh = {
            viewModel.getShopList(delay = 1000)
        },
        onSettlementClick = onSettlementClick,

        )
}