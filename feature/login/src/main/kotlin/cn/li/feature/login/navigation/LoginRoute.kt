package cn.li.feature.login.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.li.feature.login.LoginScreen
import cn.li.feature.login.LoginViewModel

@Composable
fun LoginRoute(
    onPressBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val loginUiState by viewModel.loginUiState.collectAsStateWithLifecycle()

    LoginScreen(
        uiState = loginUiState,
        onLogin = viewModel::login,
        modifier = modifier
    )
}