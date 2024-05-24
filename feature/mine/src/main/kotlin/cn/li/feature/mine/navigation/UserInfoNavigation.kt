package cn.li.feature.mine.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import cn.li.feature.mine.UserMineViewModel
import cn.li.feature.mine.ui.info.UserInfoScreen
import cn.li.model.NavigationRoute
import cn.li.model.constant.DEEP_LINK_PREFIX

/**
 * 用户信息 界面路由项
 * */
object UserInfoNavRoute : NavigationRoute {
    override val routePrefix: String
        get() = "user_mine/info"
    override val deepLinkPrefix: String
        get() = "${DEEP_LINK_PREFIX}/${routePrefix}"

    fun NavHostController.navigateToUserInfo(options: NavOptions) =
        navigate(this@UserInfoNavRoute.route, options)

    fun NavGraphBuilder.userInfoScreen(
        navController: NavController
    ) {
        composable(
            route = this@UserInfoNavRoute.route,
            arguments = this@UserInfoNavRoute.arguments,
            deepLinks = this@UserInfoNavRoute.deepLinks,
        ) {
            UserInfoRoute(
                onBackClick = navController::popBackStack
            )
        }
    }
}

@Composable
internal fun UserInfoRoute(
    onBackClick: () -> Unit,
    viewModel: UserMineViewModel = hiltViewModel()
) {
    val userPreferences by viewModel.userData.collectAsStateWithLifecycle()
    val uiState by viewModel.userInfoUiState.collectAsStateWithLifecycle()

    UserInfoScreen(
        uiState = uiState,
        userData = userPreferences,
        onBackClick = onBackClick,
        onUpdatePhone = viewModel::updatePhone,
        onUpdateSex = viewModel::updateSex,
        onUpdateAvatar = viewModel::updateAvatar,
        onUpdateNickname = viewModel::updateNickname,
        onErrorMessage = viewModel::userMineError,
    )
}