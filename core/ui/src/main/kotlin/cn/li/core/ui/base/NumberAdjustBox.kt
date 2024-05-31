package cn.li.core.ui.base

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


/**
 * 左右两侧带有加减按钮的数字选择框
 * @param value 当前值
 * @param onDecrease 点击减按钮回调，回调值为目标值
 * @param onIncrease 点击加按钮回调，回调值为目标值
 * @param enabledIncrease 是否允许点击加按钮
 * @param enabledDecrease 是否允许点击减按钮
 * */
@Composable
fun NumberAdjustBox(
    value: Int,
    onIncrease: (Int) -> Unit,
    onDecrease: (Int) -> Unit,
    modifier: Modifier = Modifier,
    limitMin: Int = 1,
    limitMax: Int = Int.MAX_VALUE
) {
    Row(
        modifier = modifier.height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                onDecrease(value - 1)
            },
            enabled = (value > limitMin),
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Remove,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .border(0.5.dp, Color.Gray, shape = CircleShape)
            )
        }
        Text(
            text = "$value",
            modifier = Modifier
                .fillMaxHeight()
                .width(48.dp)
                .wrapContentSize(Alignment.Center)
        )
        IconButton(
            onClick = {
                onIncrease(value + 1)
            },
            enabled = (value < limitMax),
            modifier = Modifier.size(24.dp)

        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color(0xffe8c279))
            )
        }
    }
}


@Composable
@Preview(showBackground = true)
private fun NumberAdjustBoxPreview() {
    var value by remember { mutableIntStateOf(2) }
    NumberAdjustBox(
        value = value,
        onDecrease = { value = it },
        onIncrease = { value = it }
    )
}