package cn.li.feature.mine.ui.info

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cn.li.datastore.UserPreferences
import cn.li.feature.mine.UserMineViewModel
import coil.compose.AsyncImage

@Composable
fun UserDataScreen(
    userdata: UserPreferences,
    onBackClick: () -> Unit,
    viewModel: UserMineViewModel = hiltViewModel()
) {
    Surface(

    ) {
        Column {
            TopBar(onBackClick = onBackClick)
        }
        Card {
            UserInfoChipItem(keyText = "头像", onClick = { /*TODO*/ }) {
//                AsyncImage(model = , contentDescription = )
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
            .height(48.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onBackClick) {
            Row {
                Icon(imageVector = Icons.Outlined.ChevronLeft, contentDescription = null)
                Text(text = "个人信息")
            }
        }
    }
}

@Composable
private fun UserInfoChipItem(
    keyText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    value: @Composable () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = keyText)
        Row {
            value()
            Icon(imageVector = Icons.Outlined.ChevronRight, contentDescription = null)
        }
    }
}