package cn.li.feature.mine

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.li.data.repository.CommonRepository
import cn.li.data.repository.UserRepository
import cn.li.datastore.FwpPreferencesDataStore
import cn.li.datastore.proto.UserPreferences
import cn.li.datastore.proto.copy
import cn.li.network.dto.onError
import cn.li.network.dto.onSuccess
import cn.li.network.dto.user.update
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


private const val TAG = "UserMineViewModel"

@HiltViewModel
internal class UserMineViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val userDataSource: FwpPreferencesDataStore,
    private val userRepository: UserRepository,
    private val commonRepository: CommonRepository
) : ViewModel() {

    /**
     * 我的界面状态
     * */
    val uiState: StateFlow<UserMineUiState> = userDataSource.userDataStateFlow.map {
        return@map if (it.userId == 0L) {
            UserMineUiState.UnLogin
        } else {
            UserMineUiState.Success(it)
        }
    }
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UserMineUiState.Loading
        )

    /**
     * 用户的数据流
     * */
    val userData: StateFlow<UserPreferences> = userDataSource.userDataStateFlow

    /**
     * 用户信息界面状态
     * */
    private val _userInfoUiState: MutableStateFlow<UserInfoUiState> =
        MutableStateFlow(UserInfoUiState.Nothing)

    val userInfoUiState: StateFlow<UserInfoUiState> = _userInfoUiState.asStateFlow()

    private var updatePhoneJob: Job? = null

    /**
     * 更新用户电话号码
     * */
    fun updatePhone(phone: String) {
        _userInfoUiState.value = UserInfoUiState.Loading
        updatePhoneJob?.cancel()
        updatePhoneJob = viewModelScope.launch(Dispatchers.IO) {
            // 更新
            runCatching {
                userRepository.updateUserInfo(
                    // 从当前值更新
                    userData.value.update(phone = phone)
                )
            }.onSuccess { apiResult ->
                apiResult
                    .onSuccess {
                        // 更新本地
                        userDataSource.updateUserData(
                            onSuccess = {
                                _userInfoUiState.value = UserInfoUiState.Success("更新电话号码成功")
                            }
                        ) {
                            it.copy { this.phone = phone }
                        }
                    }
                    ?.onError {
                        Log.d(TAG, "update-phone-failed: $it ")
                        _userInfoUiState.value = UserInfoUiState.Failed(it)
                    }
            }.onFailure {
                Log.d(TAG, "update-phone-failed: $it ")
                _userInfoUiState.value = UserInfoUiState.Failed(it.message ?: "更新电话号码失败")
            }
        }
    }

    private var updateSexJob: Job? = null

    /**
     * 更新用户性别
     * */
    fun updateSex(sex: Int) {
        _userInfoUiState.value = UserInfoUiState.Loading
        updateSexJob?.cancel()
        updateSexJob = viewModelScope.launch(Dispatchers.IO) {
            // 更新
            runCatching {
                userRepository.updateUserInfo(
                    userData.value.update(sex = sex)
                )
            }.onSuccess { result ->
                result
                    .onSuccess {
                        userDataSource.updateUserData(
                            onSuccess = {
                                _userInfoUiState.value = UserInfoUiState.Success("更新性别成功")
                            }
                        ) {
                            it.copy { this.phone = phone }
                        }
                    }?.onError {
                        Log.d(TAG, "update-sex-failed: $it ")
                        _userInfoUiState.value = UserInfoUiState.Failed(it)
                    }
            }.onFailure {
                Log.d(TAG, "update-sex-failed: $it ")
                _userInfoUiState.value = UserInfoUiState.Failed(it.message ?: "更新性别失败")
            }
        }
    }


    private var updateAvatarJob: Job? = null

    /**
     * 更新头像
     * */
    fun updateAvatar(avatar: Any) {
        _userInfoUiState.value = UserInfoUiState.Loading
        updateAvatarJob?.cancel()

        updateAvatarJob = viewModelScope.launch(Dispatchers.IO) {
            Log.d(TAG, "updateAvatar: $avatar")
            // 1. 上传图片
            val avatarUrlResult = when (avatar) {
                is Uri -> commonRepository.uploadFile(avatar)
                is Bitmap -> commonRepository.uploadFile(avatar)
                is File -> commonRepository.uploadFile(avatar)
                else -> throw IllegalArgumentException("$avatar 类型不匹配")
            }
            avatarUrlResult
                .onSuccess(dataNullable = false) { url ->
                    // 2. 拿到url后更新
                    launch {
                        val apiResult = userRepository.updateUserInfo(
                            userData.value.update(avatar = url!!)
                        )
                        apiResult.onSuccess {
                            userDataSource.updateUserData(
                                onSuccess = {
                                    _userInfoUiState.value = UserInfoUiState.Success("更新头像成功")
                                }
                            ) {
                                it.copy { this.avatar = url }
                            }
                        }?.onError {
                            Log.d(TAG, "update-avatar-update-failed: $it ")
                            _userInfoUiState.value = UserInfoUiState.Failed(it)
                        }
                    }
                }?.onError {
                    Log.d(TAG, "update-avatar-upload-failed: $it ")
                    _userInfoUiState.value = UserInfoUiState.Failed("更新头像失败")
                }
        }
    }

    private var updateNicknameJob: Job? = null

    /**
     * 更新用户昵称
     * */
    fun updateNickname(nickname: String) {
        _userInfoUiState.value = UserInfoUiState.Loading
        updateNicknameJob?.cancel()

        updateNicknameJob = viewModelScope.launch(Dispatchers.IO) {
            val apiResult = userRepository.updateUserInfo(
                userData.value.update(nickname = nickname)
            )
            apiResult
                .onSuccess {
                    userDataSource.updateUserData(
                        onSuccess = {
                            _userInfoUiState.value = UserInfoUiState.Success("更新昵称成功")
                        }
                    ) {
                        it.copy { this.nickname = nickname }
                    }
                }?.onError {
                    Log.d(TAG, "updateNickname-failed: $it ")
                    _userInfoUiState.value = UserInfoUiState.Failed("更新昵称失败")
                }
        }
    }


    fun userMineError(msg: String) {
        _userInfoUiState.value = UserInfoUiState.Failed(msg)
    }

}


/**
 * 用户 “我的” 界面状态
 * */
sealed interface UserMineUiState {

    /**
     * 加载中，初次进入该界面时
     * */
    data object Loading : UserMineUiState

    /**
     * 已登录
     * @param userPreferences 登录用户的信息
     * */
    data class Success(
        val userPreferences: UserPreferences
    ) : UserMineUiState

    /**
     * 未登录
     * */
    data object UnLogin : UserMineUiState
}


/**
 * 用户数据界面的状态
 * */
internal sealed interface UserInfoUiState {
    data object Nothing : UserInfoUiState

    data object Loading : UserInfoUiState

    data class Success(val msg: String) : UserInfoUiState

    data class Failed(val msg: String) : UserInfoUiState
}