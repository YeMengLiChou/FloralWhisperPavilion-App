package cn.li.feature.mine.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navOptions
import cn.li.feature.mine.UserMineViewModel
import cn.li.feature.mine.navigation.UserAddressAddNavigation.navigateToAddAddress
import cn.li.feature.mine.ui.address.AddressManageScreen
import cn.li.model.NavigationRoute
import cn.li.model.constant.DEEP_LINK_PREFIX


object UserAddressManageNavigation : NavigationRoute {

    data object Arguments {
        const val FROM_ROUTE = "from_route"
        const val KEY = "selected_key"
    }

    override val routePrefix: String
        get() = "address"
    override val deepLinkPrefix: String
        get() = "${DEEP_LINK_PREFIX}/${routePrefix}"

    override val arguments: List<NamedNavArgument>
        get() = listOf(
            navArgument(Arguments.KEY) {
                defaultValue = ""
                type = NavType.StringType
            }
        )

    /**
     * @param selectedKey 写入 SavedStateHandle 的键值，用于返回选择的地址项，如果为null则没有选择
     * */
    fun NavHostController.navigateToAddressManage(
        selectedKey: String? = null,
        options: NavOptions
    ) {
        val route =
            mapOf(Arguments.KEY to selectedKey).toRoute(this@UserAddressManageNavigation.routePrefix)
        navigate(route, options)

    }

    fun NavGraphBuilder.addressManageScreen(
        navHostController: NavHostController,
        parentRoute: String,
    ) {
        composable(
            route = this@UserAddressManageNavigation.route,
            arguments = this@UserAddressManageNavigation.arguments,
            deepLinks = this@UserAddressManageNavigation.deepLinks
        ) {
            val backStackEntry = remember(it) {
                navHostController.getBackStackEntry(parentRoute)
            }

            // 假设是从上一个栈来的
            val fromBackStackEntry = navHostController.previousBackStackEntry

            val selectedKey = it.arguments?.getString(Arguments.KEY)!!

            AddressManageRoute(
                shouldSelect = selectedKey.isNotEmpty(),
                onAddClick = {
                    navHostController.navigateToAddAddress(addressId = null, navOptions {
                        launchSingleTop = true
                    })
                },
                onBackClick = navHostController::popBackStack,
                onSelectedItem = { selectItemId ->
                    // 将 selectedId 写入上一个 Entry 的 savedStateHandle
                    if (selectedKey.isNotEmpty()) {
                        fromBackStackEntry?.savedStateHandle?.set(selectedKey, selectItemId)
                        navHostController.popBackStack()
                    }
                },
                onUpdateNavigate = {
                    navHostController.navigateToAddAddress(
                        addressId = it,
                        navOptions {
                            launchSingleTop = true
                            restoreState = true
                        }
                    )
                },
                viewModel = hiltViewModel(backStackEntry)
            )
        }
    }
}


@Composable
internal fun AddressManageRoute(
    shouldSelect: Boolean,
    onAddClick: () -> Unit,
    onBackClick: () -> Unit,
    onUpdateNavigate: (Long) -> Unit,
    onSelectedItem: (Long) -> Unit,
    viewModel: UserMineViewModel = hiltViewModel(key = "address")
) {
    val uiState by viewModel.userAddressUiState.collectAsStateWithLifecycle()

    SideEffect {
        viewModel.getAddressBookList(refresh = false)
    }

    AddressManageScreen(
        shouldSelect = shouldSelect,
        uiState = uiState,
        onBackClick = onBackClick,
        onSelectedItem = onSelectedItem,
        onEditClick = onUpdateNavigate,
        onAddClick = onAddClick
    )

}