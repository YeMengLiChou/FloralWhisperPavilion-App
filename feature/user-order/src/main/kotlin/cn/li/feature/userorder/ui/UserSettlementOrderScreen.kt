package cn.li.feature.userorder.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import cn.li.core.ui.bottom
import cn.li.core.ui.loading.LoadingBottomSheetLayout
import cn.li.core.ui.top
import cn.li.feature.userorder.UserOrderSettlementUiState
import cn.li.feature.userorder.vo.UserOrderSettlementVo


/**
 * 用户下单界面
 * */
@Composable
fun UserSettlementOrderScreen(
    uiState: UserOrderSettlementUiState,
    onSettlementClick: () -> Unit,
    modifier: Modifier = Modifier,
) {

    val loading by remember(uiState) {
        derivedStateOf { uiState == UserOrderSettlementUiState.Loading }
    }
    // 数据
    var data by remember {
        mutableStateOf<UserOrderSettlementVo?>(null)
    }.apply {
        if (uiState is UserOrderSettlementUiState.Success) {
            this.value = uiState.data
        }
    }

    Surface(onClick = { /*TODO*/ }, modifier = modifier) {
        LoadingBottomSheetLayout(
            show = false,
            loading = loading,
            modifier = Modifier.fillMaxSize()
        ) {
            ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                val (detailRef, bottomRef) = createRefs()
                // 上部分的顶单详情
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(detailRef) {
                        top()
                        bottom.linkTo(bottomRef.top)
                    }) {

                    ShopLocation(

                    )

                }
                // 底部的支付栏
                BottomSettlement(
                    orderTotalAmount = data?.orderAmount ?: 0.0,
                    enabled = data != null,
                    onSettlementClick = onSettlementClick,
                    modifier = Modifier
                        .constrainAs(bottomRef) {
                            bottom()
                            top.linkTo(detailRef.bottom)
                            height = Dimension.value(64.dp)
                        }
                )
            }
        }
    }
}


@Composable
private fun ShopLocation(
    modifier: Modifier = Modifier
) {

}


/**
 * 底部的支付栏，显示订单金额和支付按钮
 * @param orderTotalAmount 订单金额
 * @param 订单的点击支付
 * */
@Composable
private fun BottomSettlement(
    orderTotalAmount: Double,
    onSettlementClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth()
    ) {
        Row {
            Text(text = "实付")
            Text(text = "$orderTotalAmount", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Button(onClick = onSettlementClick, enabled = enabled) {
            Text(text = "支付订单")
        }
    }
}