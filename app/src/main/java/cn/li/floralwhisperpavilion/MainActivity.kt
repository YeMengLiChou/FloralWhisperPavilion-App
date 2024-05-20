package cn.li.floralwhisperpavilion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import cn.li.data.util.NetworkMonitor
import cn.li.floralwhisperpavilion.navigation.TopLevelDestination
import cn.li.floralwhisperpavilion.ui.FwpAppWrapper
import cn.li.floralwhisperpavilion.ui.rememberAppState
import cn.li.network.retrofit.datasource.RetrofitUserDataSource
import cn.li.floralwhisperpavilion.ui.theme.FloralWhisperPavilionTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userDataSource: RetrofitUserDataSource

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        // TODO: 添加 SplashScreen
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        var uiState: MainActivityUiState by mutableStateOf(MainActivityUiState.Loading)

        // 更新状态
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    uiState = it
                }
            }
        }

        splashScreen.setKeepOnScreenCondition {
            when (uiState) {
                MainActivityUiState.Loading -> true
                else -> false
            }
        }

        enableEdgeToEdge()
        setContent {

            // 根据当前的状态更新目的地
            val destinations = if (uiState is MainActivityUiState.EmployeeLogin) {
                listOf(TopLevelDestination.EMPLOYEE_ORDER, TopLevelDestination.SHOP)
            } else {
                listOf(
                    TopLevelDestination.HOME,
                    TopLevelDestination.MENU,
                    TopLevelDestination.USER_ORDER,
                    TopLevelDestination.USER_MINE
                )
            }
            // app 状态
            val appState = rememberAppState(
                windowSizeClass = calculateWindowSizeClass(activity = this),
                networkMonitor = networkMonitor,
                topLevelDestinations = destinations
            )

            FloralWhisperPavilionTheme {
                FwpAppWrapper(appState = appState, modifier = Modifier)
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }
}

