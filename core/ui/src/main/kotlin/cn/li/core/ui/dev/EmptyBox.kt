package cn.li.core.ui.dev

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun EmptyBox(
    modifier: Modifier = Modifier,
    tips: String = ""
) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Text(text = tips)
    }
}