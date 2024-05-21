package cn.li.feature.login.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.li.core.ui.base.CircleIcon
import cn.li.core.ui.base.ClearableTextFiled

/**
 * 用户账号输入框
 * @param username 初始值
 * @param onValueChange 值改变时的回调
 * */
@Composable
fun UserAccountTextField(
    modifier: Modifier = Modifier,
    username: String,
    isError: Boolean = false,
    errorTips: String = "",
    onValueChange: (String) -> Unit
) {
    var lastValue by remember(username) {
        mutableStateOf(username)
    }
    val focusManager = LocalFocusManager.current
    Column(
        modifier = modifier
    ) {
        Text(
            text = "账号",
            modifier = Modifier
                .padding(vertical = 8.dp)
        )
        ClearableTextFiled(
            value = username,
            onValueChange = onValueChange,
            leadingIcon = {
                Icon(
                    Icons.Rounded.AccountCircle, contentDescription = ""
                )
            },
            placeholder = {
                Text("您的账号/手机号", color = LightGrayTextColor)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            singleLine = true,
            isError = isError,
            supportingText = {
                // 需要用空串保持这个位置，否则会偏移
                Text(text = if (isError) errorTips else "")
            },
            colors = OutlinedTextFieldDefaults.colors().copy(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
                focusedContainerColor = Color(0xfffafafa),
                unfocusedContainerColor = LightGrayTextFieldContainerColor,
                errorContainerColor = Red50
            ),
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Ascii
            ),
            keyboardActions = KeyboardActions(onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            })
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UserAccountTextFiledPreview() {
    UserAccountTextField(username = "") {

    }
}