package cn.li.model

import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavArgument
import androidx.navigation.NavController
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navDeepLink

/**
 * 导航的路由项声明
 * */
interface NavigationRoute {

    /**
     * 路由项的前缀
     * */
    val routePrefix: String

    /**
     * 深层连接的前缀
     * */
    val deepLinkPrefix: String

    /**
     * 路由项：全局唯一的字符串
     * */
    val route: String
        get() = arguments.toRoute(routePrefix)

    /**
     * 路由中的参数
     * */
    val arguments: List<NamedNavArgument>
        get() = emptyList()

    /**
     * 深层连接声明
     * */
    val deepLinks: List<NavDeepLink>
        get() = listOf(
            navDeepLink {
                uriPattern = arguments.toRoute(deepLinkPrefix)
            }
        )


    /**
     * 将所有声明的参数转换为路由
     * */
    fun List<NamedNavArgument>.toRoute(prefix: String): String {
        return joinTo(StringBuilder(), separator = "&", prefix = "${prefix}?") {
            "${it.name}={${it.name}}"
        }.toString()
    }

    /**
     * 将参数转换为路由
     * */
    fun Map<String, Any>.toRoute(prefix: String): String {
        return toList()
            .joinTo(StringBuilder(), separator = "&", prefix = "${prefix}?") {
                "${it.first}=${it.second}"
            }.toString()
    }


}
