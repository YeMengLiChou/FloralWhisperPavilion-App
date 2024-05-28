package cn.li.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import cn.li.common.network.Dispatcher
import cn.li.common.network.FwpDispatcher
import cn.li.common.network.di.ApplicationScope
import cn.li.datastore.FwpCachedSerializer
import cn.li.datastore.FwpUserPreferencesSerializer
import cn.li.datastore.proto.CachedPreferences
import cn.li.datastore.proto.UserPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    /**
     * 提供 [DataStore]<[UserPreferences]> 依赖项
     * @param context
     * @param scope
     * @param ioDispatcher
     * @param userPreferencesSerializer
     * */
    @Provides
    @Singleton
    internal fun providesUserPreferencesDataStore(
        @ApplicationContext context: Context,
        @Dispatcher(FwpDispatcher.IO) ioDispatcher: CoroutineDispatcher,
        @ApplicationScope scope: CoroutineScope,
        userPreferencesSerializer: FwpUserPreferencesSerializer
    ): DataStore<UserPreferences> {
        return DataStoreFactory.create(
            serializer = userPreferencesSerializer,
            scope = CoroutineScope(scope.coroutineContext + ioDispatcher),
        ) {
            context.dataStoreFile("user_preferences.pb")
        }
    }


    @Provides
    @Singleton
    internal fun providesCachedDataStore(
        @ApplicationContext context: Context,
        @Dispatcher(FwpDispatcher.IO) ioDispatcher: CoroutineDispatcher,
        @ApplicationScope scope: CoroutineScope,
        cachedSerializer: FwpCachedSerializer
    ): DataStore<CachedPreferences> {
        return DataStoreFactory.create(
            serializer = cachedSerializer,
            scope = CoroutineScope(scope.coroutineContext + ioDispatcher),
        ) {
            context.dataStoreFile("cached_preferences.pb")
        }
    }

}