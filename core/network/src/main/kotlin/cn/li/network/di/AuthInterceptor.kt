package cn.li.network.di

import cn.li.datastore.FwpPreferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
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
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = dataStore.userData.value.token

        // token 存在时才加请求头
        val request = if (token.isNotBlank()) {
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
                dataStore.updateJwtToken(it)
            }
        return response
    }
}