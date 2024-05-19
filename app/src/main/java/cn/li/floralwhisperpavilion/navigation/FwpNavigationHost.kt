package cn.li.floralwhisperpavilion.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import cn.li.feature.employee.order.navigation.employeeOrderScreen
import cn.li.feature.home.navigation.homeScreen
import cn.li.feature.login.navigation.loginScreen
import cn.li.feature.menu.navigation.menuScreen
import cn.li.feature.mine.navigation.mineScreen
import cn.li.feature.shop.navigation.shopScreen
import cn.li.feature.userorder.navigation.userOrderScreen


/**
 * 全局导航图
 * */
@Composable
fun FwpNavigationHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        homeScreen()
        loginScreen(onBackClick = navController::popBackStack)
        menuScreen()
        userOrderScreen()
        mineScreen()
        employeeOrderScreen()
        shopScreen()
    }
}


