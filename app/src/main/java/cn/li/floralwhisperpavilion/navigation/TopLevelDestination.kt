package cn.li.floralwhisperpavilion.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material.icons.rounded.Bookmarks
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material.icons.rounded.Store
import androidx.compose.ui.graphics.vector.ImageVector
import cn.li.feature.employee.order.navigation.EMPLOYEE_ORDER_ROUTE
import cn.li.feature.home.navigation.HOME_ROUTE
import cn.li.feature.menu.navigation.MENU_ROUTE
import cn.li.feature.mine.navigation.MineNavigationRoute
import cn.li.feature.shop.navigation.SHOP_ROUTE
import cn.li.feature.userorder.navigation.USER_ORDER_ROUTE
import cn.li.model.constant.AppRole


const val PERMISSION_USER = 1

const val PERMISSION_EMPLOYEE = 2

/**
 * 顶层导航目的地
 * */
enum class TopLevelDestination(
    val selectIcon: ImageVector,
    val unselectIcon: ImageVector,
    val iconText: String,
    val role: Int,
    val route: String,
    val rank: Int,
) {
    /**
     * 用户-首页
     * */
    HOME(
        selectIcon = Icons.Rounded.Home,
        unselectIcon = Icons.Outlined.Home,
        iconText = "首页",
        role = AppRole.USER,
        route = HOME_ROUTE,
        rank = 1,
    ),

    /**
     * 用户-点单
     * */
    MENU(
        selectIcon = Icons.Rounded.ShoppingCart,
        unselectIcon = Icons.Outlined.ShoppingCart,
        iconText = "点单",
        role = AppRole.USER,
        route = MENU_ROUTE,
        rank = 1.shl(1),
    ),

    /**
     * 用户-订单
     * */
    USER_ORDER(
        selectIcon = Icons.Rounded.Bookmarks,
        unselectIcon = Icons.Outlined.Bookmarks,
        iconText = "订单",
        role = AppRole.USER,
        route = USER_ORDER_ROUTE,
        rank = 1.shl(2),

        ),

    /**
     * 用户-我的
     * */
    USER_MINE(
        selectIcon = Icons.Rounded.Person,
        unselectIcon = Icons.Outlined.Person,
        iconText = "我的",
        role = AppRole.USER,
        route = MineNavigationRoute.route,
        rank = 1.shl(3),
    ),

    /**
     * 店铺订单
     * */
    EMPLOYEE_ORDER(
        selectIcon = Icons.Rounded.Bookmarks,
        unselectIcon = Icons.Outlined.Bookmarks,
        iconText = "订单",
        role = AppRole.EMPLOYEE,
        route = EMPLOYEE_ORDER_ROUTE,
        rank = 1.shl(4),
    ),

    /**
     * 店铺情况
     * */
    SHOP(
        selectIcon = Icons.Rounded.Store,
        unselectIcon = Icons.Outlined.Store,
        iconText = "店铺",
        role = AppRole.EMPLOYEE,
        route = SHOP_ROUTE,
        rank = 1.shl(5)
    )
}