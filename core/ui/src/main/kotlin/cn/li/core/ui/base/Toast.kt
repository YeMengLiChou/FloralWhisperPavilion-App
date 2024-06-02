package cn.li.core.ui.base

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext


private var toast: Toast? = null

/**
 * 显示 Toast
 * */
@Composable
fun LToast(
    text: String,
    duration: Int
) {
    toast?.cancel()
    toast = Toast.makeText(LocalContext.current, text, duration)
    toast?.show()
}

