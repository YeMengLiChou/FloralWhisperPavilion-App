@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)

package cn.li.feature.mine.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.rounded.Book
import androidx.compose.material.icons.sharp.Person
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.li.core.ui.base.SwipeRefreshBox
import cn.li.core.ui.theme.LightGreenColor
import cn.li.feature.mine.UserMineUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * **我的** 界面
 * */
@Composable
fun MineScreen(
    uiState: UserMineUiState,
    refreshing: Boolean = false,
    onPullRefresh: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // 加载中时显示进度条
        if (uiState == UserMineUiState.Loading) {
            CircularProgressIndicator(
                color = LightGreenColor
            )
        }

        Box(Modifier.fillMaxSize()) {
            SwipeRefreshBox(
                refreshing = refreshing,
                onRefresh = onPullRefresh,
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    TopToolbar(
                        modifier = Modifier.background(Color.Transparent),
                        onSettingNavigation = {}
                    )
                    UserInfo(modifier = Modifier.fillMaxWidth(0.9f))
                    Spacer(modifier = Modifier.height(16.dp))

                    FunctionItem(
                        text = "地址管理",
                        onClick = {
                            // TODO：导航去 “地址管理” 界面
                        },
                        leadingIcon = {
                            Icon(imageVector = Icons.Rounded.Book, contentDescription = null)
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(64.dp),
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    FunctionItem(
                        text = "联系客服",
                        onClick = {

                        },
                        leadingIcon = {
                            Icon(imageVector = Icons.Rounded.Book, contentDescription = null)
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(64.dp),
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                }

            }
        }
    }
}

@Composable
internal fun UserInfo(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // TODO: 使用 Coil 加载头像
        Image(
            imageVector = Icons.Sharp.Person,
            contentDescription = null,
            modifier = Modifier
                .clip(CircleShape)
                .size(64.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceEvenly,
        ) {
            Text(text = "Username", fontSize = 20.sp)
            Text(text = "123******123")
        }
    }
}

@Composable
internal fun FunctionItem(
    text: String,
    onClick: () -> Unit,
    leadingIcon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedAssistChip(
        onClick = onClick,
        label = {
            Text(text = text)
        },
        leadingIcon = leadingIcon,
        trailingIcon = {
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null
            )
        },
        modifier = modifier,
        colors = AssistChipDefaults.elevatedAssistChipColors().copy(
            containerColor = Color.White,
        ),

        )
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MineScreenPreview() {
    var refreshing by remember {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()
    MineScreen(
        uiState = UserMineUiState.Loading,
        refreshing = refreshing,
        onPullRefresh = {
            scope.launch {
                refreshing = true
                delay(1000)
                refreshing = false
            }
        },
    )
}
