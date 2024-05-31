package cn.li.network.di

import cn.li.network.api.common.CommonDataSource
import cn.li.network.api.employee.EmployeeDataSource
import cn.li.network.api.user.UserAddressBookDataSource
import cn.li.network.api.user.UserCommodityDataSource
import cn.li.network.api.user.UserDataSource
import cn.li.network.api.user.UserOrderDataSource
import cn.li.network.api.user.UserShopDataSource
import cn.li.network.api.user.UserShoppingCartDataSource
import cn.li.network.retrofit.datasource.RetrofitAddressDataSource
import cn.li.network.retrofit.datasource.RetrofitCommonDataSource
import cn.li.network.retrofit.datasource.RetrofitEmployeeDataSource
import cn.li.network.retrofit.datasource.RetrofitUserCommodityDataSource
import cn.li.network.retrofit.datasource.RetrofitUserDataSource
import cn.li.network.retrofit.datasource.RetrofitUserOrderDataSource
import cn.li.network.retrofit.datasource.RetrofitUserShopCartDataSource
import cn.li.network.retrofit.datasource.RetrofitUserShopDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class DataSourceModule {

    @Binds
    abstract fun bindsRetrofitUserDataSource(
        retrofitUserDataSource: RetrofitUserDataSource
    ): UserDataSource


    @Binds
    abstract fun bindsRetrofitEmployeeDataSource(
        retrofitEmployeeDataSource: RetrofitEmployeeDataSource
    ): EmployeeDataSource


    @Binds
    abstract fun bindsRetrofitCommonDataSource(
        retrofitCommonDataSource: RetrofitCommonDataSource
    ): CommonDataSource


    @Binds
    abstract fun bindsRetrofitAddressDataSource(
        retrofitAddressDataSource: RetrofitAddressDataSource
    ): UserAddressBookDataSource

    @Binds
    abstract fun bindsRetrofitUserOrderDataSource(
        retrofitAddressDataSource: RetrofitUserOrderDataSource
    ): UserOrderDataSource


    @Binds
    abstract fun bindsRetrofitUserShopDataSource(
        retrofitUserShopDataSource: RetrofitUserShopDataSource
    ): UserShopDataSource

    @Binds
    abstract fun bindsUserShopCartDataSource(
        retrofitUserShopCartDataSource: RetrofitUserShopCartDataSource
    ): UserShoppingCartDataSource

    @Binds
    abstract fun bindsUserCommodityDataSource(
        retrofitUserCommodityDataSource: RetrofitUserCommodityDataSource
    ): UserCommodityDataSource
}