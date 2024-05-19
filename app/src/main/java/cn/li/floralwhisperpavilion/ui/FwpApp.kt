package cn.li.floralwhisperpavilion.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.li.feature.home.navigation.HOME_ROUTE
import cn.li.floralwhisperpavilion.navigation.FwpNavigationHost


/**
 * App 主要内容界面的扩充，添加网络连接性监听
 * */
@Composable
internal fun FwpAppWrapper(appState: AppState, modifier: Modifier = Modifier) {
    val snackbarHostState = remember { SnackbarHostState() }

    val isOffline by appState.isOffline.collectAsStateWithLifecycle()

    val notConnectedMessage = "失去连接！"
    LaunchedEffect(isOffline) {
        if (isOffline) {
            snackbarHostState.showSnackbar(
                message = notConnectedMessage,
                duration = SnackbarDuration.Indefinite,
            )
        }
    }

    FwpApp(
        appState = appState,
        snackbarHostState = snackbarHostState
    )
}

/**
 * App 主要内容界面
 * */
@Composable
internal fun FwpApp(
    appState: AppState,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.systemBarsPadding(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        contentColor = MaterialTheme.colorScheme.onBackground,
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            // 底部导航栏
            if (appState.shouldShowBottomBar && appState.shouldShowBottomBarByNavigation) {
                AppBottomBar(
                    destinations = appState.topLevelDestinations,
                    destinationsWithUnreadResources = emptySet(),
                    onNavigateToDestination = appState::navigateToTopLevelDestination,
                    currentDestination = appState.currentDestination,
                    modifier = Modifier.testTag("AppBottomBar"),
                )
            }
        }
    ) { padding ->
        Row(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .consumeWindowInsets(padding)
                .windowInsetsPadding(
                    WindowInsets.safeDrawing.only(
                        WindowInsetsSides.Horizontal,
                    ),
                ),
        ) {
            if (appState.shouldShowBottomRail) {
                // TODO: 添加侧边栏
            }

            Box(Modifier.fillMaxSize()) {
                FwpNavigationHost(
                    navController = appState.navController,
                    startDestination = HOME_ROUTE,
                )
            }
        }
    }
}