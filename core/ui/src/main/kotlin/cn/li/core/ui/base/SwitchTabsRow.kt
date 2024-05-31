package cn.li.core.ui.base

import androidx.compose.animation.Animatable
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/**
 * 使没有涟漪效果
 * */
private class NoRippleInteractionSource : MutableInteractionSource {
    override val interactions: Flow<Interaction> = emptyFlow()
    override suspend fun emit(interaction: Interaction) {}
    override fun tryEmit(interaction: Interaction) = true
}

/***
 * 切换的tab
 * @param selectedIndex 选中的索引
 * @param tabs 标签文本
 * @param onTabsClick 点击事件
 * @param containerColor 背景颜色
 * */
@Composable
fun SwitchTabs(
    selectedIndex: Int,
    tabs: List<String>,
    onTabsClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = Color.White,
    indicatorSelectedColor: Color = Color.Black,
    selectedTextColor: Color = Color.White,
    unselectedTextColor: Color = Color.Black
) {
    TabRow(
        selectedTabIndex = selectedIndex,
        containerColor = containerColor,
        modifier = modifier
            .clip(RoundedCornerShape(50)),
        indicator = { tabsPosition ->
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier
                    .tabIndicatorOffset(tabsPosition[selectedIndex])
                    .fillMaxSize()
                    .padding(1.dp)
                    .clip(RoundedCornerShape(50)),
                color = indicatorSelectedColor,
            )
        }
    ) {
        tabs.forEachIndexed { index, s ->
            val selected = (index == selectedIndex)
            val textColor = remember {
                Animatable(if (selected) selectedTextColor else unselectedTextColor)
            }
            LaunchedEffect(key1 = selected) {
                textColor.animateTo(if (selected) selectedTextColor else unselectedTextColor)
            }
            Tab(
                selected = selected,
                onClick = {
                    onTabsClick(index)
                },
                text = {
                    Text(text = s, color = textColor.value)
                },
                modifier = Modifier
                    .zIndex(2f),
                selectedContentColor = selectedTextColor,
                unselectedContentColor = unselectedTextColor,
                interactionSource = NoRippleInteractionSource()
            )
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SwitchTabsPreview() {
    SwitchTabs(
        selectedIndex = 0,
        tabs = listOf("自取", "外卖"),
        onTabsClick = {}
    )
}