package cn.li.core.ui.loading

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadingDialog(
    loading: Boolean,
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit = {}
) {
    if (loading) {
        BasicAlertDialog(onDismissRequest = onDismissRequest, modifier = modifier) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(strokeWidth = 1.dp)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun LoadingDialogPreview() {
    Box(modifier = Modifier.size(100.dp), contentAlignment = Alignment.Center) {
        LoadingDialog(loading = true)
    }
}