package cn.li.feature.login

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.li.data.repository.UserRepository
import cn.li.datastore.FwpPreferencesDataStore
import cn.li.feature.login.state.LoginUiState
import cn.li.feature.login.state.RegisterUiState
import cn.li.model.constant.AppRole
import cn.li.network.dto.ApiResult
import cn.li.network.dto.employee.EmployeeLoginResult
import cn.li.network.dto.onError
import cn.li.network.dto.onSuccess
import cn.li.network.dto.user.UserLoginResult
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
    fun login(username: String, password: String, employeeLogin: Boolean) {
        _loginScreenUiState.value = LoginUiState.Loading
        // 取消先前的协程
        loginJob?.cancel()

        loginJob = viewModelScope.launch {
            // 登录拿到结果
            val result = if (employeeLogin) {
                userRepository.employeeLogin(username, password)
            } else {
                userRepository.userLogin(username, password)
            }
            result.onSuccess { data ->
                _loginScreenUiState.value = LoginUiState.Success
                // 写入缓存
                data?.let {
                    when (it) {
                        is UserLoginResult -> {
                            viewModelScope.launch {
                                userDataStore.updateUserData(
                                    userId = it.id.toString(),
                                    token = it.token,
                                    identification = AppRole.USER
                                )
                            }
                        }

                        is EmployeeLoginResult -> {
                            viewModelScope.launch {
                                userDataStore.updateUserData(
                                    userId = it.id.toString(),
                                    token = it.token,
                                    identification = AppRole.EMPLOYEE,
                                    shopId = it.shopId.toString()
                                )
                            }
                        }
                    }
                }
            }?.onError {
                _loginScreenUiState.value = LoginUiState.Failed(tips = it)
            }
        }
    }




    // ============================= Register ===============================================


    private val _registerScreenUiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Nothing)
    val registerUiState = _registerScreenUiState.asStateFlow()
    private var registerJob: Job? = null

    /**
     * 注册操作
     */
    fun register(username: String, password: String) {
        _registerScreenUiState.value = RegisterUiState.Loading
        registerJob?.cancel()

        viewModelScope.launch {
            val result = userRepository.userRegister(username, password)
            result.onSuccess {
                _registerScreenUiState.value = RegisterUiState.Success
            }?.onError { msg ->
                _registerScreenUiState.value = RegisterUiState.Failed(msg = msg)
            }
        }
    }

}
