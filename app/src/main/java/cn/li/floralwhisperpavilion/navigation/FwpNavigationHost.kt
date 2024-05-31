package cn.li.floralwhisperpavilion.navigation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import cn.li.feature.employee.order.navigation.employeeOrderScreen
import cn.li.feature.home.navigation.homeScreen
import cn.li.feature.login.navigation.LoginNavigationRoute
import cn.li.feature.login.navigation.LoginNavigationRoute.loginScreen
import cn.li.feature.login.navigation.LoginNavigationRoute.navigateToLogin
import cn.li.feature.login.navigation.RegisterNavigationRoute
import cn.li.feature.login.navigation.RegisterNavigationRoute.navigateToRegister
import cn.li.feature.login.navigation.RegisterNavigationRoute.registerScreen
import cn.li.feature.menu.navigation.MenuNestedNavGraph.menuNestedNavGraph
import cn.li.feature.mine.navigation.MineNestedNavRoute.nestedMineNavGraph
import cn.li.feature.mine.navigation.UserAddressManageNavigation.navigateToAddressManage
import cn.li.feature.shop.navigation.shopScreen
import cn.li.feature.userorder.navigation.UserOrderNavigation.userOrderScreen

private const val TAG = "FwpNavigationHost"

private fun logNavigation(from: String, to: String) {
    Log.d(TAG, "navigate: $from -> $to ")
}

@SuppressLint("RestrictedApi")
private fun logNavigationCurrentBackStack(navController: NavHostController) {
    val backstack = navController.currentBackStack.value
    val msg = StringBuilder()
    msg.append("currentBackStack:\n")
    for ((idx, entry) in backstack.reversed().withIndex()) {
        msg.append("|${if (idx == 0) "=>" else "$idx."} route: ${entry.destination.route}, id: ${entry.destination.id}\n")
    }
    Log.d(TAG, msg.toString())
}

/**
 * 全局导航图
 * */
@SuppressLint("RestrictedApi")
@Composable
fun FwpNavigationHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        homeScreen(onLoginNavigation = {
            navController.navigateToLogin(
                username = "",
                password = "",
                navOptions = emptyNavOptions
            )
        })

        userOrderScreen(navController)
        menuNestedNavGraph(navController, onChooseAddressNavigate = {
            navController.navigateToAddressManage(
                selectedKey = "selected",
                options = navOptions {
                    launchSingleTop = true
                }
            )
        })

        employeeOrderScreen()
        shopScreen()

        nestedMineNavGraph(navController)

        loginScreen(
            onBackClick = navController::popBackStack,
            onRegisterNavigation = { username ->
                navController.navigateAvoidCircle(RegisterNavigationRoute.route) {
                    navigateToRegister(
                        username = username,
                        navOptions = it
                    )
                }
            }
        )
        registerScreen(
            onBackClick = navController::popBackStack,
            onLoginNavigate = { username, password ->
                navController.navigateAvoidCircle(LoginNavigationRoute.route) {
                    navigateToLogin(
                        username = username,
                        password = password,
                        navOptions = it
                    )
                }
            }
        )
    }
}


/**
 * 导航，防止循环创建多个实例
 *
 * 通过移除当前路由到已有的路由（包扩自身，由 [inclusive] 决定）之间的所有路由
 * @param destRoute 目标路由
 * @param inclusive 是否移除已有的目标路由
 * @param navigation 导航操作
 * */
@SuppressLint("RestrictedApi")
private inline fun NavHostController.navigateAvoidCircle(
    destRoute: String,
    inclusive: Boolean = true,
    navigation: NavHostController.(NavOptions) -> Unit,
) {
    navigation(navOptions {
        popUpTo(destRoute) {
            this.inclusive = inclusive
            // note: 该项启用，会导致部分无法导航，原因未知
//            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    })
}


private val emptyNavOptions = navOptions {
    launchSingleTop = true
    restoreState = true
}