package cn.li.feature.login

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocal
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.li.core.ui.base.CheckboxText
import cn.li.feature.login.state.LoginUiState
import cn.li.feature.login.state.validatePassword
import cn.li.feature.login.state.validateUsername
import cn.li.core.ui.theme.LightGrayTextColor
import cn.li.core.ui.theme.LightGreenColor
import cn.li.feature.login.ui.PasswordTextField
import cn.li.feature.login.ui.TopToolBar
import cn.li.feature.login.ui.UserAccountTextField

private const val TAG = "LoginScreen"

/**
 * 登录界面
 * TODO:(li) 适配主题色？通过 [CompositionLocal] 实现
 *
 * @author YeMengLiChou(Gleamrise)
 * @param uiState 当前Ui的状态 [LoginUiState]
 * @param onLogin 点击登录时触发
 * @param onBackClick 点击上方 “返回” 按钮时触发
 * @param onRegisterClick 点击 “注册” 按钮时触发
 * @param modifier
 * @param initialUsername 初始化显示的用户名
 * @param initialPassword 初始化显示的密码
 * */
@Composable
internal fun LoginScreen(
    uiState: LoginUiState,
    onLogin: ((username: String, password: String, employeeLogin: Boolean) -> Unit),
    onBackClick: () -> Unit,
    onRegisterClick: (username: String) -> Unit,
    modifier: Modifier = Modifier,
    initialUsername: String,
    initialPassword: String,
) {
    // 用于显示信息
    val snackbarHostState = remember {
        SnackbarHostState()
    }

    // 用户名输入
    var username by rememberSaveable {
        mutableStateOf(initialUsername)
    }

    var password by rememberSaveable {
        mutableStateOf(initialPassword)
    }

    // 当 uiState 发生改变时，需要重新启动协程，进行判断
    LaunchedEffect(key1 = uiState) {
        when (uiState) {
            is LoginUiState.Failed -> {
                snackbarHostState.showSnackbar(
                    uiState.tips ?: "登录失败",
                    duration = SnackbarDuration.Long,
                    actionLabel = "前往注册"
                ).let {
                    if (it == SnackbarResult.ActionPerformed) {
                        onRegisterClick(username)
                    }
                }
            }

            is LoginUiState.Success -> {
                snackbarHostState.showSnackbar(
                    message = "登录成功",
                    duration = SnackbarDuration.Short,
                    withDismissAction = true
                )
                // 登录成功后返回上一级
                onBackClick()
            }

            is LoginUiState.Loading -> {
                snackbarHostState.showSnackbar(
                    message = "登录中...",
                    duration = SnackbarDuration.Indefinite,
                )
            }

            else -> {}
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
            // 顶部栏
            TopToolBar(
                onBackClick = onBackClick,
                modifier = Modifier
                    .padding(bottom = 64.dp)
            ) {
                TextButton(onClick = { onRegisterClick("") }) {
                    Text(text = "前往注册", color = Color(0xff80c5be))
                }
            }

            LoginForm(
                uiState = uiState,
                onLogin = onLogin,
                username = username,
                password = password,
                onValueChange = { changedUsername, changedPassword ->
                    username = changedUsername
                    password = changedPassword
                }
            )
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .imePadding()
        )
    }
}

/**
 * 登录表单
 * */
@Composable
private fun LoginForm(
    uiState: LoginUiState,
    onLogin: (username: String, password: String, employeeLogin: Boolean) -> Unit,
    onValueChange: (username: String, password: String) -> Unit,
    modifier: Modifier = Modifier,
    username: String = "",
    password: String = ""
) {

    var usernameErrorTips by remember {
        mutableStateOf<String?>(null)
    }

    var passwordErrorTips by remember {
        mutableStateOf<String?>(null)
    }

    // 是否为员工登录
    var isEmployeeLogin by remember {
        mutableStateOf(false)
    }

    Column(modifier = modifier.fillMaxSize()) {
        val centerModifier = Modifier
            .fillMaxWidth(0.9f)
            .align(Alignment.CenterHorizontally)

        Text(
            text = "登录",
            modifier = centerModifier,
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "登录您的账号",
            fontSize = 16.sp,
            color = LightGrayTextColor,
            modifier = centerModifier,
        )

        Spacer(modifier = Modifier.height(24.dp))

        UserAccountTextField(
            username = username,
            isError = usernameErrorTips != null,
            errorTips = usernameErrorTips ?: "",
            modifier = centerModifier,
            onValueChange = { username ->
                onValueChange(username, password)
                usernameErrorTips = validateUsername(username)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        PasswordTextField(
            password = password,
            modifier = centerModifier,
            isError = passwordErrorTips != null,
            errorTips = passwordErrorTips ?: "",
            onValueChange = { password ->
                onValueChange(username, password)
                passwordErrorTips = validatePassword(password)
            }
        )

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = centerModifier
                .padding(vertical = 16.dp)
        ) {
            Text(
                text = "忘记密码",
                color = LightGreenColor,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                CheckboxText(
                    checked = isEmployeeLogin,
                    onCheckedChange = { isEmployeeLogin = it },
                    colors = CheckboxDefaults.colors().copy(
                        checkedCheckmarkColor = LightGreenColor
                    )
                ) {
                    Text(text = "员工登录")
                }
            }
        }

        val buttonEnable =
            (username.isNotBlank() && password.isNotBlank())
                    && (usernameErrorTips == null && passwordErrorTips == null)
                    && (uiState != LoginUiState.Loading)

        Button(
            enabled = buttonEnable,
            onClick = {
                onLogin.invoke(username, password, isEmployeeLogin)
            },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .height(48.dp)
                .then(centerModifier),
            colors = ButtonDefaults.buttonColors().copy(
                containerColor = LightGreenColor
            )
        ) {
            Text(text = "登录")
        }
    }
}


@Preview(showBackground = true, showSystemUi = true, backgroundColor = 0xFFFFFF)
@Composable
private fun LoginScreenPreview() {
    LoginScreen(
        uiState = LoginUiState.Nothing,
        onLogin = { username, password, employeeLogin ->
            Log.d("LoginScreenPreview", "login-test: $username $password $employeeLogin")
        },
        onRegisterClick = {},
        onBackClick = {},
        initialUsername = "",
        initialPassword = "",
    )
}

