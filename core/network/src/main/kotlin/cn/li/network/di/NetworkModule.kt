package cn.li.network.di

import android.content.Context
import cn.li.core.network.BuildConfig
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.util.DebugLogger
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


/**
 * 后端 url
 * */
private const val baseUrl = BuildConfig.BACKEND_URL


/**
 * 提供网络相关依赖项的 Hilt模块
 * */
@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    @Singleton
    @Provides
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor)
            .addInterceptor(
                HttpLoggingInterceptor()
                    .apply {
                        if (BuildConfig.DEBUG) {
                            setLevel(HttpLoggingInterceptor.Level.BODY)
                        }
                    }
            )
            .build()
    }


    @Provides
    @Singleton
    fun providesRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(
                JacksonConverterFactory.create(
                    ObjectMapper().apply {
                        registerModule(
                            KotlinModule.Builder()
                                .configure(KotlinFeature.StrictNullChecks, true)
                                .build()
                        )
                    }
                )
            )
            .client(okHttpClient)
            .build()
    }

    /**
     * 提供 [ImageLoader] 依赖项
     * */
    @Provides
    @Singleton
    fun imageLoader(
        client: OkHttpClient,
        @ApplicationContext application: Context,
    ): ImageLoader {
        return ImageLoader.Builder(application)
            .callFactory { client }
            .components { add(SvgDecoder.Factory()) }
            .respectCacheHeaders(false)
            .apply {
                if (BuildConfig.DEBUG) {
                    logger(DebugLogger())
                }
            }
            .build()
    }
}