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
    suspend fun updateUserData(
        userId: String,
        token: String,
        identification: Int,
        shopId: String = "",
        lastLoginTime: Long = System.currentTimeMillis()
    ) {
        try {
            userPreferences.updateData {
                it.copy {
                    this.userId = userId
                    this.token = token
                    this.identification = identification
                    this.shopId = shopId
                    this.loginTimestamp = lastLoginTime
                }
            }
        } catch (e: IOException) {
            Log.e("FwpPreferencesDataStore", "updateUserData: ", e)
        }
    }


}