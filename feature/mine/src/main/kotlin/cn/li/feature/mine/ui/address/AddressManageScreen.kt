package cn.li.feature.mine.ui.address

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddBusiness
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.layoutId
import cn.li.core.ui.base.TextLabel
import cn.li.core.ui.base.TopBarWithBack
import cn.li.core.ui.bottom
import cn.li.core.ui.end
import cn.li.core.ui.horizontalCenter
import cn.li.core.ui.loading.LoadingBottomSheetLayout
import cn.li.core.ui.loading.LoadingDialog
import cn.li.core.ui.start
import cn.li.core.ui.top
import cn.li.core.ui.verticalCenter
import cn.li.feature.mine.UserAddressUiState
import cn.li.network.dto.user.AddressBookDTO
import cn.li.network.dto.user.UserLoginResult
import com.google.common.collect.ImmutableList
import kotlinx.coroutines.delay

/**
 * @param onSelectedItem 选中地址
 * @param onBackClick 返回回调
 * @param onAddClick 点击添加按钮
 * @param onEditClick 点击编辑按钮
 * */
@Composable
internal fun AddressManageScreen(
    uiState: UserAddressUiState,
    onBackClick: () -> Unit,
    onSelectedItem: (Long) -> Unit,
    onEditClick: (Long) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier,
    constraintSet: ConstraintSet = AddressManageScreenDefaults.constraintSet(),
) {

    val loading = (uiState == UserAddressUiState.Loading)

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    // 缓存数据
    var items by remember {
        mutableStateOf(emptyList<AddressBookDTO>())
    }

    LaunchedEffect(key1 = uiState) {
        snackbarHostState.currentSnackbarData?.dismiss()
        when (uiState) {
            // 更新数据
            is UserAddressUiState.FetchSuccess -> items = uiState.data
            // 加载失败
            is UserAddressUiState.Failed -> snackbarHostState.showSnackbar(
                uiState.msg,
                withDismissAction = true,
                duration = SnackbarDuration.Indefinite
            )
            else -> {

            }
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(0xfff0f0f0),
        contentColor = Color.Black,
    ) {
        // 悬浮层
        LoadingBottomSheetLayout(
            modifier = Modifier.fillMaxSize(),
            loading = loading,
            loadingText = "加载中",
            show = false
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .systemBarsPadding()
                    .fillMaxSize(),
                constraintSet = constraintSet,
            ) {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.layoutId("snackbar")
                )

                TopBarWithBack(
                    onBackClick = onBackClick,
                    modifier = Modifier.layoutId("top-bar"),
                    title = "地址管理"
                )

                // 展示
                if (uiState is UserAddressUiState.FetchSuccess) {
                    if (uiState.data.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                        ) {
                            Text(text = "还没有添加地址呢~")
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .layoutId("item-list"),
                        ) {
                            for (idx in uiState.data.indices) {
                                val addressItem = uiState.data[idx]
                                item {
                                    AddressItem(
                                        item = addressItem,
                                        modifier = Modifier.fillMaxSize(),
                                        onSelectRequest = { onSelectedItem(it.id) },
                                        onEditRequest = { onEditClick(it.id) }
                                    )
                                }
                            }
                        }
                    }
                }

                Button(onClick = onAddClick, modifier = Modifier.layoutId("bottom-button")) {
                    Icon(imageVector = Icons.Outlined.AddBusiness, contentDescription = null)
                    Text(text = "新增收货地址")
                }
            }
        }
    }
}


object AddressManageScreenDefaults {
    @Composable
    fun constraintSet() = ConstraintSet {
        val (topBarRef, itemListRef, bottomButtonRef, snackbarRef) = createRefsFor(
            "top-bar", "item-list", "bottom-button", "snackbar"
        )
        constrain(topBarRef) {
            top()
            horizontalCenter()
        }
        constrain(bottomButtonRef) {
            bottom(8.dp)
            horizontalCenter()
        }
        constrain(itemListRef) {
            top.linkTo(topBarRef.bottom)
        }

        constrain(snackbarRef) {
            bottom.linkTo(bottomButtonRef.top)
            horizontalCenter()
        }

    }
}

/**
 * 显示地址项的组件
 * */
@Composable
private fun AddressItem(
    item: AddressBookDTO,
    modifier: Modifier = Modifier,
    onSelectRequest: (AddressBookDTO) -> Unit,
    onEditRequest: (AddressBookDTO) -> Unit,
) {
    ConstraintLayout(
        modifier = modifier
            .clickable {
                onSelectRequest(item)
            }
            .background(Color.White)
            .padding(16.dp)) {

        val (labelRef, addressRef, nameRef, phoneRef, editIconRef) = createRefs()

        val address = item.let {
            "${it.provinceName ?: ""}${it.cityName ?: ""}${it.districtName ?: ""}${it.detail}"
        }

        Text(
            text = address,
            modifier = Modifier.constrainAs(addressRef) {
                start()
                end.linkTo(editIconRef.start)
                horizontalBias = 0f
                width = Dimension.preferredWrapContent
            },
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        val name =
            "${item.consignee} ${if (item.sex.toInt() == UserLoginResult.SEX_MALE) "先生" else "小姐"}"
        Text(
            text = name,
            modifier = Modifier.constrainAs(nameRef) {
                start(4.dp)
                top.linkTo(addressRef.bottom, 8.dp)
            },
            color = Color.Gray,
            fontSize = 14.sp,
        )

        Text(
            text = item.phone,
            modifier = Modifier.constrainAs(phoneRef) {
                start.linkTo(nameRef.end, 16.dp)
                top.linkTo(addressRef.bottom)
                baseline.linkTo(nameRef.baseline)
            },
            color = Color.Gray,
            fontSize = 14.sp
        )

        // 标签
        item.label?.let {
            TextLabel(
                text = it,
                modifier = Modifier
                    .constrainAs(labelRef) {
                        baseline.linkTo(phoneRef.baseline)
                        start.linkTo(phoneRef.end, 4.dp)
                        end.linkTo(editIconRef.start)
                        horizontalBias = 0f
                    }
                    .padding(horizontal = 4.dp, vertical = 2.dp)
                    .size(width = 30.dp, height = 18.dp),
                fontSize = 16.sp
            )
        }

        IconButton(
            onClick = { onEditRequest(item) },
            modifier = Modifier.constrainAs(editIconRef) {
                end(4.dp)
                verticalCenter()
            }
        ) {
            Icon(
                imageVector = Icons.Outlined.EditNote, contentDescription = null, tint = Color.Gray
            )
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AddressScreenPreview() {
    Box(modifier = Modifier.fillMaxSize()) {
        AddressManageScreen(uiState = UserAddressUiState.FetchSuccess(
            listOf(
                AddressBookDTO(
                    id = 0L,
                    userId = 0L,
                    consignee = "璃",
                    sex = 1,
                    phone = "1234567890",
                    provinceName = "广西省",
                    provinceCode = "11",
                    cityCode = "12",
                    cityName = "桂林市",
                    districtCode = "123",
                    districtName = "临川县",
                    detail = "xxxxxx",
                    isDefault = 1,
                    label = "xxx"
                )
            )
        ),
            onBackClick = { /*TODO*/ },
            onSelectedItem = {},
            onEditClick = {},
            onAddClick = { /*TODO*/ })
    }
}