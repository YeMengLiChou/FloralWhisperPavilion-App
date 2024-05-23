package cn.li.feature.mine

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.li.datastore.FwpPreferencesDataStore
import cn.li.datastore.UserPreferences
import cn.li.datastore.copy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


private const val TAG = "UserMineViewModel"

@HiltViewModel
internal class UserMineViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val userDataSource: FwpPreferencesDataStore
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

    fun updatePhone(phone: String) {
        _userInfoUiState.value = UserInfoUiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            // 更新

            userDataSource.updateUserData {
                it.copy {
                    this.phone = phone
                }
            }
        }
    }
    fun updateSex(sex: Int) {
        _userInfoUiState.value = UserInfoUiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            // 更新

            userDataSource.updateUserData {
                it.copy {
                    this.sex = sex
                }
            }
        }
    }

    fun uploadAvatar() {

    }

    fun updateAvatar() {
        _userInfoUiState.value = UserInfoUiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            // 更新

            userDataSource.updateUserData {
                it.copy {
                    this.phone = phone
                }
            }
        }
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