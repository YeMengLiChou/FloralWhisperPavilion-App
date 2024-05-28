@file:SuppressLint("UsingMaterialAndMaterial3Libraries")

package cn.li.feature.menu.ui

import android.annotation.SuppressLint
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CommodityDetailLayout(
    uiState: CommodityDetailUiState,
    onDismiss: () -> Unit,
    onDismissRequest: () -> Unit,
    onAddCartClick: (itemId: Long, count: Int, isSetmeal: Boolean) -> Unit,
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
                                onAddCartClick(cachedData!!.id, addCount, true)
                                onDismissRequest()
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
                contentScale = ContentScale.Inside
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


//@Composable
//@Preview(showBackground = true)
//fun CommodityDetailLayoutPreview() {
//    CommodityDetailLayout(uiState = CommodityDetailUiState.Loading, onDismiss = {}, onDismissRequest = {}, onAddCartClick = ) {
//
//    }
//}