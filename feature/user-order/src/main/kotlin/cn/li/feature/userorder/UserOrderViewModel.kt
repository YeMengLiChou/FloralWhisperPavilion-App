package cn.li.feature.userorder

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import cn.li.data.repository.UserOrderRepository
import cn.li.network.dto.user.OrderDetailDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserOrderViewModel
@Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val userOrderRepository: UserOrderRepository
) : ViewModel() {

    private val _userOrderUiState = MutableStateFlow<UserOrderUiState>(UserOrderUiState.Loading)
    val userOrderUiState = _userOrderUiState.asStateFlow()

    private val _uncompletedOrder = MutableStateFlow<PagingData<OrderDetailDTO>>(PagingData.empty())
    val uncompletedOrder = _uncompletedOrder.asStateFlow()

    private var getUnCompletedOrderJob: Job? = null

    fun getUncompletedOrder() {
        _userOrderUiState.value = UserOrderUiState.Loading
        getUnCompletedOrderJob?.cancel()

        getUnCompletedOrderJob = viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                userOrderRepository.getUncompletedOrderPagingData()
                    .collectLatest { _uncompletedOrder.value = it }
            }.onFailure {
                if (it is CancellationException) return@onFailure
                Log.d(TAG, "getUncompletedOrder-failed: $it")
                _userOrderUiState.value = UserOrderUiState.Failure(it.message ?: "出现问题，请刷新")
            }
        }
    }

    private val _completedOrder = MutableStateFlow<PagingData<OrderDetailDTO>>(PagingData.empty())
    val completedOrder = _completedOrder.asStateFlow()

    private var getCompletedOrderJob: Job? = null

    fun getCompletedOrder() {
        _userOrderUiState.value = UserOrderUiState.Loading
        getCompletedOrderJob?.cancel()

        getCompletedOrderJob = viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                userOrderRepository.getCompletedOrderPagingData()
                    .collectLatest { _completedOrder.value = it }
            }.onFailure {
                if (it is CancellationException) return@onFailure
                Log.d(TAG, "getCompletedOrder-failed: $it")
                _userOrderUiState.value = UserOrderUiState.Failure(it.message ?: "出现问题，请刷新")
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

    companion object {
        private const val TAG = "UserOrderViewModel"
    }
}


sealed interface UserOrderUiState {
    data object Loading : UserOrderUiState
    data class Success(val userOrder: List<OrderDetailDTO>) : UserOrderUiState

    data class Failure(val error: String) : UserOrderUiState
}