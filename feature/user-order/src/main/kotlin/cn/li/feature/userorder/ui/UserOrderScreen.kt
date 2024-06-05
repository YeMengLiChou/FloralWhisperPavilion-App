package cn.li.feature.userorder.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.layoutId
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import cn.li.core.ui.end
import cn.li.core.ui.start
import cn.li.core.ui.top
import cn.li.feature.userorder.UserOrderUiState
import cn.li.feature.userorder.vo.UserOrderItemVo
import coil.compose.SubcomposeAsyncImage
import kotlinx.coroutines.launch
import kotlin.math.min

@Composable
fun UserOrderScreen(
    uiState: UserOrderUiState,
    onClickItem: (UserOrderItemVo) -> Unit,
    completeItems: LazyPagingItems<UserOrderItemVo>,
    uncompletedItems: LazyPagingItems<UserOrderItemVo>,
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
                        .wrapContentSize(Alignment.Center),
                    fontSize = 20.sp
                )
            }

            val pagerState = rememberPagerState {
                2
            }
            val scope = rememberCoroutineScope()

            // 标签栏
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
                            scope.launch { pagerState.animateScrollToPage(index) }
                        },
                    ) {
                        Text(text = tab, modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
            // 底部展示的内容
            HorizontalPager(state = pagerState) { pageIndex ->
                selectedTabIndex = pageIndex
                when (pageIndex) {
                    0 -> {
                        UncompletedOrderPage(
                            items = uncompletedItems,
                            modifier = Modifier.fillMaxSize(),
                            onClick = onClickItem
                        )
                    }

                    1 -> {
                        CompletedOrderPage(
                            items = completeItems,
                            modifier = Modifier.fillMaxSize(),
                            onClick = onClickItem
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun UncompletedOrderPage(
    items: LazyPagingItems<UserOrderItemVo>,
    onClick: (UserOrderItemVo) -> Unit,
    modifier: Modifier = Modifier
) {

    LazyColumn(modifier = modifier) {
        if (items.loadState.refresh == LoadState.Loading) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .height(240.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                    Text(text = "加载中...")
                }
            }
        }
        if (items.itemCount == 0) {
            item {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "暂无数据",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .wrapContentSize(Alignment.Center),
                    )
                }
            }
        } else {
            items(items.itemCount) {
                val item = items[it]
                if (item != null) {
                    OrderItem(
                        item = item,
                        modifier = Modifier
                            .padding(top = 8.dp, start = 16.dp, end = 16.dp),
                        onItemClick = {
                            onClick(item)

                        }
                    )
                }
            }
        }
        if (items.loadState.append == LoadState.Loading) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                    Text(text = "加载中...")
                }
            }
        }
    }
}

@Composable
private fun CompletedOrderPage(
    items: LazyPagingItems<UserOrderItemVo>,
    onClick: (UserOrderItemVo) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        if (items.loadState.refresh == LoadState.Loading) {
            item {
                Column(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator()
                    Text(text = "加载中...")
                }
            }
        }
        if (items.itemCount == 0) {
            item {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "暂无数据",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .wrapContentSize(Alignment.Center),
                    )
                }
            }
        } else {
            items(items.itemCount) {
                val item = items[it]
                if (item != null) {
                    OrderItem(
                        item = item,
                        modifier = Modifier
                            .padding(top = 8.dp, start = 8.dp, end = 8.dp),
                        onItemClick = {
                            onClick(item)
                        }
                    )
                }
            }
        }
        if (items.loadState.append == LoadState.Loading) {
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    CircularProgressIndicator()
                    Text(text = "加载中...")
                }
            }
        }
    }
}

fun OrderItem(item: UserOrderItemVo, modifier: Modifier) {

}


/**
 * 状态
 * */
@Composable
private fun OrderItem(
    item: UserOrderItemVo,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier,
    constraintSet: ConstraintSet = UserOrderScreenDefaults.orderItemConstraintSet,
) {
    Card(
        modifier = modifier.fillMaxSize(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        onClick = onItemClick,
    ) {
        ConstraintLayout(
            constraintSet = constraintSet,
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp, horizontal = 8.dp)
        ) {
            Column(modifier = Modifier.layoutId("address")) {
                Text(text = item.shopName, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = item.submitOrderTime, fontSize = 12.sp, color = Color.Gray)
            }

            Text(text = item.statusText, modifier = Modifier.layoutId("status"))

            Row(
                modifier = Modifier
                    .layoutId("image")
                    .height(IntrinsicSize.Min)
            ) {
                if (item.commodityList.size == 1) {
                    SubcomposeAsyncImage(
                        model = item.commodityList.first().imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.commodityList.first().name,
                        modifier = Modifier
                            .fillMaxHeight()
                            .wrapContentHeight(Alignment.CenterVertically),
                    )
                } else {
                    val omitVisibility = item.commodityList.size > 3
                    for (i in 0 until min(3, item.commodityList.size)) {
                        SubcomposeAsyncImage(
                            model = item.commodityList[i].imageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    if (omitVisibility) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "...",
                            modifier = Modifier
                                .fillMaxHeight()
                                .wrapContentHeight(Alignment.Bottom),
                        )
                    }
                }
            }
            Column(modifier = Modifier.layoutId("detail"), horizontalAlignment = Alignment.End) {
                Text(text = "¥${item.orderTotalAmount}", fontSize = 16.sp)
                Text(text = "共${item.commodityCount}件", fontSize = 14.sp, color = Color.Gray)
            }
        }
    }
}


object UserOrderScreenDefaults {

    val orderItemConstraintSet = ConstraintSet {
        val (addressRef, statusRef, detailRef, imageRef) = createRefsFor(
            "address", "status", "detail", "image"
        )

        constrain(addressRef) {
            top()
            start()
        }
        constrain(statusRef) {
            top()
            end()
        }
        constrain(detailRef) {
            top.linkTo(imageRef.top)
            bottom.linkTo(imageRef.bottom)
            end()
        }
        constrain(imageRef) {
            start()
            top.linkTo(addressRef.bottom, 8.dp)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OrderItemPreview() {
    Box(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        OrderItem(
            item = UserOrderItemVo(
                orderId = 1L,
                orderTotalAmount = "100.00",
                orderNumber = "123456789",
                shopName = "店铺名称",
                statusText = "待付款",
                completedOrderTime = "",
                submitOrderTime = "",
                commodityCount = 1,
                shopAddress = "",
                commodityList = listOf(),
                status = 0
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(128.dp)
                .padding(8.dp),
        )
    }
}