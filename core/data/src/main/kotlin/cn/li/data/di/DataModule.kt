package cn.li.data.di

import cn.li.data.repository.AddressRepository
import cn.li.data.repository.ApiAddressRepository
import cn.li.data.repository.ApiCommonRepository
import cn.li.data.repository.ApiUserOrderRepository
import cn.li.data.repository.ApiShopRepository
import cn.li.data.repository.ApiUserRepository
import cn.li.data.repository.ApiUserShopCartRepository
import cn.li.data.repository.CommonRepository
import cn.li.data.repository.UserOrderRepository
import cn.li.data.repository.ShopRepository
import cn.li.data.repository.UserRepository
import cn.li.data.repository.UserShopCartRepository
import cn.li.data.util.ConnectivityManagerNetworkMonitor
import cn.li.data.util.NetworkMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {

    /**
     * 提供 [ConnectivityManagerNetworkMonitor] 依赖项，与 [NetworkMonitor] 绑定
     * */
    @Binds
    abstract fun bindsNetworkMonitor(
        networkMonitor: ConnectivityManagerNetworkMonitor
    ): NetworkMonitor

    @Binds
    abstract fun bindsApiUserRepository(
        repository: ApiUserRepository
    ): UserRepository

    @Binds
    abstract fun bindsApiCommonRepository(
        repository: ApiCommonRepository
    ): CommonRepository


    @Binds
    abstract fun bindsApiAddressRepository(
        repository: ApiAddressRepository
    ): AddressRepository

    @Binds
    abstract fun bindsApiUserOrderRepository(
        repository: ApiUserOrderRepository
    ): UserOrderRepository

    @Binds
    abstract fun bindsApiShopRepository(
        repository: ApiShopRepository
    ): ShopRepository

    @Binds
    abstract fun bindsApiUserShopCartRepository(
        repository: ApiUserShopCartRepository
    ): UserShopCartRepository
}