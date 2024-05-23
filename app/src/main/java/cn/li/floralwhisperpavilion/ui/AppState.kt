package cn.li.floralwhisperpavilion.ui

import android.util.Log
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import cn.li.common.ext.cast
import cn.li.data.util.NetworkMonitor
import cn.li.feature.employee.order.navigation.navigateToEmployeeOrder
import cn.li.feature.home.navigation.navigateToHome
import cn.li.feature.menu.navigation.navigateToMenu
import cn.li.feature.mine.navigation.MineNestedNavRoute.navigateToMineGraph
import cn.li.feature.shop.navigation.navigateToShop
import cn.li.feature.userorder.navigation.navigateToUserOrder
import cn.li.floralwhisperpavilion.navigation.TopLevelDestination
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * App 状态信息
 * @param navController 路由导航
 * @param coroutineScope
 * @param windowSizeClass 当前屏幕窗口的大小类型
 * @param networkMonitor 网络监视器
 * @param topLevelDestinations 顶层目的地，根据登录的账号修改
 * */
@Stable
class AppState(
    val navController: NavHostController,
    coroutineScope: CoroutineScope,
    val windowSizeClass: WindowSizeClass,
    networkMonitor: NetworkMonitor,
    val topLevelDestinations: List<TopLevelDestination>
) {
    /**
     * 当前的目的地
     * */
    val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState()
            .value
            ?.destination

    /**
     * 当前的顶层目的地
     * */
    val currentTopLevelDestination: TopLevelDestination?
        @Composable get() = when (currentDestination?.route) {

            else -> null
        }

    /**
     * 是否已经与网络断联
     * */
    val isOffline = networkMonitor.isOnline
        .map(Boolean::not)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    /**
     * 是否显示底部栏，如果宽度较小则显示
     * */
    val shouldShowBottomBar
        get() = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact

    /**
     * 是否显示侧边栏，如果宽度较大则显示，比如平板
     * */
    val shouldShowBottomRail
        get() = !shouldShowBottomBar

    /**
     * 底部导航栏/侧边栏中的所有路由
     * */
    private val bottomBarRoutes: Set<String>
        get() = HashSet<String>().apply {
            // 判断是否已经初始化，即调用 `setGraph` 后
            if (navController.currentDestination == null) return@apply
            // 加载底部栏的所有路由项
            topLevelDestinations.forEach {
                if (it.isNestedGraph) {
                    // 将嵌套图的startDestination拿出来
                    val route = navController.graph
                        .findNode(route = it.route)
                        .cast<NavGraph>()!!
                        .findStartDestination()
                        .route!!
                    this.add(route)
                } else {
                    this.add(it.route)
                }
            }
        }.toSet()

    /**
     * 判断是否应该显示
     * */
    val shouldShowBottomBarByNavigation
        @Composable get() = bottomBarRoutes.contains(currentDestination?.route).apply {
        }


    /**
     * 导航到对应的目的地
     * */
    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
        val topLevelNavOptions = navOptions {
            // 回到开始的地方，避免大量实例累积
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            // 保证目标只有一个实例
            launchSingleTop = true
            // 恢复状态
            restoreState = true
        }
        Log.d("AppState", "navigateToTopLevelDestination: $topLevelDestination")
        when (topLevelDestination) {
            TopLevelDestination.HOME -> navController.navigateToHome(topLevelNavOptions)
            TopLevelDestination.MENU -> navController.navigateToMenu(topLevelNavOptions)
            TopLevelDestination.USER_ORDER -> navController.navigateToUserOrder(topLevelNavOptions)
            TopLevelDestination.USER_MINE -> navController.navigateToMineGraph(topLevelNavOptions)
            TopLevelDestination.EMPLOYEE_ORDER -> navController.navigateToEmployeeOrder(
                topLevelNavOptions
            )

            TopLevelDestination.SHOP -> navController.navigateToShop(topLevelNavOptions)
        }

    }
}


/**
 * [remember] 当前 [AppState]
 * @param coroutineScope
 * @param windowSizeClass
 * @param networkMonitor
 * @param navController
 * @param topLevelDestinations
 * */
@Composable
fun rememberAppState(
    windowSizeClass: WindowSizeClass,
    networkMonitor: NetworkMonitor,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController(),
    topLevelDestinations: List<TopLevelDestination>
): AppState {
    return remember(
        navController,
        windowSizeClass,
        networkMonitor,
        topLevelDestinations,
        coroutineScope
    ) {
        AppState(
            navController = navController,
            coroutineScope = coroutineScope,
            windowSizeClass = windowSizeClass,
            networkMonitor = networkMonitor,
            topLevelDestinations = topLevelDestinations
        )
    }
}
