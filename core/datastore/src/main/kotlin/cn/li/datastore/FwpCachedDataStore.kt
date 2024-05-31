package cn.li.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import cn.li.common.network.di.ApplicationScope
import cn.li.datastore.proto.CachedPreferences
import cn.li.datastore.proto.copy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 缓存用户使用信息
 * */
@Singleton
class FwpCachedDataStore @Inject constructor(
    @ApplicationScope private val scope: CoroutineScope,
    private val datastore: DataStore<CachedPreferences>
) {

    init {
        scope.launch {
            datastore.data.collect {
                data = it
            }
        }
    }

    var data: CachedPreferences = runBlocking { datastore.data.first() }

    val dataFlow get() = datastore.data

    /**
     * 更新选择的店铺id
     * */
    fun updateChosenShopId(shopId: Long) {
        scope.launch(Dispatchers.IO) {
            datastore.updateData {
                it.copy {
                    Log.d(
                        "FwpCachedDataStore",
                        "updateChosenShopId: old: ${it.lastChosenShopId}  ${data.lastChosenShopId}"
                    )
                    this.lastChosenShopId = shopId
                }
            }
        }
    }
}