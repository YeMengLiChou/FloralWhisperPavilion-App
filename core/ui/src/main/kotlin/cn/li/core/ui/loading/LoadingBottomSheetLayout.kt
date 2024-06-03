package cn.li.core.ui.loading

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.ExperimentalMaterialApi
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.ModalBottomSheetLayout
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.ModalBottomSheetValue
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.li.core.ui.theme.LightGreenColor

/**
 * 底部加载弹窗布局，当 [loading] 为 true 时，显示底部弹窗且无法取消该弹窗
 *
 * @param show 是否显示弹窗，
 * @param loading 是否显示加载弹窗，当 [loading] 为 true 时忽略 [show] 值
 * @param loadingText 加载弹窗中文字
 * @param sheetContent [show] 为 true 时展示的内容
 * @param content 内容布局
 * */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LoadingBottomSheetLayout(
    show: Boolean,
    loading: Boolean,
    modifier: Modifier = Modifier,
    allowDismiss: Boolean  = false,
    loadingText: String = "加载中",
    sheetContent: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    val state = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = {
            allowDismiss // 非加载可以取消
        })

    LaunchedEffect(key1 = loading, key2 = show) {
        if (loading || show) {
            state.show()
        } else {
            state.hide()
        }
    }

    ModalBottomSheetLayout(
        sheetContent = {
            if (loading) {
                LoadingIndicator(loadingText = loadingText)
            } else {
                sheetContent()
            }
        },
        modifier = modifier,
        sheetState = state,
        content = content
    )

}

@Composable
private fun LoadingIndicator(
    loadingText: String,
    progressColor: Color = LightGreenColor,
    progressStrokeWidth: Dp = 2.dp
) {
    Row(
        modifier = Modifier
            .padding(vertical = 16.dp, horizontal = 16.dp)
            .fillMaxWidth()
            .height(64.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            strokeWidth = progressStrokeWidth,
            color = progressColor
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = loadingText, color = Color.Gray)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoadingBottomSheetLayoutPreview() {
    Box(modifier = Modifier.size(300.dp, 600.dp)) {
        LoadingBottomSheetLayout(
            show = false,
            loading = true
        ) {
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .background(color = Color.LightGray)
            )
        }
    }
}