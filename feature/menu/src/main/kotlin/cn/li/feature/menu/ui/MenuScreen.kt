package cn.li.feature.menu.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Search
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
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
import cn.li.common.ext.sumAmountOf
import cn.li.core.ui.base.LToast
import cn.li.core.ui.base.NumberAdjustBox
import cn.li.core.ui.base.SwitchTabs
import cn.li.core.ui.base.TabsScrollableLazyColumn
import cn.li.core.ui.base.TextLabel
import cn.li.core.ui.bottom
import cn.li.core.ui.end
import cn.li.core.ui.loading.LoadingBottomSheetLayout
import cn.li.core.ui.start
import cn.li.core.ui.top
import cn.li.feature.menu.ui.cart.FloatingCartLayout
import cn.li.network.dto.user.ShopItemDTO
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
    onSettlementNavigate: (shopId: Long) -> Unit,
    onChooseShopNavigate: () -> Unit,
    onChooseAddressNavigate: () -> Unit,
    onAddCommodityToCart: suspend (itemId: Long, count: Int) -> Boolean,
    onCommodityDetailShow: (id: Long) -> Unit,
    onCommodityDetailDismiss: () -> Unit,
    onCartCommodityCountIncrease: (itemId: Long, number: Int) -> Unit,
    onCartCommodityCountDecrease: (itemId: Long, number: Int) -> Unit,
    onCartCommodityDeleteRequest: (itemId: Long) -> Unit,
    onCartClearRequest: () -> Unit,
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
    val cartTotalAmount by remember(key1 = cachedData?.cartInfo) {
        derivedStateOf {
            cachedData?.cartInfo?.sumAmountOf { it.amount * it.number }?.toString() ?: "0.00"
        }
    }
    // 购物车的总数量
    val cartCommodityCount by remember(key1 = cachedData?.cartInfo) {
        derivedStateOf {
            cachedData?.cartInfo?.sumOf { it.number } ?: 0
        }
    }

    // 侧边栏
    val categoriesTabText = remember(key1 = cachedData) {
        cachedData?.goods?.map { it.categoryName } ?: emptyList()
    }

    val scope = rememberCoroutineScope()

    // 购物车更改数量的防抖状态：通过新起一个协程+delay来控制防抖
    val changeCartItemsState = remember(key1 = cachedData?.cartInfo) {
        // item -> <last-time, number>
        mutableMapOf<Long, Job>()
    }


    when (uiState) {
        MenuUiState.Loading -> {}

        is MenuUiState.Success -> {
            cachedData = uiState
        }

        is MenuUiState.Failed -> {
            LToast(uiState.error, Toast.LENGTH_LONG)
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
                onAddCartClick = onAddCommodityToCart
            ) {
                // 购物车
                FloatingCartLayout(
                    showCart = orderEnabled,
                    badgeCount = cartCommodityCount,
                    amount = cartTotalAmount,
                    onSettlement = { onSettlementNavigate(cachedData?.shopInfo?.id!!) },
                    sheetContent = {
                        // 购物车的内容
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 400.dp)
                        ) {
                            cachedData?.cartInfo?.forEach {
                                item(key = it.id) {
                                    CartCommodityItem(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 12.dp, end = 12.dp, top = 12.dp),
                                        imageUrl = it.image,
                                        name = it.name,
                                        price = it.amount.toString(),
                                        cartCount = it.number,
                                        onIncreaseCount = { number ->
                                            changeCartItemsState[it.id]?.cancel()
                                            changeCartItemsState[it.id] = scope.launch {
                                                delay(300)
                                                onCartCommodityCountIncrease(it.id, number)
                                                changeCartItemsState.remove(it.id)
                                            }
                                        },
                                        onDecreaseCount = { number ->
                                            changeCartItemsState[it.id]?.cancel()
                                            changeCartItemsState[it.id] = scope.launch {
                                                delay(300)
                                                onCartCommodityCountDecrease(it.id, number)
                                                changeCartItemsState.remove(it.id)
                                            }
                                        },
                                        onDeleteRequest = {
                                            changeCartItemsState.values.forEach { job -> job.cancel() }
                                            onCartCommodityDeleteRequest(it.id)
                                        }
                                    )
                                }
                            }
                        }
                    },
                    onClearCart = {
                        onCartClearRequest()
                    },
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        MenuTopBar(
                            modifier = Modifier
                                .height(IntrinsicSize.Min),
                            onSetDelivery = {
                                // 选择店铺
                                onChooseAddressNavigate()
                            },
                            onSetSelfTake = {
                                // 选择自取
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

/**
 * 顶部栏
 * @param onSetDelivery 切换到外卖
 * @param onSetSelfTake 切换到自取
 * @param onSearchClick 点击搜索
 * @param onShopClick 点击商店
 * */
@Composable
private fun MenuTopBar(
    shopName: String,
    shopStatus: String,
    onSetSelfTake: () -> Unit,
    onSetDelivery: () -> Unit,
    onSearchClick: () -> Unit,
    onShopClick: () -> Unit,
    modifier: Modifier = Modifier,
    constraintSet: ConstraintSet = MenuScreenDefaults.topBarConstraintSet()
) {
    var selectedIndex by rememberSaveable {
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
                    .height(36.dp)
                    .border(
                        width = 0.5.dp,
                        color = Color(0xffe0e0e0),
                        shape = RoundedCornerShape(50)
                    )
                    .layoutId("tabs"),
                onTabsClick = {
                    selectedIndex = it
                    if (it == 0) onSetSelfTake()
                    else onSetDelivery()
                }
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
//                    .border(
//                        width = 0.5.dp,
//                        shape = RoundedCornerShape(50),
//                        color = Color(0xffe0e0e0)
//                    )
                    .height(40.dp)
                    .padding(vertical = 6.dp, horizontal = 12.dp)
                    .layoutId("search")
                    .pointerInput(onSearchClick) {
                        detectTapGestures { onSearchClick() }
                    }
            ) {
//                Icon(imageVector = Icons.Outlined.Search, contentDescription = null)
//                Text(
//                    text = "搜索商品",
//                    color = Color(0xff101010),
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .wrapContentSize(Alignment.Center),
//                    textAlign = TextAlign.Center,
//                )
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
            TextLabel(
                text = shopStatus,
                modifier = Modifier
                    .layoutId("status")
                    .padding(horizontal = 2.dp, vertical = 1.dp),
                fontSize = 10.sp,
                color = Color.Black
            )
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

    val commodityItemConstraintSet = ConstraintSet {
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
            end(16.dp)
            baseline.linkTo(priceRef.baseline)
            bottom.linkTo(priceRef.bottom)
        }
    }

    val cartCommodityItemConstraintSet = ConstraintSet {
        val (imageRef, nameRef, priceRef, numberRef) = createRefsFor(
            "image", "name", "price", "number"
        )

        constrain(imageRef) {
            top()
            start()
            bottom()
            height = Dimension.fillToConstraints
        }

        constrain(nameRef) {
            start.linkTo(imageRef.end, 8.dp)
            top()
        }
        constrain(priceRef) {
            start.linkTo(imageRef.end, 8.dp)
            top.linkTo(nameRef.bottom, 4.dp)
            bottom()
        }

        constrain(numberRef) {
            end()
            bottom()
        }

    }

}

/**
 * 商品列表的子项
 * @param imageUrl 图片地址
 * @param name 商品名称
 * @param price 商品价格
 * @param cartCount 选中的数量
 * @param description 商品描述
 * @param onAddClick 点击添加
 * */
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
    constraintSet: ConstraintSet = MenuScreenDefaults.commodityItemConstraintSet
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
                .clip(RoundedCornerShape(8.dp))
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
                        fontSize = 10.sp,
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
 * 绘制数字角标
 * */
fun Modifier.drawTextBadge(
    textLayoutResult: TextLayoutResult,
    badgeColor: Color = Color.Black,
    badgeTextColor: Color = Color.White
) = this then Modifier.drawWithCache {
    // 半径大小
    val radius = max(
        max(textLayoutResult.size.width / 2, textLayoutResult.size.height / 2).toFloat(),
        size.width / 3f
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

/**
 * 购物车的商品子项
 * @param imageUrl 图片地址
 * @param name 商品名称
 * @param price 商品价格
 * @param cartCount 选中的数量
 * @param onDeleteRequest 删除请求
 * @param onIncreaseCount 增加数量
 * @param onDecreaseCount 减少数量
 * @param constraintSet 约束集
 * */
@Composable
fun CartCommodityItem(
    imageUrl: String,
    name: String,
    price: String,
    cartCount: Int,
    onIncreaseCount: (Int) -> Unit,
    onDecreaseCount: (Int) -> Unit,
    onDeleteRequest: () -> Unit,
    modifier: Modifier = Modifier,
    constraintSet: ConstraintSet = MenuScreenDefaults.cartCommodityItemConstraintSet,
) {
    ConstraintLayout(
        modifier = modifier.heightIn(min = 64.dp),
        constraintSet = constraintSet
    ) {
        SubcomposeAsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier
                .layoutId("image")
                .aspectRatio(1f, matchHeightConstraintsFirst = true)
                .clip(RoundedCornerShape(4.dp)),
            contentScale = ContentScale.Crop,
        )
        Text(
            text = name,
            modifier = Modifier.layoutId("name"),
            fontSize = 16.sp,
            fontWeight = FontWeight.W500
        )
        Text(text = "¥$price", modifier = Modifier.layoutId("price"), fontSize = 16.sp)
        NumberAdjustBox(
            modifier = Modifier.layoutId("number"),
            value = cartCount,
            onIncrease = onIncreaseCount,
            onDecrease = {
                if (it == 0) {
                    onDeleteRequest()
                }
                onDecreaseCount(it)
            },
            limitMin = 0,
            limitMax = 1000,
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
