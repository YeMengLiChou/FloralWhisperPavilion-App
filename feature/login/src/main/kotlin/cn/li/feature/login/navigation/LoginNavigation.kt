package cn.li.feature.login.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val LOGIN_ROUTE ="login_route"

/**
 * 导航到注册界面
 * */
fun NavController.navigateToLogin(navOptions: NavOptions) = navigate(LOGIN_ROUTE, navOptions)

fun NavGraphBuilder.loginScreen(
    onBackClick: () -> Unit,
) {
    composable(
        route = LOGIN_ROUTE,
    ) {
        LoginRoute(onPressBack = onBackClick)
    }
}

