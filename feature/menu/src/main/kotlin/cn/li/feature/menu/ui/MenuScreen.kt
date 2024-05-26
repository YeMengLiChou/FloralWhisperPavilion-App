package cn.li.feature.menu.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.layoutId
import cn.li.core.ui.base.SwitchTabs
import cn.li.core.ui.base.TextLabel
import cn.li.core.ui.end
import cn.li.core.ui.start
import cn.li.feature.menu.ui.cart.FloatingCartLayout

@Composable
fun MenuScreen(
    onSettlementNavigate: () -> Unit,
    onSearchNavigation: () -> Unit, // 搜索优先级最低
    modifier: Modifier = Modifier
) {
    val count = 0

    Surface(modifier = modifier) {
        FloatingCartLayout(
            showCart = true, // 根据当前商店的营业状态来判断
            badgeCount = 12,
            amount = "123",
            onSettlement = { /*TODO*/ },
            sheetContent = {
                if (count == 0) {
                    EmptyCart(modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp))
                } else {
                    // 显示商品详情
                }
            }
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                MenuTopBar(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .height(IntrinsicSize.Min),
                    onSetTakeout = {},
                    onSetSelfTake = {},
                    onSearchClick = {},
                    onShopClick = {},
                    shopStatus = "",
                    shopName = "",
                )
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
        color = Color.White, modifier = modifier, shadowElevation = 2.dp
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


}


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MenuScreenPreview() {
    MenuScreen(
        onSettlementNavigate = {},
        onSearchNavigation = {},
    )
}

