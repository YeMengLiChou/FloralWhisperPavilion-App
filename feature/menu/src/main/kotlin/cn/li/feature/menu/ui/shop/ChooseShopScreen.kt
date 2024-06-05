package cn.li.feature.menu.ui.shop

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.layoutId
import cn.li.core.ui.base.SwipeRefreshBox
import cn.li.core.ui.base.TopBarWithBack
import cn.li.core.ui.loading.LoadingBottomSheetLayout
import cn.li.core.ui.start
import cn.li.core.ui.top
import cn.li.feature.menu.R
import cn.li.feature.menu.ui.ChooseShopUiState
import cn.li.network.dto.user.ShopItemDTO
import kotlinx.coroutines.delay

@Composable
fun ChooseShopScreen(
    uiState: ChooseShopUiState,
    selectedShopId: Long?,
    onBackClick: () -> Unit,
    onShopSelected: (shopId: Long) -> Unit,
    onSettlementClick: (shopInfo: ShopItemDTO) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 加载状态
    val loading by remember(key1 = uiState) {
        derivedStateOf {
            uiState is ChooseShopUiState.Loading
        }
    }
    // 刷新状态
    var refreshState by remember {
        mutableStateOf(false)
    }

    // 缓存数据
    val cachedShopList = remember {
        mutableStateOf(emptyList<ShopItemDTO>())
    }

    // 底部是否显示
    var bottomShowState by remember {
        mutableStateOf(false)
    }

    // 延长1.5s后隐藏，用于提示
    LaunchedEffect(key1 = bottomShowState) {
        if (bottomShowState) {
            delay(1500L)
            bottomShowState = false
        }
    }

    when (uiState) {
        is ChooseShopUiState.Success -> {
            cachedShopList.value = uiState.shopList
            refreshState = false
        }

        is ChooseShopUiState.Failed -> {
            refreshState = false
        }

        ChooseShopUiState.Loading -> {

        }
    }
    Surface(modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            LoadingBottomSheetLayout(
                show = bottomShowState,
                loading = loading,
                sheetContent = {
                    Text(
                        text = "请选择一家门店！",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .wrapContentSize(Alignment.Center)
                    )
                },
            ) {
                Column {
                    TopBarWithBack(
                        onBackClick = onBackClick,
                        title = "选择门店",
                        modifier = Modifier
                            .fillMaxWidth()
                            .systemBarsPadding()
                    )

                    SwipeRefreshBox(
                        refreshing = refreshState,
                        onRefresh = {
                            onRefresh()
                            refreshState = true
                        },
                        modifier = Modifier
                    ) {
                        Column {
                            cachedShopList.value.forEach { shopInfo ->
                                ShopInfoItem(
                                    selected = (shopInfo.id == selectedShopId),
                                    shopItemDTO = shopInfo,
                                    onClick = { onShopSelected(shopInfo.id) },
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(64.dp))
                        }
                    }
                }
                // 下单按钮
                AnimatedVisibility(
                    visible = selectedShopId != null, modifier = Modifier
                        .height(48.dp)
                        .align(Alignment.BottomCenter)
                        .offset(x = 0.dp, y = -(16).dp)
                ) {
                    Button(
                        onClick = {
                            onSettlementClick(cachedShopList.value.first { it.id == selectedShopId })
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.9f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(text = "去下单", color = Color.White)
                    }
                }

            }

        }
    }
}


private val shopInfoItemConstraintSet = ConstraintSet {
    val (shopNameRef, addressRef, timeRef) = createRefsFor(
        "shop-name", "address", "time"
    )

    constrain(shopNameRef) {
        top()
        start()
    }
    constrain(addressRef) {
        top.linkTo(shopNameRef.bottom, 4.dp)
        start()
    }
    constrain(timeRef) {
        top.linkTo(addressRef.bottom, 4.dp)
        start()
    }
}


@Composable
fun ShopInfoItem(
    selected: Boolean,
    shopItemDTO: ShopItemDTO,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    constraintSet: ConstraintSet = shopInfoItemConstraintSet
) {
    Card(
        modifier = Modifier
            .pointerInput(onClick) {
                detectTapGestures { onClick() }
            }
            .then(modifier),
        border = if (selected) BorderStroke(
            width = 0.5.dp,
            color = Color(0xff1E90FF)
        ) else null,
        colors = CardDefaults.cardColors().copy(
            containerColor = Color.White,
        ),
        elevation = CardDefaults.elevatedCardElevation()
    ) {
        // 绘制右上角的选中角标
        val drawModifier = if (selected) {
            Modifier.drawTopRightBadge(
                size = 40.dp,
                painter = rememberVectorPainter(image = ImageVector.vectorResource(id = R.drawable.feature_menu_tick))
            )
        } else {
            Modifier
        }
        // 主要内容
        ConstraintLayout(
            modifier = drawModifier
                .fillMaxSize()
                .then(drawModifier)
                .padding(vertical = 16.dp, horizontal = 12.dp),
            constraintSet = constraintSet
        ) {
            // 商店名
            Text(
                text = shopItemDTO.name,
                modifier = Modifier.layoutId("shop-name"),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            )
            // 地址
            Row(modifier = Modifier.layoutId("address")) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    modifier = Modifier
                        .size(20.dp)
                        .alignByBaseline()

                )
                Spacer(modifier = Modifier.width(2.dp))

                Text(text = shopItemDTO.address, color = Color(0xff929292))
            }
            // 营业时间
            Row(
                modifier = Modifier
                    .layoutId("time")
                    .height(IntrinsicSize.Min)
            ) {
                Icon(
                    imageVector = Icons.Outlined.AccessTime,
                    contentDescription = null,
                    modifier = Modifier
                        .size(20.dp)
                        .padding(1.dp)
                        .alignByBaseline()
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "${shopItemDTO.startTime}-${shopItemDTO.endTime}",
                    color = Color(0xff929292),
                    modifier = Modifier
                        .fillMaxHeight()
                        .wrapContentSize(Alignment.Center)
                )
            }
        }
    }
}

/**
 * 绘制顶部右上角勾
 * */
private fun Modifier.drawTopRightBadge(
    color: Color = Color(0xff1E90FF),
    size: Dp,
    painter: VectorPainter
) = this then Modifier.drawWithCache {
    val w = size.toPx()
    val rectSize = this@drawWithCache.size
    onDrawBehind {
        val normalSize = painter.intrinsicSize.width * 2
        val scale = w / normalSize
        translate(left = rectSize.width - w, top = 0f) {
            scale(scale = scale, pivot = Offset(0f, 0f)) {
                drawPath(
                    path = Path().apply {
                        moveTo(0f, 0f)
                        lineTo(normalSize, normalSize)
                        lineTo(normalSize, 0f)
                        close()
                    },
                    color = color
                )
                with(painter) {
                    translate(left = normalSize / 2, top = painter.intrinsicSize.height / 4) {
                        draw(
                            size = painter.intrinsicSize,
                            colorFilter = ColorFilter.tint(Color.White)
                        )
                    }
                }
            }
        }
    }
}


@Composable
@Preview(showBackground = true, showSystemUi = true)
private fun ChooseShopScreenPreview() {
    ChooseShopScreen(
        onBackClick = {},
        onShopSelected = {},
        uiState = ChooseShopUiState.Success(
            listOf(
                ShopItemDTO(
                    id = 1L,
                    name = "桂林电子科大店",
                    startTime = "09:00",
                    endTime = "12:00",
                    status = 1,
                    address = "广西壮族自治区桂林市龙胜各族自治县电子科大店"
                )
            )
        ),
        onRefresh = {},
        selectedShopId = 1,
        onSettlementClick = {},
    )
}


@Composable
@Preview(showBackground = true)
private fun ShopInfoItemPreview() {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .height(100.dp), contentAlignment = Alignment.Center
    ) {
        ShopInfoItem(
            selected = true, shopItemDTO = ShopItemDTO(
                id = 1L,
                name = "桂林电子科大店",
                startTime = "09:00",
                endTime = "12:00",
                status = 1,
                address = "广西壮族自治区桂林市龙胜各族自治县电子科大店"

            ), modifier = Modifier.padding(8.dp),
            onClick = {}
        )
    }
}