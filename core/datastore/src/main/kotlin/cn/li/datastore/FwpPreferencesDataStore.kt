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
     * 用户数据流，最新数据
     * */
    val userData get() = userPreferences.data

    /**
     * 更新用户数据，token 和 id
     * */
    suspend fun updateUserData(userId: String, token: String, lastLoginTime: Long) {
        try {
            userPreferences.updateData {
                it.copy {
//                    this.userId = userId
                }
            }
        } catch (e: IOException) {
            Log.e("FwpPreferencesDataStore", "updateUserData: ", e)
        }
    }


}