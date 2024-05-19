package cn.li.feature.shop.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import cn.li.feature.shop.ui.ShopScreen

const val SHOP_ROUTE = "shop_route"

/**
 * 导航到 **点单** 界面
 * */
fun NavController.navigateToShop(navOptions: NavOptions) = navigate(SHOP_ROUTE, navOptions)


fun NavGraphBuilder.shopScreen() {
    composable(
        route = SHOP_ROUTE
    ) {
        ShopRoute()
    }
}

@Composable
fun ShopRoute() {
    ShopScreen()
}