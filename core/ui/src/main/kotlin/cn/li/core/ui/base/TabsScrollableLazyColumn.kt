package cn.li.core.ui.base

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch


/**
 * 侧边栏滚动+内容栏滚动，联动
 * @param tabsText 标签栏显示的内容
 * @param stickyHeaders 吸附效果的头部
 * @param items 内容
 * @param modifier
 * @param selectedTabsColor 选中的标签背景颜色
 * @param selectedTabsTextColor 选中的标签文字颜色
 * @param unselectedTabColor 未选中的标签文字颜色
 * @param unselectedTabTextColor 未选中的标签文字颜色
 * @param
 *
 * */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TabsScrollableLazyColumn(
    tabsText: List<String>,
    stickyHeaders: @Composable (index: Int, tab: String) -> Unit,
    items: @Composable (index: Int, tab: String) -> Unit,
    modifier: Modifier = Modifier,
    selectedTabsColor: Color = Color.White,
    selectedTabsTextColor: Color = Color.Black,
    unselectedTabColor: Color = Color.Transparent,
    unselectedTabTextColor: Color = Color.Gray,
    tabsSideColor: Color = Color(0xfff8f8f8),
    tabsWeight: Float = 1f,
    contentWeight: Float = 4f,
    contentPaddingValues: PaddingValues = PaddingValues(0.dp)
) {
    val sideTabsScrollState = rememberLazyListState()
    val contentScrollState = rememberLazyListState()
    // 当前选中下标
    var selectedTabsIndex by remember {
        mutableIntStateOf(0)
    }

    val scope = rememberCoroutineScope()

    // 当前第一个可见的信息
    val state by remember(key1 = contentScrollState) {
        derivedStateOf {
            contentScrollState.firstVisibleItemIndex / 2
        }
    }

    Surface(modifier) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = contentPaddingValues)
        ) {
            // 右边的滑动需要滚动到对应位置，触发左边的滑动
            LaunchedEffect(key1 = state) {
                if (tabsText.isNotEmpty()) {
                    sideTabsScrollState.animateScrollToCenter(state)
                    selectedTabsIndex = state
                }
            }
            if (tabsText.isNotEmpty()) {
                // 侧边标签栏
                LazyColumn(
                    modifier = Modifier
                        .weight(tabsWeight)
                        .fillMaxHeight()
                        .background(tabsSideColor),
                    state = sideTabsScrollState
                ) {
                    tabsText.forEachIndexed { index, text ->
                        item {
                            SideTabItem(selected = (selectedTabsIndex == index),
                                text = text,
                                selectedColor = selectedTabsColor, // 选中的标签背景颜色
                                unselectedColor = unselectedTabColor, // 未选中的标签背景颜色
                                selectedTextColor = selectedTabsTextColor, // 选中的标签文字颜色
                                unselectedTextColor = unselectedTabTextColor, // 未选中的标签文字颜色
                                onSelected = {
                                    // 左边的标签栏需要点击，才能触发右边的滑动
                                    scope.launch {
                                        selectedTabsIndex = index
                                        sideTabsScrollState.animateScrollToCenter(index)
                                    }
                                    scope.launch {
                                        contentScrollState.scrollToItem(index * 2)
                                    }
                                }
                            )
                        }
                    }
                }
                // 主内容栏
                LazyColumn(
                    modifier = Modifier
                        .weight(contentWeight)
                        .fillMaxHeight()
                        .background(color = Color.White),
                    state = contentScrollState
                ) {
                    tabsText.forEachIndexed { index, tab ->
                        stickyHeader {
                            stickyHeaders(index, tab)
                        }
                        item {
                            items(index, tab)
                        }
                    }
                }
            }
        }
    }
}


/**
 * 将指定 [index] 位置的子项滚动到列表的中间位置
 * */
suspend fun LazyListState.animateScrollToCenter(index: Int) {
    val itemInfo = layoutInfo.visibleItemsInfo.firstOrNull { it.index == index }
    when {
        itemInfo != null -> {
            // 列表的中心点
            val center = this@animateScrollToCenter.layoutInfo.viewportEndOffset / 2
            // 子项的中心点
            val childCenter = itemInfo.offset + itemInfo.size / 2
            // 滚动指定距离
            animateScrollBy(childCenter.toFloat() - center, animationSpec = TweenSpec())
        }

        else -> {
            animateScrollToItem(index)
        }
    }
}

/**
 * 侧边栏标签子项
 * @param selected 是否选中
 * @param text 标签内容
 * @param selectedColor 选中的背景颜色
 * @param selectedTextColor 选中的文字颜色
 * @param unselectedColor 未选中的背景颜色
 * @param unselectedTextColor 未选中的文字颜色
 * @param onSelected 点击事件回调
 * @param modifier
 * */
@Composable
fun SideTabItem(
    selected: Boolean,
    text: String,
    selectedColor: Color,
    selectedTextColor: Color,
    unselectedColor: Color,
    unselectedTextColor: Color,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(64.dp)
            .background(
                color = if (selected) selectedColor
                else unselectedColor
            )
            // 使用该方法避免涟漪效果
            .pointerInput(onSelected) {
                detectTapGestures {
                    onSelected()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) selectedTextColor
            else unselectedTextColor,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight(500),
            modifier = Modifier
                .fillMaxSize()
                .wrapContentHeight(Alignment.CenterVertically)
        )
        AnimatedVisibility(
            visible = selected, modifier = Modifier .align(Alignment.CenterStart)
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight(0.5f)
                    .clip(RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp))
                    .background(color = Color(0xFFF9A825))
            )
        }
    }
}


@Composable
@Preview(showBackground = true, showSystemUi = true)
private fun TabsScrollableLazyColumnPreview() {
    val tabsText = mutableListOf<String>().apply {
        for (i in 1..30) {
            add("tab$i")
        }
    }
    TabsScrollableLazyColumn(
        tabsText = tabsText,
        stickyHeaders = { _, tab ->
            Text(
                text = tab,
                modifier = Modifier
                    .padding(4.dp)
                    .background(Color.Blue),
                color = Color.White
            )
        },
        items = { index, _ ->
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                for (j in 0..5) {
                    Text(
                        text = "tab$index item$j",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .wrapContentSize(Alignment.Center),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    )
}