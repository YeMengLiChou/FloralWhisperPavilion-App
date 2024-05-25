package cn.li.feature.mine.ui.address

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.layoutId
import cn.li.core.ui.base.TopBarWithBack
import cn.li.core.ui.horizontalCenter
import cn.li.core.ui.theme.LightGrayTextFieldContainerColor
import cn.li.core.ui.top
import cn.li.network.dto.user.AddressBookAddDTO
import cn.li.network.dto.user.AddressBookDTO
import cn.li.network.dto.user.AddressBookUpdateDTO
import cn.li.network.dto.user.toUpdateDTO


@Composable
fun AddressAddScreen(
    onBackClick: () -> Unit,
    onAddedClick: (AddressBookAddDTO) -> Unit,
    onEditedClick: (AddressBookUpdateDTO) -> Unit,
    modifier: Modifier = Modifier,
    editItem: AddressBookDTO? = null,
) {
    val needEdit = editItem != null

    // 更新地址
    var editSaveItem by remember {
        mutableStateOf(editItem?.toUpdateDTO())
    }
    // 添加地址
    var addSaveItem by remember {
        mutableStateOf(AddressBookAddDTO.EMPTY)
    }

    Surface(
        modifier = modifier,
        color = Color(0xfff0f0f0)
    ) {

        Box(modifier = Modifier.fillMaxSize()) {

            ConstraintLayout(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding(),
                constraintSet = AddressAddScreenDefaults.constraintSet()
            ) {
                // 顶部栏
                TopBarWithBack(
                    onBackClick = onBackClick,
                    title = if (needEdit) "修改地址" else "添加地址",
                    modifier = Modifier.layoutId("top-bar")
                )
                // 中间的输入内容
                Column(
                    modifier = Modifier.layoutId("content").padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val divider = @Composable {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            thickness = 0.2.dp,
                        )
                    }

                    TextInputEditItem(
                        prefix = "联系人",
                        placeHolder = "请填写收货人姓名",
                        value = if (needEdit) {
                            editSaveItem?.consignee ?: ""
                        } else {
                            addSaveItem.consignee
                        },
                        onValueChange = {
                            if (needEdit) {
                                editSaveItem = editSaveItem?.copy(consignee = it)
                            } else {
                                addSaveItem = addSaveItem.copy(consignee = it)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    divider()
                    SexEditItem(
                        value = if (needEdit) {
                            editSaveItem?.sex ?: -1
                        } else {
                            addSaveItem.sex
                        },
                        onValueChange = {
                            if (needEdit) {
                                editSaveItem = editSaveItem?.copy(sex = it)
                            } else {
                                addSaveItem = addSaveItem.copy(sex = it)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()

                    )
                    divider()

                    TextInputEditItem(
                        prefix = "手机号",
                        placeHolder = "请填写收货手机号",
                        value = if (needEdit) {
                            editSaveItem?.phone ?: ""
                        } else {
                            addSaveItem.phone
                        },
                        onValueChange = {
                            if (needEdit) {
                                editSaveItem = editSaveItem?.copy(phone = it)
                            } else {
                                addSaveItem = addSaveItem.copy(phone = it)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()

                    )
                    divider()

                    TextInputEditItem(
                        prefix = "具体地址",
                        placeHolder = "例: A座8102室",
                        value = if (needEdit) {
                            editSaveItem?.detail ?: ""
                        } else {
                            addSaveItem.detail
                        },
                        onValueChange = {
                            if (needEdit) {
                                editSaveItem = editSaveItem?.copy(detail = it)
                            } else {
                                addSaveItem = addSaveItem.copy(detail = it)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()

                    )
                }
                // 确认按钮
                Button(
                    onClick = {
                        if (needEdit) {
                            editSaveItem?.let(onEditedClick)
                        } else {
                            addSaveItem.let(onAddedClick)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .layoutId("save-button")
                ) {
                    Icon(imageVector = Icons.Outlined.Save, contentDescription = null)
                    Text(text = "保存")
                }

            }
        }
    }
}

object AddressAddScreenDefaults {
    @Composable
    fun constraintSet() = ConstraintSet {
        val (topBarRef, contentRef, buttonRef) = createRefsFor(
            "top-bar", "content", "save-button"
        )
        constrain(topBarRef) {
            top()
            horizontalCenter()
        }
        constrain(contentRef) {
            top.linkTo(topBarRef.bottom, 16.dp)
            horizontalCenter()
        }
        constrain(buttonRef) {
            top.linkTo(contentRef.bottom, 16.dp)
            horizontalCenter()
        }
    }
}


@Composable
private fun EditItem(
    prefix: String,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = prefix,
            modifier = Modifier.width(64.dp),
            fontSize = 16.sp,
            fontWeight = FontWeight.W500
        )
        Spacer(modifier = Modifier.width(16.dp))
        content()
    }
}

@Composable
private fun TextInputEditItem(
    prefix: String,
    placeHolder: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    EditItem(
        prefix = prefix,
        modifier = modifier
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            colors = OutlinedTextFieldDefaults.colors().copy(
                disabledIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedContainerColor = LightGrayTextFieldContainerColor,
            ),
            placeholder = {
                Text(text = placeHolder)
            },
            maxLines = 1
        )
    }
}


@Composable
private fun SexEditItem(
    modifier: Modifier = Modifier,
    value: Short = 0,
    onValueChange: (Short) -> Unit,
) {
    EditItem(prefix = "性别", modifier = modifier) {
        TextButton(onClick = {
            onValueChange(1)
        }) {
            RadioButton(selected = (value.toInt() == 1), onClick = { })
            Text(text = "男")
        }
        TextButton(onClick = {
            onValueChange(0)
        }) {
            RadioButton(selected = (value.toInt() == 0), onClick = { })
            Text(text = "女")
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun AddressAddScreenPreview() {
    AddressAddScreen(editItem = null, onBackClick = {

    },
        onAddedClick = {}, onEditedClick = {})
}