package cn.li.feature.home.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun HomeRoute(onLoginNavigate: () -> Unit) {
    Column {
        Box(
            contentAlignment = Alignment.Center, modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
        ) {
            Text(text = "Home开发中")
        }
        TextButton(onClick = onLoginNavigate) {
            Text(text = "肘！登录！")
        }
    }
}