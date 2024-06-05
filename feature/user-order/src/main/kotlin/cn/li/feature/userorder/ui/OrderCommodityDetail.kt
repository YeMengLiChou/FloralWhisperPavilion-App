package cn.li.feature.userorder.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.layoutId
import cn.li.common.ext.sumAmountOf
import cn.li.network.dto.user.ShoppingCartDTO
import coil.compose.SubcomposeAsyncImage


@Composable
fun CommodityDetail(
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
