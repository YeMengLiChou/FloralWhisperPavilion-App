package cn.li.data.util

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.core.content.getSystemService
import cn.li.common.network.Dispatcher
import cn.li.common.network.FwpDispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject


/**
 * 网络连接性监视器，实现了 [NetworkMonitor] 接口
 * */
internal class ConnectivityManagerNetworkMonitor @Inject constructor(
    @ApplicationContext private val context: Context,
    @Dispatcher(FwpDispatcher.IO) private val dispatcher: CoroutineDispatcher
): NetworkMonitor {
    override val isOnline: Flow<Boolean> = callbackFlow {
        val connectivityManager = context.getSystemService<ConnectivityManager>()

        if (connectivityManager == null) {
            channel.trySend(false)
            channel.close()
            return@callbackFlow
        }

        val callback = object: NetworkCallback() {
            private val networks = mutableListOf<Network>()

            /**
             * 每当连接一个新网络且该网络可用，则回调
             * */
            override fun onAvailable(network: Network) {
                networks += network
                channel.trySend(true)
            }

            /**
             * 每当断开连接或网络不可用，则回调
             * */
            override fun onLost(network: Network) {
                networks -= network
                channel.trySend(networks.isNotEmpty())
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        // 注册回调，当满足 request 时调用 callback
        connectivityManager.registerNetworkCallback(request, callback)

        // 上面的回调还没生效，发送最新的状态
        channel.trySend(connectivityManager.isCurrentlyConnected())

        // 当关闭时，取消注册回调
        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }
        .flowOn(dispatcher)
        .conflate()
}

@SuppressLint("ObsoleteSdkInt")
@Suppress("DEPRECATION")
private fun ConnectivityManager.isCurrentlyConnected() = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ->
        activeNetwork
            ?.let(::getNetworkCapabilities)
            ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)

    else -> activeNetworkInfo?.isConnected
} ?: false
