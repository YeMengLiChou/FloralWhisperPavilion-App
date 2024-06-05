@file:SuppressLint("UsingMaterialAndMaterial3Libraries")

package cn.li.feature.menu.ui

import android.annotation.SuppressLint
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import cn.li.core.ui.base.NumberAdjustBox
import cn.li.core.ui.bottom
import cn.li.core.ui.horizontalCenter
import cn.li.core.ui.top
import cn.li.model.CommodityItemDetailVO
import coil.compose.SubcomposeAsyncImage
import kotlinx.coroutines.launch

/**
 * 商品详细信息底部弹窗
 *
 * */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CommodityDetailLayout(
    uiState: CommodityDetailUiState,
    onDismiss: () -> Unit,
    onDismissRequest: () -> Unit,
    onAddCartClick: suspend (itemId: Long, count: Int) -> Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    // 底部弹窗状态
    val sheetState =
        rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
            confirmValueChange = {
                if (it == ModalBottomSheetValue.Hidden) {
                    onDismiss()
                }
                true
            },
            skipHalfExpanded = true,
            animationSpec = TweenSpec(durationMillis = 500)
        )

    var cachedData by remember {
        mutableStateOf<CommodityItemDetailVO?>(null)
    }

    LaunchedEffect(key1 = uiState) {
        when (uiState) {
            CommodityDetailUiState.Hide -> {
                launch {
                    sheetState.hide()
                    // 彻底关闭后清除该数据
                    cachedData = null
                }
            }

            is CommodityDetailUiState.Success -> {
                cachedData = uiState.commodityDetail
                launch { sheetState.show() }
            }

            else -> {
                launch { sheetState.show() }
            }
        }
    }

    val scope = rememberCoroutineScope()

    ModalBottomSheetLayout(
        modifier = modifier,
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        sheetContent = {
            when (uiState) {
                //
                is CommodityDetailUiState.Success,
                    // hide 的时候还是显示该部分数据，使过渡动画自然结束
                CommodityDetailUiState.Hide -> {
                    if (cachedData != null) {
                        CommodityDetail(
                            item = cachedData!!,
                            modifier = Modifier
                                .fillMaxHeight(0.8f)
                                .fillMaxWidth(),
                            onAddCartClick = { addCount ->
                                scope.launch {
                                    // 当只有返回 true 的时候才能关闭
                                    if (onAddCartClick(cachedData!!.id, addCount)) {
                                        onDismissRequest()
                                    }
                                }
                            }
                        )
                    }
                }

                is CommodityDetailUiState.Failed -> {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight(0.8f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = uiState.error)
                    }
                }

                CommodityDetailUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight(0.8f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        },
        sheetState = sheetState,
    ) {
        content() // 主内容
    }
}


/**
 * 商店商品详细信息
 * @param item 商品信息
 * @param onAddCartClick 添加购物车回调
 * */
@Composable
private fun CommodityDetail(
    item: CommodityItemDetailVO,
    onAddCartClick: (count: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val verticalScrollState = rememberScrollState()
    ConstraintLayout(modifier = modifier) {
        val (topRef, bottomRef) = createRefs()
        Column(
            modifier = Modifier
                .verticalScroll(
                    state = verticalScrollState
                )
                .constrainAs(topRef) {
                    top()
                    horizontalCenter()
                    bottom.linkTo(bottomRef.top)
                    height = Dimension.fillToConstraints
                }

        ) {
            SubcomposeAsyncImage(
                model = item.imageUrl,
                contentDescription = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.3f),
                contentScale = ContentScale.Crop
            )
            Text(
                text = item.name,
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 8.dp, start = 12.dp, end = 12.dp)
            )
            Text(
                text = item.description,
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp, start = 12.dp, end = 12.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            CommodityParam(
                item = item,
                modifier = Modifier
                    .padding(top = 16.dp, start = 12.dp, end = 12.dp)
                    .height(IntrinsicSize.Min)
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        var addCartCount by remember(key1 = item.id) {
            mutableIntStateOf(1)
        }
        Surface(
            shadowElevation = 4.dp,
            modifier = Modifier.constrainAs(bottomRef) {
                bottom()
            }
        ) {
            BottomAddCart(
                price = item.price,
                count = addCartCount,
                onAddCartClick = {
                    onAddCartClick(addCartCount)
                },
                onIncreaseCount = {
                    addCartCount = it
                },
                onDecreaseCount = {
                    addCartCount = it
                },
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )
        }
    }
}

/**
 * 底部的加入购物车+选择数量
 * @param price 显示的单价
 * @param count 显示的数量
 * @param onAddCartClick 点击加入购物车
 * @param onIncreaseCount 增加数量
 * @param onDecreaseCount 减少数量
 * */
@Composable
fun BottomAddCart(
    price: Double,
    onAddCartClick: () -> Unit,
    onIncreaseCount: (Int) -> Unit,
    onDecreaseCount: (Int) -> Unit,
    modifier: Modifier = Modifier,
    count: Int = 1,
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "¥$price", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            NumberAdjustBox(
                value = count,
                onIncrease = onIncreaseCount,
                onDecrease = onDecreaseCount,
                limitMax = 100,
                limitMin = 1,
                modifier = Modifier
            )
        }

        Button(
            onClick = onAddCartClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(50)
        ) {
            Text(
                text = "加入购物车",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                textAlign = TextAlign.Center,
                color = Color.White,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun CommodityParam(
    item: CommodityItemDetailVO,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(text = "商品参数")
        Spacer(modifier = Modifier.height(8.dp))
        Table(
            items = listOf(
                "主花材" to item.mainName,
                "枝数/朵数" to item.mainNumber.toString(),
                "产地" to item.mainOrigin,
                "色系" to item.mainColor,
                "配花/配叶" to item.mainDecorate,
                "款式" to item.mainStyle,
                "适用人群" to item.mainPeople
            )
        )

    }
}


@Composable
fun Table(
    items: List<Pair<String, String>>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .border(
                0.5.dp,
                color = Color.LightGray,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        items.forEachIndexed { index, pair ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
                Text(
                    text = pair.first,
                    modifier = Modifier
                        .fillMaxWidth(0.3f)
                        .background(Color(0xfff0f0f0))
                        .padding(start = 8.dp, top = 4.dp, bottom = 4.dp)
                        .height(30.dp)
                        .wrapContentHeight(Alignment.CenterVertically),
                    color = Color.Gray
                )
                VerticalDivider(
                    thickness = 0.5.dp,
                    color = Color.LightGray,
                )
                Text(
                    text = pair.second,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, top = 4.dp, bottom = 4.dp)
                        .height(30.dp)
                        .wrapContentHeight(Alignment.CenterVertically)
                )
            }
            if (index != items.size - 1) {
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 0.5.dp,
                    color = Color.LightGray
                )
            }
        }
    }
}


@Composable
@Preview(showBackground = true)
private fun TablePreview() {
    Table(items = listOf("1" to "2", "3" to "4"))
}


//@Composable
//@Preview(showBackground = true)
//fun CommodityDetailLayoutPreview() {
//    CommodityDetailLayout(uiState = CommodityDetailUiState.Loading, onDismiss = {}, onDismissRequest = {}, onAddCartClick = ) {
//
//    }
//}