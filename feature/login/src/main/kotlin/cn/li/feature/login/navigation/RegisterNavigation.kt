package cn.li.feature.login.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.navigation.navDeepLink
import cn.li.feature.login.LoginViewModel
import cn.li.feature.login.RegisterScreen
import cn.li.model.NavigationRoute
import cn.li.model.constant.DEEP_LINK_PREFIX

object RegisterNavigationRoute : NavigationRoute {

    data object Arguments {
        const val OPTIONAL_USERNAME = "username"
    }

    override val routePrefix: String
        get() = "register"

    override val deepLinkPrefix: String
        get() = "${DEEP_LINK_PREFIX}/register"

    override val route: String
        get() = arguments.toRoute(routePrefix)

    override val arguments: List<NamedNavArgument>
        get() = listOf(
            navArgument(Arguments.OPTIONAL_USERNAME) {
                type = NavType.StringType
                defaultValue = ""
            }
        )

    override val deepLinks: List<NavDeepLink>
        get() = listOf(
            navDeepLink {
                uriPattern = arguments.toRoute(deepLinkPrefix)
            }
        )

    /**
     * 导航路由项
     * */
    fun NavController.navigateToRegister(username: String, navOptions: NavOptions) {
        val genRoute = mapOf(
            (Arguments.OPTIONAL_USERNAME to username)
        ).toRoute(routePrefix)
        navigate(genRoute, navOptions)
    }

    /**
     * 在 [NavGraphBuilder] 中注册该路由项
     * */
    fun NavGraphBuilder.registerScreen(
        onBackClick: () -> Unit,
        onLoginNavigate: (username: String, password: String) -> Unit,
    ) {
        composable(
            route = this@RegisterNavigationRoute.route,
            arguments = this@RegisterNavigationRoute.arguments,
            deepLinks = this@RegisterNavigationRoute.deepLinks
        ) {
            val username =
                it.arguments?.getString(Arguments.OPTIONAL_USERNAME)!!

            RegisterRoute(
                onBackClick = onBackClick,
                onLoginNavigate = onLoginNavigate,
                initialUsername = username
            )
        }
    }
}


@Composable
internal fun RegisterRoute(
    onBackClick: () -> Unit,
    onLoginNavigate: (username: String, password: String) -> Unit,
    initialUsername: String,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.registerUiState.collectAsStateWithLifecycle()
    RegisterScreen(
        uiState = uiState,
        onRegister = viewModel::register,
        onBackClick = onBackClick,
        onLoginNavigate = onLoginNavigate,
        initialUsername = initialUsername,
    )
}