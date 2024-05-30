package cn.li.feature.userorder.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import cn.li.feature.userorder.UserOrderUiState
import cn.li.network.dto.user.OrderDetailDTO
import kotlinx.coroutines.launch

@Composable
fun UserOrderScreen(
    uiState: UserOrderUiState,
    completeItems: LazyPagingItems<OrderDetailDTO>,
    uncompletedItems: LazyPagingItems<OrderDetailDTO>,
    onUncompletedOrderSwitch: () -> Unit,
    onHistoryOrderSwitch: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedTabIndex by rememberSaveable {
        mutableIntStateOf(0)
    }

    val tabs = listOf("未完成订单", "历史订单")
    Surface(modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
            ) {
                Text(
                    text = "订单列表",
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center)
                )
            }

            val pagerState = rememberPagerState {
                2
            }
            val scope = rememberCoroutineScope()
            TabRow(selectedTabIndex = selectedTabIndex, indicator = { tabPositions ->
                if (selectedTabIndex < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(
                            tabPositions[selectedTabIndex]
                        )
                    )
                }
            }) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = {
                            selectedTabIndex = index
                            when (index) {
                                0 -> {
                                    onUncompletedOrderSwitch()
                                }

                                1 -> {
                                    onHistoryOrderSwitch()
                                }
                            }
                            scope.launch { pagerState.animateScrollToPage(index) }
                        },
                    ) {
                        Text(text = tab, modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }
            HorizontalPager(state = pagerState) { pageIndex ->
                when (pageIndex) {
                    0 -> {
                        UncompletedOrderPage(items = uncompletedItems)
                    }

                    1 -> {
                        CompletedOrderPage(items = completeItems)
                    }
                }
            }
        }
    }
}


@Composable
fun UncompletedOrderPage(
    items: LazyPagingItems<OrderDetailDTO>,
    modifier: Modifier = Modifier
) {
    if (items.itemCount != 0) {
        LazyColumn(modifier = modifier) {
            items(items.itemCount) { index ->
                val item = items[index]
                if (item != null) {

                }
            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(text = "暂无数据")
        }
    }
}

@Composable
fun CompletedOrderPage(
    items: LazyPagingItems<OrderDetailDTO>,
    modifier: Modifier = Modifier
) {
    if (items.itemCount != 0) {
        LazyColumn(modifier = modifier) {
            items(items.itemCount) { index ->
                val item = items[index]
                if (item != null) {

                }
            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(text = "暂无数据")
        }
    }
}
