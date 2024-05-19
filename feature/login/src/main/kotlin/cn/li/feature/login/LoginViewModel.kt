package cn.li.feature.login

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.li.data.repository.UserRepository
import cn.li.datastore.FwpPreferencesDataStore
import cn.li.feature.login.state.LoginUiState
import cn.li.model.constant.AppRole
import cn.li.network.dto.onError
import cn.li.network.dto.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 登录/注册界面对应的状态容器 ViewModel
 * */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val savedStatedHandle: SavedStateHandle,
    private val userRepository: UserRepository,
    private val userDataStore: FwpPreferencesDataStore
) : ViewModel() {

    private val _loginScreenUiState = MutableStateFlow<LoginUiState>(LoginUiState.Nothing)
    val loginUiState get() = _loginScreenUiState.asStateFlow()

    private var loginJob: Job? = null

    /**
     * 登录
     * */
    fun login(username: String, password: String) {
        _loginScreenUiState.value = LoginUiState.Loading
        loginJob?.cancel()
        loginJob = viewModelScope.launch {
            val result = userRepository.userLogin(username, password)
            result.onSuccess { data ->
                _loginScreenUiState.value = LoginUiState.Success
                data?.let {
                    viewModelScope.launch {
                        userDataStore.updateUserData(
                            userId = it.id.toString(),
                            token = it.token,
                            identification = AppRole.USER
                        )
                    }
                }
            }?.onError {
                _loginScreenUiState.value = LoginUiState.Failed(tips = it)
            }
        }
    }

    fun register(username: String, password: String) {
        viewModelScope.launch {
            userRepository.userRegister(username, password)
        }
    }

}
