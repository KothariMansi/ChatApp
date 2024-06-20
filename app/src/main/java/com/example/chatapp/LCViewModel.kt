package com.example.chatapp

import androidx.lifecycle.ViewModel
import com.example.chatapp.data.LCState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LCViewModel @Inject constructor(): ViewModel() {
    private var _uiState = MutableStateFlow(LCState())
    val uiState = _uiState.asStateFlow()

    fun updateUi(
        name: String = _uiState.value.name,
        number: String = _uiState.value.number,
        email: String = _uiState.value.email,
        password: String = _uiState.value.password


    ) {
        _uiState.update {
            it.copy(
                name = name,
                number = number,
                email = email,
                password = password
            )
        }
    }

}