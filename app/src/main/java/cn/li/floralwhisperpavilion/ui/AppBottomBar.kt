package cn.li.floralwhisperpavilion.ui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import cn.li.floralwhisperpavilion.navigation.TopLevelDestination

/**
 * 应用底部栏
 * @param destinations 底部栏显示的导航目的地
 * @param currentDestination 当前显示
 * @param destinationsWithUnreadResources 拥有未读消息的部分
 * @param onNavigateToDestination 当点击到某部分时传递对应的 [TopLevelDestination] 回调
 * @param modifier
 * */
@Composable
fun AppBottomBar(
    destinations: List<TopLevelDestination>,
    currentDestination: NavDestination?,
    destinationsWithUnreadResources: Set<TopLevelDestination>,
    onNavigateToDestination: (TopLevelDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(
        modifier = modifier,
        contentColor = FwpNavigationDefaults.navigationContentColor(),
        tonalElevation = 0.dp,
    ) {
        // 为每个 item 插入到底部导航栏中
        destinations.forEach { dest ->
            // 选中状态
            val selected = currentDestination.isTopLevelDestinationInHierarchy(dest)
            // 是否存在未读信息
            val hasUnread = destinationsWithUnreadResources.contains(dest)

            NavigationBottomBarItem(
                selected = selected,
                onClick = {
                    onNavigateToDestination(dest)
                },
                icon = {
                    Icon(imageVector = dest.unselectIcon, contentDescription = null)
                },
                selectedIcon = {
                    Icon(imageVector = dest.selectIcon, contentDescription = null)
                },
                label = { Text(text = dest.iconText) },
                modifier = if (hasUnread) Modifier.notificationDot() else Modifier,

            )
        }
    }
}

/**
 * 导航栏子项
 * @param selected 是否选中
 * @param onClick 点击时回调
 * @param modifier
 * @param enabled 启用
 * @param alwaysShowLabel 展示底部标签
 * @param selectedIcon 选中时的图标
 * @param label 标签
 * */
@Composable
private fun RowScope.NavigationBottomBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    alwaysShowLabel: Boolean = true,
    icon: @Composable () -> Unit,
    selectedIcon: @Composable () -> Unit = icon,
    label: @Composable (() -> Unit)? = null,
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = if (selected) selectedIcon else icon,
        modifier = modifier,
        enabled = enabled,
        label = label,
        alwaysShowLabel = alwaysShowLabel,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = FwpNavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = FwpNavigationDefaults.navigationContentColor(),
            selectedTextColor = FwpNavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = FwpNavigationDefaults.navigationContentColor(),
            indicatorColor = FwpNavigationDefaults.navigationIndicatorColor(),
        ),
    )
}

/**
 * 画一个圆点
 * TODO: 实现显示具体数字
 * */
private fun Modifier.notificationDot(): Modifier =
    composed {
        val tertiaryColor = MaterialTheme.colorScheme.tertiary
        drawWithContent {
            drawContent()
            // 画圆
            drawCircle(
                tertiaryColor,
                radius = 5.dp.toPx(),
                // 圆的中心点
                // (NavigationBarTokens.ActiveIndicatorWidth = 64.dp)
                center = center + Offset(
                    64.dp.toPx() * .45f,
                    32.dp.toPx() * -.45f - 6.dp.toPx(),
                ),
            )
        }
    }

/**
 *
 *
 * */
private fun NavDestination?.isTopLevelDestinationInHierarchy(destination: TopLevelDestination) =
    this?.hierarchy?.any {
        it.route?.contains(destination.name, true) ?: false
    } ?: false

private object FwpNavigationDefaults {
    @Composable
    fun navigationContentColor() = MaterialTheme.colorScheme.onSurfaceVariant

    @Composable
    fun navigationSelectedItemColor() = MaterialTheme.colorScheme.onPrimaryContainer

    @Composable
    fun navigationIndicatorColor() = MaterialTheme.colorScheme.primaryContainer
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AppBottomBarPreview() {
    var currentState by remember {
        mutableStateOf(TopLevelDestination.HOME)
    }
    Scaffold(
        bottomBar = {
            AppBottomBar(
                destinations = listOf(
                    TopLevelDestination.HOME,
                    TopLevelDestination.MENU,
                    TopLevelDestination.USER_ORDER,
                    TopLevelDestination.USER_MINE
                ),
                currentDestination = null,
                onNavigateToDestination = {
                    currentState = it
                },
                destinationsWithUnreadResources = setOf(TopLevelDestination.USER_ORDER)
            )
        }
    ) {
        it
    }
}

