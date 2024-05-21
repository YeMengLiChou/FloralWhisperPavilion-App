package cn.li.feature.login

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import cn.li.feature.login.state.RegisterUiState
import cn.li.feature.login.state.validatePassword
import cn.li.feature.login.state.validateSecondPassword
import cn.li.feature.login.state.validateUsername
import cn.li.feature.login.ui.LightGrayTextColor
import cn.li.feature.login.ui.PasswordTextField
import cn.li.feature.login.ui.TopToolBar
import cn.li.feature.login.ui.UserAccountTextField

/**
 * 注册界面
 *
 * TODO(Li): 适配输入法，防止被输入法覆盖
 * */
@Composable
internal fun RegisterScreen(
    uiState: RegisterUiState,
    onRegister: ((username: String, password: String) -> Unit),
    onBackClick: () -> Unit,
    onLoginNavigate: (username: String, password: String) -> Unit,
    modifier: Modifier = Modifier,
    initialUsername: String = "",
) {
    // 用于显示信息
    val snackbarHostState = remember {
        SnackbarHostState()
    }

    var username by rememberSaveable {
        mutableStateOf(initialUsername)
    }

    var password by rememberSaveable {
        mutableStateOf("")
    }

    // 当 uiState 发生改变时，需要重新启动协程，进行判断
    LaunchedEffect(key1 = uiState) {
        when (uiState) {
            is RegisterUiState.Failed -> {
                snackbarHostState.showSnackbar(
                    uiState.msg.takeIf { it.isNotBlank() } ?: "注册失败",
                    duration = SnackbarDuration.Long,
                    withDismissAction = true,
                )
            }

            is RegisterUiState.Success -> {
                snackbarHostState.showSnackbar(
                    message = "注册成功",
                    duration = SnackbarDuration.Short,
                    actionLabel = "前往登录",
                    withDismissAction = true,
                ).let {
                    // 点击 “前往登录” 后跳转到登录界面
                    if (it == SnackbarResult.ActionPerformed) {
                        onLoginNavigate(username, password)
                    }
                }
            }

            is RegisterUiState.Loading -> {
                snackbarHostState.showSnackbar(
                    message = "请稍后...",
                    duration = SnackbarDuration.Indefinite,
                )
            }

            else -> {}
        }
    }
    Box(
        modifier = modifier
            .fillMaxSize()

    ) {
        Column(
            modifier = Modifier
                .matchParentSize()
        ) {// 顶部栏
            TopToolBar(
                onBackClick = onBackClick,
                modifier = Modifier
                    .padding(bottom = 64.dp)
            ) {
                TextButton(onClick = {
                    onLoginNavigate(username, password)
                }) {
                    Text(text = "已有账号? 前往登录", color = Color(0xff80c5be))
                }
            }

            RegisterForm(
                uiState = uiState,
                onRegister = onRegister,
                username = username,
                password = password,
                onValueChange = { changedUsername, changedPassword ->
                    username = changedUsername
                    password = changedPassword
                },
                modifier = Modifier
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

@Composable
private fun RegisterForm(
    uiState: RegisterUiState,
    onRegister: (username: String, password: String) -> Unit,
    username: String,
    password: String,
    onValueChange: (username: String, password: String) -> Unit,
    modifier: Modifier = Modifier,
) {

    var usernameErrorTips by remember {
        mutableStateOf<String?>(null)
    }

    var passwordErrorTips by remember {
        mutableStateOf<String?>(null)
    }

    // 重复密码
    var secondPassword by remember {
        mutableStateOf("")
    }

    var secondPasswordErrorTips by remember {
        mutableStateOf<String?>(null)
    }


    Column(modifier = modifier.fillMaxSize()) {
        val centerModifier = Modifier
            .fillMaxWidth(0.9f)
            .align(Alignment.CenterHorizontally)

        Text(
            text = "注册",
            modifier = centerModifier,
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "注册您的账号",
            fontSize = 16.sp,
            color = LightGrayTextColor,
            modifier = centerModifier
        )

        Spacer(modifier = Modifier.height(24.dp))

        UserAccountTextField(
            username = username,
            isError = usernameErrorTips != null,
            errorTips = usernameErrorTips ?: "",
            modifier = centerModifier,
            onValueChange = {
                onValueChange(it, password)
                usernameErrorTips = validateUsername(it)
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        PasswordTextField(
            password = password,
            modifier = centerModifier,
            isError = passwordErrorTips != null,
            errorTips = passwordErrorTips ?: "",
            onValueChange = {
                onValueChange(username, it)
                passwordErrorTips = validatePassword(it)
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        PasswordTextField(
            password = secondPassword,
            modifier = centerModifier,
            isError = secondPasswordErrorTips != null,
            errorTips = secondPasswordErrorTips ?: "",
            onValueChange = {
                secondPassword = it
                secondPasswordErrorTips = validateSecondPassword(password, it)
            },
            labelText = "重复密码",
            placeHolderText = "请重复输入您的密码"
        )


        val buttonEnable =
            (usernameErrorTips == null && passwordErrorTips == null && secondPasswordErrorTips == null)
                    && (uiState != RegisterUiState.Loading)
        // 注册按钮，点击时检测是否存在错误
        Button(
            enabled = buttonEnable,
            onClick = {
                onRegister.invoke(username, password)
            },
            shape = RoundedCornerShape(16.dp),
            modifier = centerModifier
                .padding(top = 32.dp)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors().copy(
                containerColor = Color(0xff80c5be)
            )
        ) {
            Text(text = "注册")
        }
    }
}


@Preview(showBackground = true, showSystemUi = true, backgroundColor = 0xFFFFFF)
@Composable
private fun RegisterScreenPreview() {
    RegisterScreen(
        uiState = RegisterUiState.Nothing,
        onRegister = { username, password ->
            Log.d("LoginScreenPreview", "login-test: $username $password")
        },
        onLoginNavigate = { _, _ -> },
        onBackClick = {}
    )
}

