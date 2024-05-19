package cn.li.feature.login.state

/**
 * 登录界面的状态
 * */
sealed interface LoginUiState {

    /**
     * 登录/注册成功
     * */
    data object Success : LoginUiState

    /**
     * 登录/注册失败
     * @param tips 提示
     * */
    data class Failed(val tips: String?) : LoginUiState

    /**
     * 登录/注册等待ing
     * */
    data object Loading : LoginUiState

    /**
     * 无事发生
     * */
    data object Nothing: LoginUiState
}