package cn.li.common.network.di

import cn.li.common.network.Dispatcher
import cn.li.common.network.FwpDispatcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers

/**
 * 提供调度器依赖项的 Hilt模块
 *
 * 通过声明注解 [Dispatcher] 来指定所使用的调度器
 * */
@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {
    @Provides
    @Dispatcher(FwpDispatcher.IO)
    fun providesIODispatcher() = Dispatchers.IO

    @Provides
    @Dispatcher(FwpDispatcher.DEFAULT)
    fun providesDefaultDispatcher() = Dispatchers.Default
}