package cn.li.feature.mine

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.li.datastore.FwpPreferencesDataStore
import cn.li.datastore.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton


private const val TAG = "UserMineViewModel"

@HiltViewModel
class UserMineViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val userDataSource: FwpPreferencesDataStore
) : ViewModel() {

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