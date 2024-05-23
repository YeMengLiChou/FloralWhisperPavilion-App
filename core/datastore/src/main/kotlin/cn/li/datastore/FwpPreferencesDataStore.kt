package cn.li.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import cn.li.common.network.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 用户数据来源
 * */
class FwpPreferencesDataStore @Inject constructor(
    @ApplicationScope private val scope: CoroutineScope,
    private val userPreferences: DataStore<UserPreferences>
) {
    /**
     * 用户数据流，最新数据
     * */
    val userData
        get() = userPreferences.data.stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(3_000),
            initialValue = UserPreferences.getDefaultInstance()
        )

    /**
     * 更新用户数据，token 和 id
     * */
    fun updateUserData(
        id: Long,
        username: String,
        avatar: String,
        createTime: Long,
        updateTime: Long,
        phone: String,
        sex: Int,
        identification: Int,
        shopId: Long,
        lastLoginTime: Long
    ) {
        scope.launch(Dispatchers.IO) {
            try {
                userPreferences.updateData {
                    it.copy {
                        this.userId = id
                        this.username = username
                        this.avatar = avatar
                        this.createTime = createTime
                        this.updateTime = updateTime
                        this.phone = phone
                        this.sex = sex
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


    fun updateUserData(block: (UserPreferences) -> UserPreferences) {
        scope.launch(Dispatchers.IO) {
            try {
                userPreferences.updateData(block)
            } catch (e: IOException) {
                Log.e("FwpPreferencesDataStore", "updateUserData: ", e)
            }
        }
    }

    /**
     * 更新 jwt token
     * */
    fun updateJwtToken(token: String) {
        scope.launch(Dispatchers.IO) {
            try {
                userPreferences.updateData {
                    it.copy {
                        this.token = token
                    }
                }
            } catch (e: IOException) {
                Log.e("FwpPreferencesDataStore", "updateUserData: ", e)
            }
        }
    }


}