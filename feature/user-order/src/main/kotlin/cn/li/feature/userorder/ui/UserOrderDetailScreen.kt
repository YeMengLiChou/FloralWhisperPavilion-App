package cn.li.feature.userorder.ui

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.li.core.ui.base.TopBarWithBack
import cn.li.core.ui.loading.LoadingBottomSheetLayout
import cn.li.feature.userorder.UserOrderDetailUiState
import cn.li.feature.userorder.vo.UserOrderItemVo
import cn.li.feature.userorder.vo.toCartInfo


@Composable
fun UserOrderDetailScreen(
    uiState: UserOrderDetailUiState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val loading by remember(uiState) {
        derivedStateOf {
            uiState is UserOrderDetailUiState.Loading
        }
    }

    val orderData by remember {
        mutableStateOf<UserOrderItemVo?>(null)
    }.apply {
        if (uiState is UserOrderDetailUiState.Success) {
            this.value = uiState.data
        }
    }
    Surface(modifier = modifier) {
        LoadingBottomSheetLayout(
            loading = loading,
            show = false,
            sheetContent = {

            },
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
            ) {
                TopBarWithBack(onBackClick = onBackClick, title = "订单详情")

                orderData?.let { item ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White)
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text(text = item.shopName, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier, horizontalArrangement = Arrangement.Center) {
                                Text(text = "订单${item.statusText}")
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        CommodityDetail(
                            cartInfo = item.commodityList.toCartInfo(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White)
                                .padding(8.dp)

                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White)
                                .padding(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "下单时间", fontSize = 14.sp, color = Color.Gray)
                                Text(text = item.submitOrderTime)
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "订单号", fontSize = 14.sp, color = Color.Gray)
                                Text(text = item.orderNumber)
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "取货方式", fontSize = 14.sp, color = Color.Gray)
                                Text(text = item.deliveryText)
                            }
                            item.deliveryAddress?.let {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(IntrinsicSize.Min)
                                        .padding(bottom = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "配送地址",
                                        fontSize = 14.sp,
                                        color = Color.Gray,
                                        modifier = Modifier.fillMaxHeight()
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(text = item.deliveryAddress, textAlign = TextAlign.End)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                        .padding(bottom = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "收货人", fontSize = 14.sp, color = Color.Gray)
                                    Text(text = item.consignee!!)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                        .padding(bottom = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "手机号", fontSize = 14.sp, color = Color.Gray)
                                    Text(text = item.phone!!)
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "订单备注", fontSize = 14.sp, color = Color.Gray)
                                Text(text = item.remark.takeIf { it.isNotBlank() } ?: "无")
                            }
                        }
                    }
                }
            }

        }
    }

}


@Composable
@Preview(showBackground = true, showSystemUi = true)
private fun UserOrderDetailScreenPreview() {
//    UserOrderDetailScreen(onBackClick = {}, onSettlementClick = {}, shouldSettlement = true, item = UserOrderItemVo())
}