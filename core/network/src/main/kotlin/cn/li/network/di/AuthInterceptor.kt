package cn.li.network.di

import android.util.Log
import cn.li.datastore.FwpPreferencesDataStore
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * 加入验证头的拦截器
 * @param dataStore 读取 token 来源
 * */
class AuthInterceptor @Inject constructor(
    private val dataStore: FwpPreferencesDataStore
) : Interceptor {
    private companion object {
        const val TAG = "AuthIntercept"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = dataStore.currentUserData.token

        // token 存在时才加请求头
        val request = if (token.isNotBlank()) {
            Log.d(TAG, "intercept: add-token: $token")
            chain.request()
                .newBuilder().apply {
                    addHeader("authorization", token)
                }.build()
        } else {
            chain.request()
        }

        // 拦截响应
        val response = chain.proceed(request)
        // 写入datastore
        response.headers["authorization"]
            ?.takeIf { it.isNotBlank() }
            ?.let {
                Log.d(TAG, "intercept save-token: $it")
                dataStore.updateJwtToken(it)
            } ?: {
                Log.d(TAG, "intercept: no-token")
        }
        return response
    }
}