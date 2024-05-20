package cn.li.feature.login.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavController
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import cn.li.feature.login.LoginScreen
import cn.li.feature.login.LoginViewModel
import cn.li.model.NavigationRoute
import cn.li.model.constant.DEEP_LINK_PREFIX

private const val TAG = "LoginNavigation"

object LoginNavigationRoute : NavigationRoute {

    data object Arguments {
        const val USERNAME = "username"

        const val PASSWORD = "password"
    }

    override val routePrefix: String
        get() = "login"

    override val deepLinkPrefix: String
        get() = "${DEEP_LINK_PREFIX}/login"

    override val arguments: List<NamedNavArgument>
        get() = listOf(
            navArgument(Arguments.USERNAME) {
                type = NavType.StringType
                defaultValue = ""
            },
            navArgument(Arguments.PASSWORD) {
                type = NavType.StringType
                defaultValue = ""
            }
        )

    /**
     * 导航到注册界面
     * */
    fun NavController.navigateToLogin(
        username: String,
        password: String,
        navOptions: NavOptions
    ) {
        val genRoute = mapOf(
            Arguments.USERNAME to username,
            Arguments.PASSWORD to password
        ).toRoute(routePrefix)
        navigate(genRoute, navOptions)
    }

    /**
     * 在 [NavGraphBuilder] 注册界面
     * */
    fun NavGraphBuilder.loginScreen(
        onBackClick: () -> Unit,
        onRegisterNavigation: (username: String) -> Unit,
    ) {
        composable(
            route = this@LoginNavigationRoute.route,
            deepLinks = this@LoginNavigationRoute.deepLinks,
            arguments = this@LoginNavigationRoute.arguments
        ) {
            val username = it.arguments?.getString(Arguments.USERNAME)!!
            val password = it.arguments?.getString(Arguments.PASSWORD)!!
            LoginRoute(
                onPressBack = onBackClick,
                onRegisterNavigation = onRegisterNavigation,
                initialUsername = username,
                initialPassword = password,
            )
        }
    }
}


@Composable
fun LoginRoute(
    initialUsername: String,
    initialPassword: String,
    onPressBack: () -> Unit,
    onRegisterNavigation: (username: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val loginUiState by viewModel.loginUiState.collectAsStateWithLifecycle()

    LoginScreen(
        uiState = loginUiState,
        onLogin = viewModel::login,
        onBackClick = onPressBack,
        onRegisterClick = onRegisterNavigation,
        modifier = modifier,
        initialUsername = initialUsername,
        initialPassword = initialPassword,
    )
}