package cn.li.feature.mine

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.li.datastore.FwpPreferencesDataStore
import cn.li.datastore.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton

@HiltViewModel
class UserMineViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val userDataSource: FwpPreferencesDataStore
) : ViewModel() {

    val uiState
        get() = userDataSource.userData.map {
            return@map if (it.userId == 0L) {
                UserMineUiState.UnLogin
            } else {
                UserMineUiState.Success(it)
            }
//            UserMineUiState.Loading
        }.
        stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UserMineUiState.Loading
        )


}


sealed interface UserMineUiState {

    data object Loading: UserMineUiState

    data class Success(
        val userPreferences: UserPreferences
    ): UserMineUiState

    data object UnLogin: UserMineUiState
}