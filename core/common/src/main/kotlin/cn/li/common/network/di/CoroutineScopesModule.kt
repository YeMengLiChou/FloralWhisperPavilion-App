package cn.li.common.network.di

import cn.li.common.network.Dispatcher
import cn.li.common.network.FwpDispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * 提供全应用唯一的协程作用域
 * */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ApplicationScope

/**
 * 提供协程域依赖项的 Hilt模块
 * */
@Module
@InstallIn(SingletonComponent::class)
internal object CoroutineScopesModule {

    @Singleton
    @Provides
    @ApplicationScope
    fun providesCoroutineScope(
        @Dispatcher(FwpDispatcher.DEFAULT) dispatcher: CoroutineDispatcher,
    ): CoroutineScope = CoroutineScope(SupervisorJob() + dispatcher)
}