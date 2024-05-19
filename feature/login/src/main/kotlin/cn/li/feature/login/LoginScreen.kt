package cn.li.feature.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.li.core.ui.icon.FwpIcons
import cn.li.feature.login.state.LoginUiState
import cn.li.feature.login.ui.LightGrayTextColor
import cn.li.feature.login.ui.PasswordTextField
import cn.li.feature.login.ui.UserAccountTextField

@Composable
internal fun LoginScreen(
    uiState: LoginUiState,
    modifier: Modifier = Modifier,
    onLogin: ((username: String, password: String) -> Unit) = { _, _ -> },
    onBackClick: () -> Unit = {}
) {
    Column(modifier = modifier.fillMaxSize()) {
        val centerModifier = Modifier
            .fillMaxWidth(0.9f)
            .align(Alignment.CenterHorizontally)

        // 顶部栏
        TopToolBar(
            onBackClick = onBackClick,
            modifier = Modifier
                .padding(bottom = 64.dp)
        )

        Text(
            text = "登录",
            modifier = Modifier
                .padding(4.dp)
                .then(centerModifier),
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "登录您的账号",
            fontSize = 16.sp,
            color = LightGrayTextColor,
            modifier = Modifier
                .then(centerModifier)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 用于显示某些信息
        val snackbarHostState = remember {
            SnackbarHostState()
        }
        // 当 uiState 发生改变时，需要重新启动协程，进行判断
        LaunchedEffect(key1 = uiState) {
            if (uiState is LoginUiState.Failed) {
                val result = snackbarHostState.showSnackbar(uiState.tips ?: "登录失败", duration = SnackbarDuration.Long, actionLabel = "clear")
                when (result) {
                    SnackbarResult.ActionPerformed -> {

                    }
                    else -> {

                    }
                }
            }
        }

        // 用户名输入
        var username by remember {
            mutableStateOf("")
        }
        var usernameErrorTips by remember {
            mutableStateOf<ErrorTips?>(null)
        }
        UserAccountTextField(
            username = username,
            isError = usernameErrorTips?.hasError ?: false,
            errorTips = usernameErrorTips?.tips ?: "",
            modifier = Modifier
                .then(centerModifier)
        ) {
            username = it
            usernameErrorTips = validateUsername(username)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 密码输入
        var password by remember {
            mutableStateOf("")
        }
        var passwordErrorTips by remember {
            mutableStateOf<ErrorTips?>(null)
        }
        PasswordTextField(
            password = password,
            modifier = Modifier
                .then(centerModifier)
        ) {
            password = it
            passwordErrorTips = validatePassword(password)
        }
        Row(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .then(centerModifier)
        ) {
            Text(
                text = "忘记密码",
                color = Color(0xff83bdb4),
            )
        }

        val snackBarHostState = remember {
            SnackbarHostState()
        }
//        LaunchedEffect(key1 = ) {
//            
//        }
        Button(
            enabled = usernameErrorTips == null && passwordErrorTips == null,
            onClick = {
                // TODO: 验证

                onLogin.invoke(username, password)
            },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .height(48.dp)
                .then(centerModifier),
            colors = ButtonDefaults.buttonColors().copy(
                containerColor = Color(0xff80c5be)
            )
        ) {
            Text(text = "登录")
        }
    }
}


internal data class ErrorTips(
    val hasError: Boolean,
    val tips: String
)

private fun validateUsername(username: String): ErrorTips? {
    if (username.isEmpty()) {
        return ErrorTips(true, "账号/手机号不能为空!")
    }
    if (username.length >= 20) {
        return ErrorTips(true, "账号/手机号过长!")
    }
    return null
}

private fun validatePassword(password: String): ErrorTips {
    if (password.isEmpty()) {
        return ErrorTips(true, "密码不能为空!")
    }
    if (password.length >= 30) {
        return ErrorTips(true, "密码过长!")
    }
    return ErrorTips(false, "")
}


@Composable
fun TopToolBar(
    modifier: Modifier = Modifier,
    showBackButton: Boolean = true,
    onBackClick: () -> Unit = {},
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
    ) {
        if (showBackButton) {
            IconButton(onClick = { onBackClick() }) {
                Icon(
                    imageVector = FwpIcons.ArrowBack,
                    contentDescription = "",
                )
            }
        } else {
            // Keeps the NiaFilterChip aligned to the end of the Row.
            Spacer(modifier = Modifier.width(1.dp))
        }
    }
}

//@Preview(showBackground = true, showSystemUi = true, backgroundColor = 0xFFFFFF)
//@Composable
//internal fun LoginScreenPreview() {
////    LoginScreen()
//}
