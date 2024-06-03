package cn.li.core.ui.base

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.li.core.ui.start


@Composable
fun TextLabel(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 14.sp,
    roundRadius: Dp = 4.dp,
    color: Color = Color(0xff03a9f4),
    backgroundColor: Color = Color(0xffb3e5fc)
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(shape = RoundedCornerShape(roundRadius))
            .background(color = backgroundColor).then(modifier)
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            modifier = Modifier,
            color = color,
            fontSize = fontSize
        )
    }
}


@Composable
@Preview(showBackground = true)
private fun TextLabelPreview() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        TextLabel(text = "营业中")
    }
}