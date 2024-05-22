package cn.li.feature.login.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.li.core.ui.base.ClearableTextFiled
import cn.li.core.ui.theme.LightGrayTextColor
import cn.li.core.ui.theme.LightGrayTextFieldContainerColor
import cn.li.core.ui.theme.Red50

/**
 * 密码输入框
 * @param password 初始值
 * @param onValueChange 值改变时的回调
 * @param isError 是否存在错误
 * @param errorTips 错误提示
 * */
@Composable
fun PasswordTextField(
    password: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorTips: String = "",
    placeHolderText: String? = null,
    labelText: String? = null
) {
    var lastValue by remember(password) {
        mutableStateOf(password)
    }
    var passwordVisible by remember {
        mutableStateOf(false)
    }
    Column(modifier = modifier) {
        Text(
            text = labelText ?: "密码",
            modifier = Modifier
                .padding(vertical = 8.dp)
        )
        val focusManager = LocalFocusManager.current
        ClearableTextFiled(
            value = lastValue,
            onValueChange = onValueChange,
            leadingIcon = {
                Icon(Icons.Rounded.Lock, contentDescription = "")
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    // 修改密码的显示状态
                    if (!passwordVisible) {
                        Icon(
                            Icons.Rounded.VisibilityOff,
                            contentDescription = null,
                        )
                    } else {
                        Icon(
                            Icons.Rounded.Visibility,
                            contentDescription = null,
                        )
                    }
                }
            },
            // 设置密码可见性
            visualTransformation = if (passwordVisible) VisualTransformation.None
            else PasswordVisualTransformation('*'),

            isError = isError,
            supportingText = {
                Text(text = if (isError) errorTips else "")
            },
            placeholder = {
                Text(placeHolderText ?: "请输入您的密码", color = LightGrayTextColor)
            },
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors().copy(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
                errorContainerColor = Red50,
                focusedContainerColor = Color(0xfffafafa),
                unfocusedContainerColor = LightGrayTextFieldContainerColor,
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Password
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }),
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PasswordTextFieldPreview() {
    var password by remember {
        mutableStateOf("123")
    }
    PasswordTextField(
        password = password,
        isError = true,
        errorTips = "密码长度长！",
        onValueChange = {})
}