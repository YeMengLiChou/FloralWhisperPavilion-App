package cn.li.feature.menu.ui.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.ModalBottomSheetLayout
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.ModalBottomSheetState
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.ShoppingBag
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastFirst
import androidx.constraintlayout.compose.layoutId
import kotlinx.coroutines.launch
import kotlin.math.max


/***
 * 悬浮购物车布局
 * @param badgeCount 显示的商品数量，角标显示
 * @param amount 显示的金额
 * @param onSettlement 点击去结算的回调
 * @param sheetContent 购物车底部的布局
 * @param modifier
 * @param content 主布局内容
 * */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FloatingCartLayout(
    showCart: Boolean,
    badgeCount: Int,
    amount: String,
    onSettlement: () -> Unit,
    sheetContent: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {

    val sheetState: ModalBottomSheetState =
        rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)

    val scope = rememberCoroutineScope()

    Box(modifier) {
        ModalBottomSheetLayout(
            sheetContent = {
                Text(
                    text = "商品",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .padding(vertical = 2.dp)
                )
                sheetContent()
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                )
            },
            sheetState = sheetState,
            sheetShape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
        ) {
            content()
        }

        if (showCart) {
            FloatingDetails(
                badge = badgeCount,
                amount = amount,
                modifier = Modifier.align(Alignment.BottomCenter),
                onSettlement = onSettlement,
                isExpanded = sheetState.currentValue != ModalBottomSheetValue.Hidden,
                onDetail = {
                    scope.launch { if (sheetState.currentValue == ModalBottomSheetValue.Hidden) sheetState.show() else sheetState.hide() }
                },
            )
        }
    }
}


/**
 * 悬浮购物车详情
 * @param badge 角标
 * @param amount 金额
 * @param onSettlement 点击去结算的回调
 * @param onDetail 点击详情的回调
 * @param modifier
 * */
@Composable
private fun FloatingDetails(
    badge: Int,
    amount: String,
    onSettlement: () -> Unit,
    onDetail: () -> Unit,
    modifier: Modifier = Modifier,
    isExpanded: Boolean = false,
    badgeColor: Color = Color.White,
) {
    Surface(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .padding(12.dp)
            .height(48.dp),
        shadowElevation = 6.dp,
        shape = RoundedCornerShape(8.dp),
        color = Color.Black,
        contentColor = Color.White,
    ) {
        Layout(
            content = {
                val badgeText = if (badge >= 100) "99+" else if (badge > 0) badge.toString() else ""
                val textMeasure = rememberTextMeasurer()
                val textResult = remember(badge) {
                    textMeasure.measure(
                        badgeText,
                        style = TextStyle(fontSize = 10.sp, textAlign = TextAlign.Center)
                    )
                }
                // 左侧图标
                Icon(
                    imageVector = Icons.Outlined.ShoppingBag,
                    contentDescription = null,
                    modifier = Modifier
                        .layoutId("icon")
                        .aspectRatio(1f)
                        .padding(6.dp)
                        .drawWithCache {
                            // 绘制右上角的角标
                            onDrawBehind {
                                val radius = 8.dp.toPx()
                                drawCircle(
                                    color = badgeColor,
                                    radius = radius,
                                    center = Offset(
                                        size.width,
                                        radius / 2
                                    )
                                )
                                // 角标中间的文字
                                drawText(
                                    textResult,
                                    topLeft = Offset(
                                        size.width - textResult.size.width / 2f,
                                        radius / 2 - textResult.size.height / 2f
                                    )
                                )
                            }
                        }
                )
                // 金额
                Text(
                    text = "¥${amount}",
                    modifier = Modifier
                        .layoutId("amount")
                        .wrapContentSize(Alignment.Center),
                    fontSize = 18.sp,
                    fontWeight = FontWeight(600)
                )
                // 明细
                Row(
                    modifier = Modifier
                        .layoutId("detail")
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "明细",
                        modifier = Modifier.wrapContentSize(Alignment.Center),
                        fontSize = 10.sp
                    )
                    Icon(
                        imageVector = when (isExpanded) {
                            true -> Icons.Outlined.KeyboardArrowUp
                            false -> Icons.Outlined.KeyboardArrowDown
                        },
                        contentDescription = null,
                        modifier = Modifier.size(12.dp)
                    )
                }
                Text(
                    text = "去结算",
                    modifier = Modifier
                        .layoutId("settle")
                        .background(Color(0xff313131))
                        .wrapContentSize(Alignment.Center)
                        .padding(horizontal = 12.dp)
                        .pointerInput(onSettlement) {
                            detectTapGestures { onSettlement() }
                        },
                    color = Color(0xffceb27d)
                )
            },
            modifier = Modifier.pointerInput(onDetail) {
                detectTapGestures {
                    onDetail()
                }
            }
        ) { measurables, constraints ->
            val iconPlaceable = measurables.fastFirst { it.layoutId == "icon" }.measure(constraints)
            val amountPlaceable =
                measurables.fastFirst { it.layoutId == "amount" }.measure(constraints)
            val detailPlaceable =
                measurables.fastFirst { it.layoutId == "detail" }.measure(constraints)
            val settlePlaceable =
                measurables.fastFirst { it.layoutId == "settle" }.measure(constraints)
            val containerWidth = max(
                constraints.maxWidth,
                iconPlaceable.width + amountPlaceable.width + detailPlaceable.width + settlePlaceable.width
            )
            val containerHeight = 48.dp.roundToPx()

            layout(containerWidth, containerHeight) {
                iconPlaceable.placeRelative(0, 0)
                amountPlaceable.placeRelative(iconPlaceable.width, 0)
                detailPlaceable.placeRelative(
                    iconPlaceable.width + amountPlaceable.width,
                    0
                )
                settlePlaceable.placeRelative(
                    containerWidth - settlePlaceable.width,
                    0
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun FloatCartPreview() {
    Box(modifier = Modifier.fillMaxSize()) {
        FloatingCartLayout(
            showCart = true,
            badgeCount = 12,
            amount = "123",
            sheetContent = {},
            onSettlement = {},
        ) {
        }
    }
}