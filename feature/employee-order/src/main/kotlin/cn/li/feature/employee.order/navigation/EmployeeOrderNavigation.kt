package cn.li.feature.employee.order.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import cn.li.feature.employee.order.ui.EmployeeOrderScreen

const val EMPLOYEE_ORDER_ROUTE = "employee_order_route"

/**
 * 导航到 **店铺订单** 界面
 * */
fun NavController.navigateToEmployeeOrder(navOptions: NavOptions) = navigate(EMPLOYEE_ORDER_ROUTE, navOptions)


fun NavGraphBuilder.employeeOrderScreen() {
    composable(
        route = EMPLOYEE_ORDER_ROUTE
    ) {
        EmployeeOrderRoute()
    }
}

@Composable
fun EmployeeOrderRoute() {
    EmployeeOrderScreen()
}