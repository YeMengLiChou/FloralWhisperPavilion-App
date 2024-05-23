package cn.li.feature.mine.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import cn.li.feature.mine.UserMineUiState
import cn.li.feature.mine.UserMineViewModel
import cn.li.feature.mine.ui.MineScreen
import cn.li.model.NavigationRoute
import cn.li.model.constant.DEEP_LINK_PREFIX
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * **我的** 界面 导航路由
 * */
object MineNavigationRoute : NavigationRoute {
    override val routePrefix: String
        get() = "user_mine"
    override val deepLinkPrefix: String
        get() = "$DEEP_LINK_PREFIX/user_mine"


    /**
     * 导航到 **我的** 界面
     * */
    fun NavController.navigateToMine(navOptions: NavOptions) = navigate(route, navOptions)

    fun NavGraphBuilder.mineScreen() {
        composable(
            route = this@MineNavigationRoute.route,
            arguments = this@MineNavigationRoute.arguments,
            deepLinks = this@MineNavigationRoute.deepLinks
        ) {
            MineRoute()
        }
    }
}


@Composable
fun MineRoute(
    viewModel: UserMineViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
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
    )
}