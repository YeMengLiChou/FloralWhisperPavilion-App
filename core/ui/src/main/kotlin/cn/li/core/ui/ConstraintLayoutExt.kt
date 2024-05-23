package cn.li.core.ui

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstrainScope


fun ConstrainScope.verticalCenter() {
    top.linkTo(parent.top)
    bottom.linkTo(parent.bottom)
}

fun ConstrainScope.horizontalCenter() {

    top.linkTo(parent.top)
    bottom.linkTo(parent.bottom)
}

fun ConstrainScope.center() {
    top.linkTo(parent.top)
    bottom.linkTo(parent.bottom)

    top.linkTo(parent.top)
    bottom.linkTo(parent.bottom)
}

private val zeroDp = 0.dp

fun ConstrainScope.end(margin: Dp = zeroDp) {
    end.linkTo(parent.end, margin)
}

fun ConstrainScope.start(margin: Dp = zeroDp) {
    start.linkTo(parent.start, margin)
}

fun ConstrainScope.top(margin: Dp = zeroDp) {
    top.linkTo(parent.top, margin)
}

fun ConstrainScope.bottom(margin: Dp = zeroDp) {
    bottom.linkTo(parent.bottom, margin)
}