package cn.li.feature.userorder.ui

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.layoutId
import cn.li.common.ext.sumAmountOf
import cn.li.core.ui.base.ClearableTextFiled
import cn.li.core.ui.base.LToast
import cn.li.core.ui.base.TopBarWithBack
import cn.li.core.ui.bottom
import cn.li.core.ui.end
import cn.li.core.ui.loading.LoadingBottomSheetLayout
import cn.li.core.ui.start
import cn.li.core.ui.top
import cn.li.feature.userorder.UserOrderSettlementUiState
import cn.li.feature.userorder.vo.UserOrderSettlementVo
import cn.li.network.dto.user.OrderSubmitDTO
import cn.li.network.dto.user.ShoppingCartDTO
import coil.compose.SubcomposeAsyncImage
import kotlinx.coroutines.delay

/**
 * 内容框的外边距
 * */
private val LocalHorizontalPadding = compositionLocalOf {
    12.dp // defaultValue
}


/***
 * 内容框的内边距
 * */
private val LocalHorizontalInnerPadding = compositionLocalOf {
    16.dp
}

/**
 * 内容框的外边距
 * */
private val LocalVerticalPadding = compositionLocalOf {
    8.dp
}


/**
 * 内容框的内边距
 * */
private val LocalVerticalInnerPadding = compositionLocalOf {
    12.dp
}

/**
 * 用户下单界面
 * */
@Composable
fun UserSettlementOrderScreen(
    uiState: UserOrderSettlementUiState,
    onBackClick: () -> Unit,
    onSettlementClick: (OrderSubmitDTO) -> Unit,
    onOrderSubmitNavigate: () -> Unit,
    modifier: Modifier = Modifier,
) {

    val loading by remember(uiState) {
        derivedStateOf { uiState == UserOrderSettlementUiState.Loading }
    }
    // 数据
    val data by remember {
        mutableStateOf<UserOrderSettlementVo?>(null)
    }.apply {
        if (uiState is UserOrderSettlementUiState.Success) {
            value = uiState.data
        }
    }

    // 订单备注
    var remark by remember(key1 = data) {
        mutableStateOf("")
    }

    // 支付中显示
    var payingVisibility by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = payingVisibility) {
        if (payingVisibility) {
            // 模拟支付
            delay(2000L)
            // 然后跳换到订单详情
            payingVisibility = false
            onOrderSubmitNavigate()
        }
    }


    when (uiState) {
        UserOrderSettlementUiState.Loading -> {

        }

        is UserOrderSettlementUiState.Success -> {

        }

        is UserOrderSettlementUiState.Failed -> {
            LToast(uiState.msg, Toast.LENGTH_SHORT)
        }

        // 提交成功
        is UserOrderSettlementUiState.OrderSubmitSuccess -> {
            payingVisibility = true
        }
    }

    Surface(
        enabled = false,
        onClick = {},
        modifier = modifier.fillMaxSize(),
        color = Color(0xfff6f6f6),
    ) {
        LoadingBottomSheetLayout(
            show = payingVisibility,
            loading = loading,
            modifier = Modifier.fillMaxSize(),
            sheetContent = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "支付中...")
                }
            }
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
            ) {
                val (toolbarRef, detailRef, remarkRef, bottomRef) = createRefs()

                // 顶部栏
                TopBarWithBack(
                    onBackClick = onBackClick,
                    title = "订单详情",
                    modifier = Modifier.constrainAs(toolbarRef) {},
                )

                val verticalChainRef = createVerticalChain(
                    toolbarRef,
                    detailRef,
                    remarkRef,
                    chainStyle = ChainStyle.Packed(0f)
                )

                constrain(verticalChainRef) {
                    top.linkTo(parent.top)
                    bottom.linkTo(bottomRef.top)
                }

                // 上部分的顶单详情
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(detailRef) {
                        }
                        .padding(horizontal = LocalHorizontalPadding.current)

                ) {
                    if (data != null) {
                        val curModifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .padding(
                                horizontal = LocalHorizontalInnerPadding.current,
                                vertical = LocalVerticalInnerPadding.current
                            )

                        when (data!!.deliveryStatus) {
                            // 自取
                            UserOrderSettlementVo.DELIVERY_STATUS_SELF_PICKUP -> {
                                OrderSelfTake(
                                    shopName = data!!.shopName,
                                    shopAddress = data!!.shopLocation,
                                    modifier = curModifier,
                                )
                            }
                            // 配送
                            UserOrderSettlementVo.DELIVERY_STATUS_DELIVERY -> {
                                OrderDelivery(
                                    userName = data!!.userName!!,
                                    userPhoneNumber = data!!.userPhoneNumber!!,
                                    userAddress = data!!.userAddress!!,
                                    shopName = data!!.shopName,
                                    modifier = curModifier
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(LocalVerticalPadding.current))

                        // 商品明细
                        CommodityDetail(
                            cartInfo = data!!.cartDTO,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White)
                                .padding(
                                    vertical = LocalVerticalInnerPadding.current,
                                    horizontal = LocalHorizontalInnerPadding.current,
                                )
                        )
                    } else {
                        // Place Holder
                    }
                }

                // 订单备注
                Column(
                    modifier = Modifier
                        .padding(
                            horizontal = LocalHorizontalPadding.current,
                            vertical = LocalVerticalPadding.current
                        )
                        .constrainAs(remarkRef) {}
                ) {
//                    Spacer(modifier = Modifier.height(LocalVerticalPadding.current))
                    UserOrderRemarks(
                        orderRemarks = remark,
                        onRemarkChange = { remark = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .padding(
                                horizontal = LocalHorizontalInnerPadding.current,
                                vertical = LocalVerticalInnerPadding.current,
                            )
                    )
                }

                // 底部的支付栏
                BottomSettlement(
                    orderTotalAmount = data?.orderAmount ?: "0.00",
                    enabled = data != null,
                    onSettlementClick = {
                        onSettlementClick(
                            OrderSubmitDTO(
                                remark = remark.takeIf { it.isNotEmpty() }, // 订单备注
                                addressBookId = data!!.userAddressId, // 地址id（可选）
                                shopId = data!!.shopId, // 商店id
                                estimatedDeliveryTime = null, // 预计送达时间，未实现
                                deliveryStatus = data!!.deliveryStatus, // 配送方式
                                packAmount = "0.00", // 打包费（未实现）
                                amount = data!!.orderAmount, // 订单金额
                            )
                        )
                    },
                    modifier = Modifier
                        .constrainAs(bottomRef) {
                            bottom()
                            height = Dimension.value(64.dp)
                            verticalBias = 1f
                        }
                )
            }
        }
    }
}


/**
 * **自取** 显示内容
 *
 * */
@Composable
private fun OrderSelfTake(
    shopAddress: String,
    shopName: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "自取",
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Black)
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                color = Color.White,
                fontSize = 13.sp
            )
            Text(
                text = shopName,
                modifier = Modifier.padding(start = 8.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 18.sp
            )
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
            )
        }
        Text(
            text = shopAddress,
            color = Color.Gray,
            modifier = Modifier.fillMaxWidth(),
            fontSize = 14.sp
        )
    }
}

/**
 * **外卖** 显示的信息
 * */
@Composable
private fun OrderDelivery(
    userAddress: String,
    userName: String,
    userPhoneNumber: String,
    shopName: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "外送",
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Black)
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                color = Color.White,
                fontSize = 13.sp
            )
            Text(
                text = userAddress,
                modifier = Modifier.padding(start = 8.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 18.sp

            )
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
            )
        }
        Text(
            text = "$userName | $userPhoneNumber",
            color = Color.Gray,
            modifier = Modifier.fillMaxWidth(),
            fontSize = 14.sp
        )
        Text(text = "由 ", color = Color.Gray, fontSize = 14.sp)
        Text(text = shopName, fontSize = 15.sp)
        Text(text = " 送出", color = Color.Gray, fontSize = 14.sp)
    }
}


@Composable
private fun CommodityDetail(
    cartInfo: List<ShoppingCartDTO>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
    ) {
        Text(text = "商品明细", modifier = Modifier.padding(vertical = 4.dp))

        HorizontalDivider(
            color = Color(0xffe0e0e0),
            modifier = Modifier.padding(vertical = 8.dp),
            thickness = 0.3.dp
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            cartInfo.forEach {
                item {
                    OrderCommodityItem(
                        imageUrl = it.image,
                        name = it.name,
                        price = it.amount,
                        count = it.number,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
            }
        }

        HorizontalDivider(
            color = Color(0xffe0e0e0),
            modifier = Modifier.padding(vertical = 8.dp),
            thickness = 0.3.dp
        )
        // 底部的总结
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // 商品数量
            Text(
                text = "共${cartInfo.sumOf { it.number }}件商品",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "总计", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(4.dp))
            // 订单总金额
            Text(
                text = "¥${cartInfo.sumAmountOf { it.number * it.amount }}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * 订单中的商品信息
 * */
@Composable
private fun OrderCommodityItem(
    imageUrl: String,
    name: String,
    price: Double,
    count: Int,
    modifier: Modifier = Modifier,
    constraintSet: ConstraintSet = UserSettlementOrderScreenDefaults.orderCommodityItemConstraintSet
) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth(),
        constraintSet = constraintSet
    ) {
        SubcomposeAsyncImage(
            model = imageUrl, contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(8.dp))
                .layoutId("image"),
            contentScale = ContentScale.Crop,
        )
        Text(
            text = name,
            modifier = Modifier.layoutId("name"),
            fontSize = 16.sp
        )
        Text(text = "¥$price", modifier = Modifier.layoutId("price"), fontSize = 16.sp)
        Text(
            text = "x$count",
            modifier = Modifier.layoutId("count"),
            color = Color.Gray,
            fontSize = 14.sp
        )
    }
}


/**
 * 底部的支付栏，显示订单金额和支付按钮
 * @param orderTotalAmount 订单金额
 * @param onSettlementClick 订单的点击支付
 * */
@Composable
private fun BottomSettlement(
    orderTotalAmount: String,
    onSettlementClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "实付", modifier = Modifier.alignByBaseline(), fontWeight = FontWeight.Thin)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "¥$orderTotalAmount",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.alignByBaseline()
            )
        }
        Button(
            onClick = onSettlementClick,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(0.7f),
            colors = ButtonDefaults.buttonColors().copy(
                containerColor = Color(0xffedcc7b),
                contentColor = Color.Black
            )
        ) {
            Text(text = "支付订单")
        }
    }
}

@Composable
private fun UserOrderRemarks(
    orderRemarks: String,
    onRemarkChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var dialogVisibility by remember {
        mutableStateOf(false)
    }
    // 输入框输入的内容
    var remarkInput by remember {
        mutableStateOf(orderRemarks)
    }

    // 输入备注的对话框
    if (dialogVisibility) {
        AlertDialog(
            onDismissRequest = { /* 禁止在外部关闭dialog */ },
            confirmButton = {
                OutlinedButton(
                    border = null,
                    colors = ButtonDefaults.outlinedButtonColors().copy(
                        containerColor = Color(0xffedcc7b),
                        contentColor = Color.White,
                        disabledContainerColor = Color(0x80edcc7b),
                        disabledContentColor = Color.LightGray
                    ),
                    shape = RoundedCornerShape(8.dp),
                    enabled = remarkInput.isNotEmpty(),
                    onClick = {
                        onRemarkChange(remarkInput)
                        dialogVisibility = false
                    }
                ) {
                    Text(text = "确定")
                }
            },
            dismissButton = {
                OutlinedButton(
                    shape = RoundedCornerShape(8.dp),
                    onClick = { dialogVisibility = false },
                    border = BorderStroke(0.5.dp, color = Color.Gray)
                ) {
                    Text(text = "取消")
                }
            },
            title = {
                Text(text = "订单备注", fontSize = 20.sp)
            },
            text = {
                ClearableTextFiled(
                    modifier = Modifier.heightIn(min = 128.dp),
                    value = remarkInput, onValueChange = {
                        remarkInput = it
                    },
                    placeholder = {
                        Text(text = "请输入该订单的备注信息~", color = Color.LightGray)
                    }
                )
            },
            shape = RoundedCornerShape(8.dp),
            containerColor = Color.White,
        )
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .pointerInput(key1 = onRemarkChange) {
                    detectTapGestures {
                        dialogVisibility = true
                    }
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "订单备注", Modifier.padding(end = 16.dp))
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                // placeholder
                if (orderRemarks.isEmpty()) {
                    Text(text = "填写备注", color = Color.Gray)
                } else {
                    Text(text = orderRemarks)
                }
                Icon(
                    imageVector = Icons.Rounded.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}


object UserSettlementOrderScreenDefaults {

    val orderCommodityItemConstraintSet = ConstraintSet {
        val (imageRef, nameRef, priceRef, countRef) = createRefsFor(
            "image", "name", "price", "count"
        )
        constrain(imageRef) {
            start()
            top()
//            end.linkTo(nameRef.start)
        }
        constrain(nameRef) {
            top()
            start.linkTo(imageRef.end, 8.dp)
        }
        constrain(priceRef) {
            top()
            end()
        }
        constrain(countRef) {
            top.linkTo(priceRef.bottom, 8.dp)
            end()
        }
    }
}


@Composable
@Preview(showBackground = true, showSystemUi = true, backgroundColor = 0xfff6f6f6)
private fun UserSettlementScreenPreview() {
    UserSettlementOrderScreen(
        uiState = UserOrderSettlementUiState.Success(
            UserOrderSettlementVo(
                shopId = 1L,
                shopName = "小店",
                shopLocation = "广东省广州市天河区天河路",
                userName = "小明",
                userPhoneNumber = "12345678901",
                userAddress = "广东省广州市天河区天河路",
                orderAmount = "100.00",
                deliveryStatus = UserOrderSettlementVo.DELIVERY_STATUS_SELF_PICKUP,
                userAddressId = 0L,
                cartDTO = listOf(
                    ShoppingCartDTO(
                        id = 1L,
                        name = "商品名称",
                        image = "https://img.alicdn.com/imgextra/i2/O1CN01KXzKXV1qvZjV2jY5w/product-img.jpg",
                        amount = 100.0,
                        number = 1,
                        status = 1,
                        shopId = 1L,
                        dishId = 1L,
                        setmealId = 1L,
                        userId = 1L,
                        createTime = "",
                        dishFlavor = "",
                    )
                )
            )
        ),
        onSettlementClick = {

        },
        onBackClick = {},
        onOrderSubmitNavigate = {}
    )
}