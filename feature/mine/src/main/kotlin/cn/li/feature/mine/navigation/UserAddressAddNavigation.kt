package cn.li.feature.mine.navigation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.Composable
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
import cn.li.common.ext.debugId
import cn.li.feature.mine.UserMineViewModel
import cn.li.feature.mine.ui.address.AddressAddScreen
import cn.li.model.NavigationRoute
import cn.li.model.constant.DEEP_LINK_PREFIX
import kotlinx.coroutines.flow.first

object UserAddressAddNavigation : NavigationRoute {

    data object Arguments {
        const val ADDRESS_ID = "address_id"
    }

    override val routePrefix: String
        get() = "address_add"
    override val deepLinkPrefix: String
        get() = "${DEEP_LINK_PREFIX}/${routePrefix}"

    override val arguments: List<NamedNavArgument>
        get() = listOf(
            navArgument(Arguments.ADDRESS_ID) {
                defaultValue = 0
                type = NavType.LongType
            }
        )

    /**
     * 导航到 编辑地址项页面，
     * @param addressId 需要编辑的地址项id，如果为null，则表示新增
     * */
    fun NavHostController.navigateToAddAddress(addressId: Long? = null, navOptions: NavOptions) =
        navigate(
            route = mapOf(Arguments.ADDRESS_ID to addressId).toRoute(this@UserAddressAddNavigation.routePrefix),
            navOptions
        )

    @SuppressLint("UnrememberedGetBackStackEntry")
    fun NavGraphBuilder.addAddressScreen(
        controller: NavHostController,
        parentRoute: String,
    ) {
        composable(
            route = this@UserAddressAddNavigation.route,
            arguments = this@UserAddressAddNavigation.arguments,
            deepLinks = this@UserAddressAddNavigation.deepLinks,
        ) {
            val id = it.arguments!!.getLong(Arguments.ADDRESS_ID)
            val backStackEntry = remember {
                controller.getBackStackEntry(parentRoute)
            }
            Log.d("Navigation", "Navigated to `addAddressScreen`:  id: $id")
            UserAddAddressRoute(
                id = id,
                onBackClick = controller::popBackStack,
                viewModel = hiltViewModel(backStackEntry)
            )
        }
    }
}


@Composable
internal fun UserAddAddressRoute(
    id: Long?,
    onBackClick: () -> Unit,
    viewModel: UserMineViewModel = hiltViewModel(key = "address")
) {
    // 从当前的位置找到
    val editItem = id?.let {
        viewModel.currentAddressList.collectAsStateWithLifecycle().value.find { item ->
            item.id == it
        }
    }

    AddressAddScreen(
        editItem = editItem,
        onBackClick = onBackClick,
        onAddedClick = {
            viewModel.addAddressBookItem(it)
        },
        onEditedClick = {
            viewModel.updateAddressBookItem(it)
        })
}