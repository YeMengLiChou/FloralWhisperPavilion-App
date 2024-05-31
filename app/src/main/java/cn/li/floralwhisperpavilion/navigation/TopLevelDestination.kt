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
import cn.li.feature.menu.navigation.MenuNestedNavGraph
import cn.li.feature.mine.navigation.MineNestedNavRoute
import cn.li.feature.shop.navigation.SHOP_ROUTE
import cn.li.feature.userorder.navigation.UserOrderNavigation
import cn.li.model.constant.AppRole

/**
 * 顶层导航目的地
 * @param selectIcon 选中时的图标
 * @param unselectIcon 未选中时的图标
 * @param iconText 底部栏显示的文本
 * @param role 当 [AppRole] 匹配时才能显示
 * @param route 对应的路由名称
 * @param rank 顺序
 * @param isNestedGraph 是否为嵌套图
 * */
enum class TopLevelDestination(
    val selectIcon: ImageVector,
    val unselectIcon: ImageVector,
    val iconText: String,
    val role: Int,
    val route: String,
    val rank: Int,
    val isNestedGraph: Boolean = false,
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
        route = MenuNestedNavGraph.route,
        rank = 1.shl(1),
        isNestedGraph = true,
    ),

    /**
     * 用户-订单
     * */
    USER_ORDER(
        selectIcon = Icons.Rounded.Bookmarks,
        unselectIcon = Icons.Outlined.Bookmarks,
        iconText = "订单",
        role = AppRole.USER,
        route = UserOrderNavigation.route,
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
        route = MineNestedNavRoute.route,
        rank = 1.shl(3),
        isNestedGraph = true,
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