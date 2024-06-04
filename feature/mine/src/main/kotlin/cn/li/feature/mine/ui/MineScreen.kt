package cn.li.feature.mine.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.layoutId
import cn.li.common.ext.cast
import cn.li.core.ui.base.GradientBackground
import cn.li.core.ui.base.LToast
import cn.li.core.ui.base.SwipeRefreshBox
import cn.li.core.ui.end
import cn.li.core.ui.start
import cn.li.core.ui.theme.GradientColors
import cn.li.core.ui.theme.LightGreenColor
import cn.li.core.ui.verticalCenter
import cn.li.datastore.proto.UserPreferences
import cn.li.feature.mine.UserMineUiState
import coil.compose.SubcomposeAsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * **我的** 界面
 * */
@Composable
fun MineScreen(
    uiState: UserMineUiState,
    refreshing: Boolean = false,
    onPullRefresh: () -> Unit,
    onUserInfoNavigation: () -> Unit,
    onAddressManagementNavigation: () -> Unit,
    onSettingsNavigation: () -> Unit,
    onLoginNavigate: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        // 加载中时显示进度条
        if (uiState == UserMineUiState.Loading) {
            CircularProgressIndicator(
                color = LightGreenColor
            )
        }
        // 用户信息
        val userInfo by remember {
            mutableStateOf<UserPreferences?>(null)
        }.apply {
            if (uiState is UserMineUiState.Success) {
                this.value = uiState.userPreferences
            } else if (uiState is UserMineUiState.UnLogin) {
                this.value = null
            }
        }

        GradientBackground(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(.5f),
            gradientColors = GradientColors(
                top = Color.Transparent,
            )
        ) {
            Spacer(modifier = Modifier.fillMaxHeight())
        }
        Box(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
        ) {
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
                    // 顶部栏
                    TopToolbar(
                        modifier = Modifier
                            .background(Color.Transparent)
                            .drawWithContent { /* 等同于 invisibility */ },
                        onSettingNavigation = onSettingsNavigation
                    )

                    // 用户信息栏
                    UserInfo(
                        userPreferences = userInfo,
                        modifier = Modifier.fillMaxWidth(),
                        onRightIconClick = onUserInfoNavigation,
                        onLoginNavigate = onLoginNavigate
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    // 选项
                    Card(
                        colors = CardDefaults.cardColors().copy(
                            containerColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(vertical = 8.dp, horizontal = 8.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            ChipItem(
                                onClick = onAddressManagementNavigation,
                                leadingIcon = Icons.Outlined.LocationOn,
                                text = "地址管理",
                                modifier = Modifier.clickable {
                                    onAddressManagementNavigation()
                                }
                            )
                            HorizontalDivider(
                                color = Color(0xfff0f0f0),
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                            ChipItem(
                                onClick = { /*TODO*/ },
                                leadingIcon = Icons.Outlined.Email,
                                text = "我的消息"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun UserInfo(
    userPreferences: UserPreferences?,
    onRightIconClick: () -> Unit,
    onLoginNavigate: () -> Unit,
    modifier: Modifier = Modifier,
    constraintSet: ConstraintSet = UserInfoDefaults.constraintSet(),
) {

    var clicked by remember {
        mutableStateOf(false)
    }

    if (clicked) {
        if (userPreferences == null) {
            LToast(text = "未登录，请先登录", duration = Toast.LENGTH_SHORT)
            onLoginNavigate()
        } else {
            onRightIconClick()
        }
    }
    val scope = rememberCoroutineScope()

    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(vertical = 16.dp, horizontal = 8.dp)
            .pointerInput(onRightIconClick) {
                detectTapGestures {
                    clicked = true
                    scope.launch {
                        delay(300)
                        clicked = false
                    }
                }
            },
        constraintSet = constraintSet,
    ) {
        Box(
            modifier = Modifier.layoutId("avatarBox")
        ) {
            val avatarUrl = userPreferences?.avatar?.takeIf { it.isNotBlank() }
            val imageModifier =
                Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color = Color.White)
            // 没有头像时显示默认
            if (avatarUrl == null) {
                Image(
                    imageVector = Icons.Rounded.Person,
                    contentDescription = null,
                    modifier = imageModifier,
                    contentScale = ContentScale.Fit
                )
            } else {
                // coil 加载图片
                SubcomposeAsyncImage(
                    model = avatarUrl,
                    contentDescription = null,
                    modifier = imageModifier,
                    contentScale = ContentScale.Crop,
                    loading = {
                        CircularProgressIndicator()
                    },
                    error = {
                        /* TODO：添加加载失败的图像 */
                    }
                )
            }
        }

        Spacer(
            modifier = Modifier
                .width(16.dp)
                .layoutId("spacer")
        )

        val nickname = if (userPreferences == null) {
            "未登录"
        } else if (userPreferences.nickname.isBlank()) {
            "未设置"
        } else {
            userPreferences.nickname
        }
        val phoneNumber = if (userPreferences == null || userPreferences.phone.isBlank()) {
            null
        } else {
            userPreferences.phone
        }
        // 用户名 + 电话号码
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .layoutId("column"),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = nickname, fontSize = TextUnit(20f, TextUnitType.Sp))
            phoneNumber?.let {
                Text(text = it)
            }
        }
        // 右边的 >
        Box(
            modifier = Modifier
                .layoutId("rightIcon")
                .clip(CircleShape)
                .size(48.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                modifier = Modifier.clip(CircleShape)
            )
        }

    }
}

/**
 * 默认使用样式
 * */
private object UserInfoDefaults {

    @Composable
    fun constraintSet() = ConstraintSet {
        val (avatarBox, spacerRef, columnRef, buttonRef) = createRefsFor(
            "avatarBox", "spacer", "column", "rightIcon"
        )

        constrain(avatarBox) {
            start()
            verticalCenter()
        }
        constrain(spacerRef) {
            start.linkTo(avatarBox.end)
        }
        constrain(columnRef) {
            verticalCenter()
            start.linkTo(spacerRef.end)
        }
        constrain(buttonRef) {
            end()
            verticalCenter()
        }
    }
}


@Composable
fun ChipItem(
    onClick: () -> Unit,
    leadingIcon: ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    val constraintSet = ConstraintSet {
        val (leftIconRef, textRef, rightIconRef) = createRefsFor(
            "leftIcon", "text", "rightIcon"
        )
        constrain(leftIconRef) {
            start(8.dp)
            verticalCenter()
        }
        constrain(textRef) {
            start.linkTo(leftIconRef.end, margin = 8.dp)
            verticalCenter()
        }
        constrain(rightIconRef) {
            verticalCenter()
            end(4.dp)
        }
    }

    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clickable(onClick = onClick),
        constraintSet = constraintSet,
    ) {
        Icon(
            imageVector = leadingIcon,
            contentDescription = null,
            modifier = Modifier.layoutId("leftIcon")
        )
        Text(text = text, modifier = Modifier.layoutId("text"))
        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = null,
            modifier = Modifier.layoutId("rightIcon")
        )
    }
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
        onUserInfoNavigation = {},
        onAddressManagementNavigation = {},
        onSettingsNavigation = {},
        onLoginNavigate = {}
    )
}
