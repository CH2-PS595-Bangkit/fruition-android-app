package com.dicoding.fruition1.ui.home

import androidx.lifecycle.ViewModel
import com.dicoding.fruition1.data.HistoryRepository

class HomeViewModel(
    private val repository: HistoryRepository
) : ViewModel() {



    /*private val _uiState: MutableStateFlow<UiState<List<OrderFruit>>> = MutableStateFlow(UiState.Loading)
    val uiState: StateFlow<UiState<List<OrderFruit>>>
        get() = _uiState

    fun getAllFruits() {
        viewModelScope.launch {
            repository.getAllFruits()
                .catch {
                    _uiState.value = UiState.Error(it.message.toString())
                }
                .collect { orderFruits ->
                    _uiState.value = UiState.Success(orderFruits)
                }
        }
    }*/
}