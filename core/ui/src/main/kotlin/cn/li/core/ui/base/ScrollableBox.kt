package cn.li.core.ui.base

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 可刷新的Box容器
 * */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeRefreshBox(
    refreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    enableRefresh: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    userScrollEnabled: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = refreshing,
        onRefresh = onRefresh
    )
    Box(
        modifier = modifier.pullRefresh(
            state = pullRefreshState,
            enabled = enableRefresh
        )
    ) {
        PullRefreshIndicator(
            refreshing = refreshing,
            state = pullRefreshState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .zIndex(10f)

        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = userScrollEnabled,
            contentPadding = contentPadding
        ) {
            item {
                Box(modifier = Modifier.fillMaxSize(), content = content)
            }
        }

    }
}


@Composable
@Preview(showBackground = true)
private fun SwipeRefreshBoxPreview() {
    Box(modifier = Modifier.fillMaxSize()) {
        var refreshing by remember {
            mutableStateOf(false)
        }
        val scope = rememberCoroutineScope()
        SwipeRefreshBox(refreshing = refreshing, onRefresh = {
            scope.launch {
                refreshing = true
                delay(3_000)
                refreshing = false
            }
        }) {
            Text(text = "hellow", modifier = Modifier.fillMaxSize())
        }
    }
}