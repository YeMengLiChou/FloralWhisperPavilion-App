package cn.li.feature.mine.navigation

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

    fun NavGraphBuilder.mineScreen(
        navController: NavHostController
    ) {
        composable(
            route = this@MineNavigationRoute.route,
            arguments = this@MineNavigationRoute.arguments,
            deepLinks = this@MineNavigationRoute.deepLinks
        ) {
            MineRoute(
                onPullRefresh = {},
                onUserInfoNavigation = {
                    navController.navigateToUserInfo(options = navOptions {
                        launchSingleTop = true
                        restoreState = true
                    })
                },
                onAddressManagementNavigation = {},
                onSettingsNavigation = {}
            )
        }
    }
}


@Composable
private fun MineRoute(
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
            scope.launch {
                refreshing = true
                delay(1000)
                refreshing = false
            }
        },
        onUserInfoNavigation = onUserInfoNavigation,
        onAddressManagementNavigation = onAddressManagementNavigation,
        onSettingsNavigation = onSettingsNavigation
    )
}