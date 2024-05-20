package cn.li.network.di

import cn.li.datastore.FwpPreferencesDataStore
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
        val token = runBlocking {
            dataStore.userData.first().token
        }
        // token 存在时才加请求头
        val request = if (token.isNotBlank()) {
            chain.request()
                .newBuilder().apply {
                    addHeader("authorization", token)
                }.build()
        } else {
            chain.request()
        }
        return chain.proceed(request)
    }
}