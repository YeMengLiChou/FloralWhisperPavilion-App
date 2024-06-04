package cn.li.feature.mine.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import cn.li.feature.mine.UserMineViewModel
import cn.li.feature.mine.navigation.UserAddressManageNavigation.navigateToAddressManage
import cn.li.feature.mine.navigation.UserInfoNavRoute.navigateToUserInfo
import cn.li.feature.mine.ui.MineScreen
import cn.li.model.NavigationRoute
import cn.li.model.constant.DEEP_LINK_PREFIX
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/**
 * **我的** 界面 导航路由
 * */
internal object MineNavigationRoute : NavigationRoute {
    override val routePrefix: String
        get() = "user_mine"
    override val deepLinkPrefix: String
        get() = "$DEEP_LINK_PREFIX/${routePrefix}"


    /**
     * 导航到 **我的** 界面
     * */
    fun NavHostController.navigateToMine(navOptions: NavOptions) = navigate(route, navOptions)

    @SuppressLint("UnrememberedGetBackStackEntry")
    fun NavGraphBuilder.mineScreen(
        navController: NavHostController,
        onLoginNavigate: () -> Unit,
        parentRoute: String,
    ) {
        composable(
            route = this@MineNavigationRoute.route,
            arguments = this@MineNavigationRoute.arguments,
            deepLinks = this@MineNavigationRoute.deepLinks
        ) {
            val backStackEntry = remember(it) {
                navController.getBackStackEntry(parentRoute)
            }
            val viewModel: UserMineViewModel = hiltViewModel(backStackEntry)
            MineRoute(
                onPullRefresh = {},
                onUserInfoNavigation = {
                    navController.navigateToUserInfo(options = navOptions {
                        launchSingleTop = true
                        restoreState = true
                    })
                },
                onAddressManagementNavigation = {
                    navController.navigateToAddressManage(
                        selectedKey = null, // 无需选中操作
                        options = navOptions {
                            launchSingleTop = true
                            restoreState = true
                        })
                },
                onSettingsNavigation = {},
                onLoginNavigate = onLoginNavigate,
                viewModel = viewModel
            )
        }
    }
}


@Composable
private fun MineRoute(
    onLoginNavigate: () -> Unit,
    onPullRefresh: () -> Unit,
    onUserInfoNavigation: () -> Unit,
    onAddressManagementNavigation: () -> Unit,
    onSettingsNavigation: () -> Unit,
    viewModel: UserMineViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var refreshing by remember {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()

    MineScreen(
        uiState = uiState,
        refreshing = refreshing,
        onPullRefresh = {
            // TODO: 实现刷新操作
            scope.launch {
                refreshing = true
                delay(1000)
                refreshing = false
            }
        },
        onUserInfoNavigation = onUserInfoNavigation,
        onAddressManagementNavigation = onAddressManagementNavigation,
        onSettingsNavigation = onSettingsNavigation,
        onLoginNavigate = onLoginNavigate
    )
}