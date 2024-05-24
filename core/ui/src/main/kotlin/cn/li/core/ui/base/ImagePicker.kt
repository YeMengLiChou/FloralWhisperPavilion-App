package cn.li.core.ui.base

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 底部弹窗的照片选择器，包括从相册选取 [onSelectedImage] 和 拍照获取 [onTakePicture]
 *
 * @param show 是否显示
 * @param title 顶部标题
 * @param onSelectedImage 选择图片获取到uri时回调
 * @param onTakePicture 拍照返回时回调
 * @param onDismissRequest 请求取消显示
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetImagePicker(
    show: Boolean,
    title: @Composable ColumnScope.() -> Unit,
    onSelectedImage: (Uri?) -> Unit,
    onTakePicture: (Bitmap?) -> Unit,
    onDismissRequest: () -> Unit
) {
    // 存放 uri 的部分
    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }
    // 存放拍照返回的bitmap
    var pictureBitmap by remember {
        mutableStateOf<Bitmap?>(null)
    }
    // 选择
    val imagePickerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {
            imageUri = it
            onSelectedImage(it)
            onDismissRequest()
        }

    val imageTakerLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicturePreview()) {
            pictureBitmap = it
            onTakePicture(it)
            onDismissRequest()
        }

    val bottomSheetState = rememberModalBottomSheetState()

    LaunchedEffect(key1 = show) {
        Log.d("BottomSheetImagePicker", "show: $show")
        if (bottomSheetState.hasExpandedState) {
            if (!show) {
                bottomSheetState.hide()
            }
        } else {
            if (show) {
                bottomSheetState.show()
            }
        }
    }

    if (show) {
        ModalBottomSheet(
            sheetState = bottomSheetState,
            onDismissRequest = onDismissRequest,
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
            ) {
                title()
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(
                    onClick = {
                        imagePickerLauncher.launch("image/*")
                    },
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)

                ) {
                    Icon(imageVector = Icons.Outlined.Photo, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "从相册选择",
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp
                    )
                }

                TextButton(
                    onClick = {
                        // TODO：拍照权限申请
                        imageTakerLauncher.launch()
                    },
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Icon(imageVector = Icons.Outlined.CameraAlt, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "拍照选择",
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}


@Composable
@Preview(showBackground = true, showSystemUi = true)
private fun ImagePickerPreview() {
    Box(modifier = Modifier.size(300.dp, 600.dp)) {
        Button(onClick = { /*TODO*/ }) {
            Text(text = "选择图片")
        }
        BottomSheetImagePicker(show = true, onDismissRequest = {

        }, onSelectedImage = {}, onTakePicture = {}, title = {
            Text(text = "更新图片")
        })
    }
}