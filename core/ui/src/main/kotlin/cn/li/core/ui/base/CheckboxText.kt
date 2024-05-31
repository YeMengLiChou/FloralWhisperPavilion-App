package cn.li.core.ui.base

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * 正常的 [Checkbox] 没有带有文字，该组件可以传递 [content] 显示文字
 * @param checked 是否选中
 * @param onCheckedChange 选中状态的变化
 * @param modifier
 * @param enabled 是否启用
 * @param colors 颜色
 * @param isRightSide [content] 的所在位置，左边还是右边
 * @param interactionSource
 * @param content 显示的内容
 * */
@Composable
fun CheckboxText(
    checked: Boolean,
    onCheckedChange: (checked: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: CheckboxColors = CheckboxDefaults.colors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    isRightSide: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        if (isRightSide) {
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.alignByBaseline(),
                enabled = enabled,
                colors = colors,
                interactionSource = interactionSource
            )
            Box(content = content)
        } else {
            Box(content = content)
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.alignByBaseline(),
                enabled = enabled,
                colors = colors,
                interactionSource = interactionSource
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun CheckboxTextPreview() {
    var checked by remember {
        mutableStateOf(false)
    }
    Box(Modifier.size(100.dp), contentAlignment = Alignment.Center) {
        CheckboxText(checked = checked, onCheckedChange = { checked = it }) {
            Text("文本")
        }
    }
}