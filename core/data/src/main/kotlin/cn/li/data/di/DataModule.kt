package cn.li.data.di

import cn.li.data.repository.ApiCommonRepository
import cn.li.data.repository.ApiUserRepository
import cn.li.data.repository.CommonRepository
import cn.li.data.repository.UserRepository
import cn.li.data.util.ConnectivityManagerNetworkMonitor
import cn.li.data.util.NetworkMonitor
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {

    /**
     * 提供 [ConnectivityManagerNetworkMonitor] 依赖项，与 [NetworkMonitor] 绑定
     * */
    @Binds
    abstract fun providesNetworkMonitor(
        networkMonitor: ConnectivityManagerNetworkMonitor
    ): NetworkMonitor

    @Binds
    abstract fun providesApiUserRepository(
        repository: ApiUserRepository
    ): UserRepository

    @Binds
    abstract fun providesApiCommonRepository(
        repository: ApiCommonRepository
    ): CommonRepository
}