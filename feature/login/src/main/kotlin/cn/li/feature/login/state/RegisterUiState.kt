package cn.li.feature.login.state

sealed interface RegisterUiState {

    data object Nothing: RegisterUiState

    data object Success: RegisterUiState

    data object Loading: RegisterUiState

    data class Failed(val msg: String): RegisterUiState
}