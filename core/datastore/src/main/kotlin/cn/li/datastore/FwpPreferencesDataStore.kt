package cn.li.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import javax.inject.Inject

/**
 * 用户数据来源
 * */
class FwpPreferencesDataStore @Inject constructor(
    private val userPreferences: DataStore<UserPreferences>
) {
    /**
     * 更新用户数据，token 和 id
     * */
    suspend fun updateUserData() {
        try {
            userPreferences.updateData {
                it.copy {

                }
            }
        } catch (e: IOException) {
            Log.e("FwpPreferencesDataStore", "updateUserData: ", e)
        }
    }

}