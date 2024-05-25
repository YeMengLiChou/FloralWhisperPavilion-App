@file:OptIn(ExperimentalMaterial3Api::class)

package cn.li.feature.mine.ui.info

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Female
import androidx.compose.material.icons.outlined.Male
import androidx.compose.material.icons.outlined.NoAccounts
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import cn.li.core.ui.base.BottomSheetImagePicker
import cn.li.core.ui.base.ClearableTextFiled
import cn.li.core.ui.loading.LoadingBottomSheetLayout
import cn.li.datastore.proto.UserPreferences
import cn.li.feature.mine.UserInfoUiState
import cn.li.network.dto.user.UserLoginResult
import coil.compose.SubcomposeAsyncImage


/**
 * ”用户信息“ 界面
 * @param userDataState 当前用户数据。不能去掉 [State]，因为 [UserPreferences] 可能是不稳定的
 * @param onBackClick 按下返回键时触发
 * */
@Composable
internal fun UserInfoScreen(
    userData: UserPreferences,
    uiState: UserInfoUiState,
    onBackClick: () -> Unit,
    onUpdatePhone: (String) -> Unit,
    onUpdateSex: (Int) -> Unit,
    onUpdateAvatar: (Any) -> Unit,
    onUpdateNickname: (String) -> Unit,
    onErrorMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    // 加载状态
    val loading = uiState == UserInfoUiState.Loading

    // 提示更新状态
    LaunchedEffect(key1 = uiState) {
        when (uiState) {
            UserInfoUiState.Loading -> snackbarHostState.showSnackbar(
                "更新中",
                duration = SnackbarDuration.Indefinite
            )

            is UserInfoUiState.Success -> snackbarHostState.showSnackbar(uiState.msg)
            is UserInfoUiState.Failed -> snackbarHostState.showSnackbar(uiState.msg)
            else -> {
                snackbarHostState.currentSnackbarData?.dismiss()
            }
        }
    }


    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(0xfff0f0f0),
        contentColor = Color.White,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // 底部显示
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
            // 更新电话号码
            var phoneUpdateDialogState by remember(userData) {
                mutableStateOf(false)
            }
            var changedPhone by remember(userData) {
                mutableStateOf(userData.phone)
            }

            PhoneUpdateDialog(
                show = phoneUpdateDialogState,
                phone = changedPhone,
                onValueChange = {
                    changedPhone = it
                },
                onDismissRequest = {
                    phoneUpdateDialogState = false
                },
                onConfirm = {
                    onUpdatePhone(changedPhone)
                    phoneUpdateDialogState = false
                },
            )

            // 底部弹窗的状态
            var bottomSheetPickerState by remember {
                mutableStateOf(false)
            }
            // 拍照选择
            // TODO: 加入裁剪功能
            BottomSheetImagePicker(
                show = bottomSheetPickerState,
                onTakePicture = { bitmap ->
                    if (bitmap == null) {
                        onErrorMessage("取消选择图片")
                    } else {
                        onUpdateAvatar(bitmap)
                    }
                },
                onDismissRequest = {
                    bottomSheetPickerState = false
                },
                onSelectedImage = { uri ->
                    if (uri == null) {
                        onErrorMessage("取消拍摄")
                    } else {
                        onUpdateAvatar(uri)
                    }
                },
                title = {
                    Text(
                        text = "更新头像",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp
                    )
                }
            )

            // 更新用户昵称
            var nicknameDialogState by remember {
                mutableStateOf(false)
            }
            var changedNickname by remember {
                mutableStateOf("")
            }

            NicknameUpdateDialog(
                show = nicknameDialogState,
                nickname = changedNickname,
                onValueChange = {
                    changedNickname = it
                },
                onDismissRequest = {
                    nicknameDialogState = false
                    changedNickname = ""
                },
                onConfirm = {
                    onUpdateNickname(changedNickname)
                })

            LoadingBottomSheetLayout(
                show = false,
                loading = true,
                loadingText = "更新中"
            ) {
                Column(modifier = Modifier.systemBarsPadding()) {
                    TopBar(onBackClick = onBackClick)
                    Box(
                        modifier = Modifier.padding(vertical = 16.dp, horizontal = 12.dp)
                    ) {
                        Card(
                            colors = CardDefaults.cardColors().copy(
                                containerColor = Color.White
                            )
                        ) {
                            val divider = @Composable {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 8.dp),
                                    color = Color(0xfff0f0f0)
                                )
                            }

                            UserInfoChipItem(
                                keyText = "头像",
                                onClick = {
                                    bottomSheetPickerState = true
                                },
                                modifier = Modifier.height(84.dp)
                            ) {
                                if (userData.avatar.isNotBlank()) {
                                    SubcomposeAsyncImage(
                                        model = userData.avatar,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .aspectRatio(1f)
                                            .clip(CircleShape)
                                    )
                                } else {
                                    Text(text = "未设置")
                                }
                            }

                            divider()

                            UserInfoChipItem(
                                keyText = "昵称",
                                onClick = {
                                    nicknameDialogState = true
                                },
                                modifier = Modifier.height(56.dp)
                            ) {
                                if (userData.nickname.isNotBlank()) {
                                    Text(text = userData.nickname)
                                } else {
                                    Text(text = "未设置")
                                }
                            }

                            divider()

                            var dropdownMenuState by remember {
                                mutableStateOf(false)
                            }
                            var selectedSex by remember {
                                mutableIntStateOf(userData.sex)
                            }
                            // 性别项
                            UserSexChipItem(
                                sex = selectedSex,
                                dropdownMenuShowState = dropdownMenuState,
                                onShowRequest = {
                                    dropdownMenuState = false
                                },
                                onDismissRequest = {
                                    dropdownMenuState = false
                                },
                                onUpdateSex = {
                                    selectedSex = it
                                    onUpdateSex(it)
                                },
                                modifier = Modifier.height(56.dp)
                            )


                            divider()
                            UserInfoChipItem(
                                keyText = "手机号",
                                onClick = {
                                    phoneUpdateDialogState = true
                                },
                                modifier = Modifier.height(56.dp)
                            ) {
                                if (userData.phone.isNotBlank()) {
                                    Text(text = userData.phone)
                                } else {
                                    Text(text = "未设置")
                                }
                            }
                            divider()
                            UserInfoChipItem(keyText = "注册时间", enabled = false) {
                                Text(text = userData.createTime.toString())
                            }

                            divider()
                            UserInfoChipItem(keyText = "最后更新时间", enabled = false) {
                                Text(text = userData.updateTime.toString())
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun TopBar(
    onBackClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextButton(onClick = onBackClick) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.ChevronLeft,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = "个人信息",
                    modifier = Modifier,
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp,
                )
            }
        }
    }
}

@Composable
private fun UserInfoChipItem(
    keyText: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    value: @Composable () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick ?: {})
            .padding(vertical = 12.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = keyText)
        Row(verticalAlignment = Alignment.CenterVertically) {
            value()
            Spacer(modifier = Modifier.width(4.dp))
            if (enabled) {
                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = null,
                    tint = Color.LightGray
                )
            } else {
                Spacer(modifier = Modifier.width(24.dp))
            }
        }
    }
}

/**
 * 更新电话号码的对话框
 * */
@Composable
private fun PhoneUpdateDialog(
    show: Boolean,
    phone: String,
    onValueChange: (String) -> Unit,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
) {
    if (show) {
        val error = if (phone.isEmpty()) {
            "手机号不能为空"
        } else if (phone.length != 11) {
            "手机号长度非法"
        } else if (!phone.isDigitsOnly()) {
            "手机号非法"
        } else {
            null
        }

        val updateEnabled = error == null

        AlertDialog(
            onDismissRequest = onDismissRequest,
            confirmButton = {
                TextButton(onClick = onConfirm, enabled = updateEnabled) { Text(text = "更新") }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) { Text(text = "取消") }
            },
            title = {
                Text(text = "更新手机号")
            },
            text = {
                ClearableTextFiled(
                    label = {
                        Text(text = "请输入手机号")
                    },
                    value = phone,
                    onValueChange = onValueChange,
                    isError = !updateEnabled,
                    supportingText = {
                        Text(text = error ?: "")
                    }
                )
            }
        )
    }
}

/**
 * 更新用户名的对话框
 * */
@Composable
private fun NicknameUpdateDialog(
    show: Boolean,
    nickname: String,
    onValueChange: (String) -> Unit,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
) {
    if (show) {
        val error = if (nickname.isEmpty()) {
            "昵称不能为空"
        } else if (nickname.length > 20) {
            "昵称过长"
        } else {
            null
        }

        val updateEnabled = error == null

        AlertDialog(
            onDismissRequest = onDismissRequest,
            confirmButton = {
                TextButton(
                    onClick = {
                        onConfirm()
                        onDismissRequest()
                    },
                    enabled = updateEnabled
                ) { Text(text = "更新") }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) { Text(text = "取消") }
            },
            title = {
                Text(text = "更新昵称")
            },
            text = {
                ClearableTextFiled(
                    label = {
                        Text(text = "请输入新的昵称")
                    },
                    value = nickname,
                    onValueChange = onValueChange,
                    isError = !updateEnabled,
                    supportingText = {
                        Text(text = error ?: "")
                    }
                )
            }
        )
    }
}


@Composable
private fun UserSexChipItem(
    sex: Int,
    dropdownMenuShowState: Boolean,
    onShowRequest: () -> Unit,
    onDismissRequest: () -> Unit,
    onUpdateSex: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    UserInfoChipItem(
        keyText = "性别",
        onClick = {
            if (dropdownMenuShowState) onDismissRequest()
            else onShowRequest()
        },
        modifier = modifier
    ) {
        Text(
            text = when (sex) {
                UserLoginResult.SEX_UNSPECIFIED -> "未知"
                UserLoginResult.SEX_MALE -> "男"
                UserLoginResult.SEX_FEMALE -> "女"
                else -> throw NoSuchFieldException("没有 $sex 常量")
            }
        )
        DropdownMenu(
            expanded = dropdownMenuShowState,
            onDismissRequest = {
                onDismissRequest()
            }) {
            DropdownMenuItem(text = {
                Text(
                    text = "保持神秘",
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }, onClick = {
                onDismissRequest()
                onUpdateSex(UserLoginResult.SEX_UNSPECIFIED)
            }, trailingIcon = {
                Icon(
                    imageVector = Icons.Outlined.NoAccounts,
                    contentDescription = null
                )
            })
            DropdownMenuItem(text = {
                Text(
                    text = "男",
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            },
                onClick = {
                    onDismissRequest()
                    onUpdateSex(UserLoginResult.SEX_MALE)
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Male,
                        contentDescription = null
                    )
                })
            DropdownMenuItem(text = {
                Text(
                    text = "女",
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            },
                onClick = {
                    onDismissRequest()
                    onUpdateSex(UserLoginResult.SEX_FEMALE)
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Female,
                        contentDescription = null
                    )
                })

        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun UserInfoScreenPreview() {
    UserInfoScreen(
        userData = UserPreferences.getDefaultInstance(),
        uiState = UserInfoUiState.Nothing,
        onBackClick = {},
        onUpdatePhone = {},
        onUpdateAvatar = {},
        onUpdateSex = {},
        onUpdateNickname = {},
        onErrorMessage = {}
    )
}