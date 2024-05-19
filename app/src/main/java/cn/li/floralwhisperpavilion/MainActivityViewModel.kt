package cn.li.floralwhisperpavilion

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.li.datastore.FwpPreferencesDataStore
import cn.li.model.constant.AppRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

private val TAG = MainActivityViewModel::class.simpleName

/**
 * [MainActivity] 的 ViewModel
 * */
@HiltViewModel
internal class MainActivityViewModel @Inject constructor(
    userDataStore: FwpPreferencesDataStore,
) : ViewModel() {

    /**
     * UI状态，根据 [FwpPreferencesDataStore] 中存储的用户身份进行比对
     *
     * 说明：
     * 1. [stateIn] 将 Flow 转换为热流，initialValue 为流启动时发送的第一个值
     * */
    val uiState: StateFlow<MainActivityUiState> = userDataStore.userData
        .map {
            Log.d(
                TAG,
                "uiState:map: \nuserId: ${it.userId} \ntoken: ${it.token} \nidentification: ${it.identification} \nlastLoginTime: ${it.loginTimestamp}"
            )
            when (it.identification) {
                AppRole.USER -> MainActivityUiState.UserLogin
                AppRole.EMPLOYEE -> MainActivityUiState.EmployeeLogin
                else -> MainActivityUiState.UnLogin
            }
        }
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MainActivityUiState.Loading
        )
}

/**
 * UI状态集合
 * */
sealed interface MainActivityUiState {
    /**
     * 加载中
     * */
    data object Loading : MainActivityUiState

    /**
     * 未登录
     * */
    data object UnLogin : MainActivityUiState

    /**
     * 用户登录
     * */
    data object UserLogin : MainActivityUiState


    /**
     * 店员登录
     * */
    data object EmployeeLogin : MainActivityUiState

}