package cn.li.feature.menu.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.layoutId
import cn.li.core.ui.base.SwitchTabs
import cn.li.core.ui.base.TabsScrollableLazyColumn
import cn.li.core.ui.base.TextLabel
import cn.li.core.ui.bottom
import cn.li.core.ui.end
import cn.li.core.ui.loading.LoadingBottomSheetLayout
import cn.li.core.ui.start
import cn.li.core.ui.top
import cn.li.feature.menu.ui.cart.FloatingCartLayout
import cn.li.model.CommodityItemVO
import cn.li.model.ShopCommodityItemVO
import cn.li.network.dto.user.ShopItemDTO
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import kotlin.math.max


/**
 * @param uiState
 * @param commodityDetailUiState
 * @param onSettlementNavigate 跳转到下单界面
 * @param onChooseShopNavigate 跳转到选择店铺界面
 * @param onAddCommodityToCart 添加商品到购物车
 * @param onCommodityDetailShow 显示商品详情
 * @param onCommodityDetailDismiss 隐藏商品详情
 * @param onSearchNavigation 搜索优先级最低
 * @param modifier
 * */
@Composable
fun MenuScreen(
    uiState: MenuUiState,
    commodityDetailUiState: CommodityDetailUiState,
    onSettlementNavigate: () -> Unit,
    onChooseShopNavigate: () -> Unit,
    onAddCommodityToCart: (itemId: Long, count: Int) -> Unit,
    onCommodityDetailShow: (id: Long) -> Unit,
    onCommodityDetailDismiss: () -> Unit,
    onSearchNavigation: () -> Unit, // 搜索优先级最低
    modifier: Modifier = Modifier
) {
    var cachedData by remember {
        mutableStateOf<MenuUiState.Success?>(null)
    }

    val loading by remember(key1 = uiState) {
        derivedStateOf { uiState == MenuUiState.Loading }
    }

    // 能否支付，显示订单按钮
    val orderEnabled by remember(key1 = cachedData) {
        derivedStateOf {
            // 营业状态
            cachedData?.shopInfo?.status == ShopItemDTO.STATUS_IN_BUSINESS
                    // 购物车不为空
                    && cachedData?.cartInfo?.isNotEmpty() == true
        }
    }

    // 订单的总金额
    val orderAmount by remember(key1 = cachedData) {
        derivedStateOf { cachedData?.cartInfo?.sumOf { it.amount } ?: 0.0 }
    }

    // 侧边栏
    val categoriesTabText = remember(key1 = cachedData) {
        cachedData?.goods?.map { it.categoryName } ?: emptyList()
    }

//    val contentItems = remember(key1 = cachedData?.goods) {
//        cachedData?.goods
//    }

    when (uiState) {
        MenuUiState.Loading -> {}
        is MenuUiState.Success -> {
            cachedData = uiState
        }

        is MenuUiState.Failed -> {
            // TODO 错误提示
        }
        else -> {}
    }

    Surface(modifier = modifier) {
        // 底部加载栏
        LoadingBottomSheetLayout(
            loading = loading,
            show = false,
            modifier = Modifier.fillMaxSize()
        ) {
            // 详情
            CommodityDetailLayout(
                uiState = commodityDetailUiState,
                modifier = Modifier.fillMaxSize(),
                onDismiss = onCommodityDetailDismiss,
                onDismissRequest = onCommodityDetailDismiss,
                onAddCartClick = { id, addCount, isSetmeal ->
                    onAddCommodityToCart(id, addCount)
                }
            ) { // 购物车
                FloatingCartLayout(
                    showCart = orderEnabled,
                    badgeCount = cachedData?.cartInfo?.size ?: 0,
                    amount = orderAmount.toString(),
                    onSettlement = onSettlementNavigate,
                    sheetContent = {
                    },
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        MenuTopBar(
                            modifier = Modifier
                                .height(IntrinsicSize.Min),
                            onSetTakeout = {

                            },
                            onSetSelfTake = {

                            },
                            onSearchClick = onSearchNavigation,
                            onShopClick = onChooseShopNavigate,
                            shopStatus = when (cachedData?.shopInfo?.status) {
                                ShopItemDTO.STATUS_IN_BUSINESS -> "营业中"
                                ShopItemDTO.STATUS_NOT_IN_BUSINESS -> "已打烊"
                                else -> ""
                            },
                            shopName = cachedData?.shopInfo?.name ?: "未选择",
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            TabsScrollableLazyColumn(
                                tabsText = categoriesTabText,
                                modifier = Modifier.fillMaxSize(),
                                stickyHeaders = { _, tab ->
                                    Text(
                                        text = tab,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color.White)
                                            .height(48.dp)
                                            .padding(start = 16.dp)
                                            .wrapContentSize(Alignment.CenterStart),
                                        fontWeight = FontWeight.W600,

                                        )
                                },
                                items = { index, _ ->
                                    cachedData?.goods?.get(index)?.let { goods ->
                                        Column {
                                            goods.items.forEach { item ->
                                                CommodityItem(
                                                    imageUrl = item.imageUrl,
                                                    name = item.name,
                                                    price = "¥${item.price}",
                                                    cartCount = item.cartCount,
                                                    description = item.description,
                                                    onAddClick = {
                                                        onCommodityDetailShow(item.id)

                                                    },
                                                    modifier = Modifier
                                                        .padding(
                                                            top = 12.dp,
                                                            start = 16.dp,
                                                            bottom = 16.dp,
                                                        )
                                                        .height(128.dp)
                                                )
                                            }
                                        }
                                    }
                                },
                                contentPaddingValues = PaddingValues(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun EmptyCart(
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(text = "暂无心仪的商品~快去选购吧~")
    }
}

/**
 * 顶部栏
 * @param onSetTakeout 切换到外卖
 * @param onSetSelfTake 切换到自取
 * @param onSearchClick 点击搜索
 * @param onShopClick 点击商店
 * */
@Composable
private fun MenuTopBar(
    shopName: String,
    shopStatus: String,
    onSetSelfTake: () -> Unit,
    onSetTakeout: () -> Unit,
    onSearchClick: () -> Unit,
    onShopClick: () -> Unit,
    modifier: Modifier = Modifier,
    constraintSet: ConstraintSet = MenuScreenDefaults.topBarConstraintSet()
) {
    var selectedIndex by remember {
        mutableIntStateOf(0)
    }
    val tabs = listOf("自取", "外卖")

    Surface(
        color = Color.White, modifier = modifier, shadowElevation = 6.dp
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(vertical = 8.dp, horizontal = 12.dp),
            constraintSet = constraintSet
        ) {
            SwitchTabs(
                selectedIndex = selectedIndex,
                tabs = tabs,
                modifier = Modifier
                    .width(120.dp)
                    .height(40.dp)
                    .border(
                        width = 0.5.dp,
                        color = Color(0xffe0e0e0),
                        shape = RoundedCornerShape(50)
                    )
                    .layoutId("tabs"),
                onTabsClick = {
                    selectedIndex = it
                    if (it == 0) onSetTakeout()
                    else onSetSelfTake()
                }
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .border(
                        width = 0.5.dp,
                        shape = RoundedCornerShape(50),
                        color = Color(0xffe0e0e0)
                    )
                    .height(40.dp)
                    .padding(vertical = 6.dp, horizontal = 12.dp)
                    .layoutId("search")
                    .pointerInput(onSearchClick) {
                        detectTapGestures { onSearchClick() }
                    }
            ) {
                Icon(imageVector = Icons.Outlined.Search, contentDescription = null)
                Text(
                    text = "搜索商品",
                    color = Color(0xff101010),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.Center),
                    textAlign = TextAlign.Center,
                )
            }

            Row(
                modifier = Modifier
                    .layoutId("shopName")
                    .pointerInput(onShopClick) {
                        detectTapGestures { onShopClick() }
                    },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = shopName, fontSize = 20.sp, fontWeight = FontWeight(600))
                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.padding(2.dp)
                )
            }
            TextLabel(text = shopStatus, modifier = Modifier.layoutId("status"))

        }
    }
}


object MenuScreenDefaults {
    @Composable
    fun topBarConstraintSet() = ConstraintSet {
        val (tabsRef, searchRef, shopNameRef, statusRef) = createRefsFor(
            "tabs", "search", "shopName", "status"
        )

        constrain(tabsRef) {
            start()
        }

        constrain(searchRef) {
            start.linkTo(tabsRef.end, 8.dp)
            end()
            width = Dimension.preferredWrapContent
        }

        constrain(shopNameRef) {
            start(4.dp)
            top.linkTo(tabsRef.bottom, 8.dp)
        }
        constrain(statusRef) {
            start(4.dp)
            top.linkTo(shopNameRef.bottom, 4.dp)
        }
    }

    @Composable
    fun commodityItemConstraintSet() = ConstraintSet {
        val (imageRef, nameRef, descRef, priceRef, addRef) = createRefsFor(
            "image", "name", "desc", "price", "add"
        )
        constrain(imageRef) {
            top()
            start()

        }

        constrain(nameRef) {
            top()
            start.linkTo(imageRef.end, 8.dp)
            end()
            horizontalBias = 0f
        }

        constrain(descRef) {
            top.linkTo(nameRef.bottom, 4.dp)
            start.linkTo(imageRef.end, 8.dp)
            end(8.dp)

            width = Dimension.fillToConstraints
            horizontalBias = 0f
        }

        constrain(priceRef) {
            top.linkTo(descRef.bottom, 8.dp)
            start.linkTo(imageRef.end, 8.dp)
            bottom()
            verticalBias = 0.5f
        }
        constrain(addRef) {
            end(8.dp)
            baseline.linkTo(priceRef.baseline)
            bottom.linkTo(priceRef.bottom)
        }
    }

}

@Composable
private fun CommodityItem(
    imageUrl: String,
    name: String,
    price: String,
    cartCount: Int, // 选中的数量
    description: String,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
    textMeasurer: TextMeasurer = rememberTextMeasurer(),
    constraintSet: ConstraintSet = MenuScreenDefaults.commodityItemConstraintSet()
) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .pointerInput(key1 = onAddClick) {
                detectTapGestures {
                    onAddClick()
                }
            },
        constraintSet = constraintSet
    ) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(imageUrl).build(),
            error = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Gray)
                )
            },
            contentScale = ContentScale.Crop,
            contentDescription = null,
            modifier = Modifier
                .layoutId("image")
                .size(100.dp, 100.dp)
                .aspectRatio(1f),

            )

        Text(
            text = name,
            modifier = Modifier.layoutId("name"),
            fontSize = 16.sp,
            fontWeight = FontWeight.W400,
        )
        Text(
            text = description,
            modifier = Modifier.layoutId("desc"),
            fontSize = 14.sp,
            color = Color(0xff808080),
            lineHeight = 16.sp,
            style = TextStyle(
//                fontFamily = FontFamily.Monospace
            )
        )
        Text(
            text = price,
            modifier = Modifier.layoutId("price"),
            fontSize = 16.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
//            fontFamily = FontFamily.SansSerif,
        )

        val badgeModifier = if (cartCount > 0) {
            Modifier.drawTextBadge(
                textLayoutResult = textMeasurer.measure(
                    text = if (cartCount > 100) "99+"
                    else cartCount.toString(),
                    style = TextStyle(
                        fontSize = 8.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                    )
                )
            )
        } else {
            Modifier
        }

        IconButton(
            onClick = onAddClick,
            modifier = Modifier
                .layoutId("add")
                .then(badgeModifier)
                .clip(CircleShape)
                .background(
                    Color(0xffe8c279)
                )
                .size(24.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

/**
 *
 * 绘制数字角标
 * */
fun Modifier.drawTextBadge(
    textLayoutResult: TextLayoutResult,
    badgeColor: Color = Color.Black,
    badgeTextColor: Color = Color.White
) = this then Modifier.drawWithCache {
    // 半径大小
    val radius = max(
        max(textLayoutResult.size.width, textLayoutResult.size.height).toFloat(),
        size.width / 5f
    )
    onDrawWithContent {
        drawContent()
        drawCircle(
            color = badgeColor,
            radius = radius,
            center = Offset(size.width, 0f)
        )
        drawText(
            textLayoutResult,
            color = badgeTextColor,
            topLeft = Offset(
                size.width - textLayoutResult.size.width / 2,
                -textLayoutResult.size.height / 2f
            )
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun CommodityItemPreview() {
    CommodityItem(
        imageUrl = "",
        name = "商品名称",
        price = "￥100",
        cartCount = 1,
        description = "商品描述12345678912345678912345678912312165465465456ascsadsevhjwvwh",
        modifier = Modifier.size(300.dp, 120.dp),
        onAddClick = {},
    )
}

//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//private fun MenuScreenPreview() {
//    MenuScreen(
//        onSettlementNavigate = {},
//        onSearchNavigation = {},
//        uiState = MenuUiState.Loading,
//        onChooseShopNavigate = {}
//    )
//}
//
