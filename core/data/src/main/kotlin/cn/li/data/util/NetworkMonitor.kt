package cn.li.data.util

import kotlinx.coroutines.flow.Flow

/**
 * 网络连接状态的工具类接口
 * */
interface NetworkMonitor {
    val isOnline: Flow<Boolean>
}