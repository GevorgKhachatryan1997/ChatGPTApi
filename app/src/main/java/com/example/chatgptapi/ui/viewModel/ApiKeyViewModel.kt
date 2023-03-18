package com.example.chatgptapi.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatgptapi.data.ApiKeyRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class ApiKeyViewModel : ViewModel() {

    private val _apiKeyValidationSharedFlow = MutableSharedFlow<Boolean>()
    val apiKeyValidationSharedFlow = _apiKeyValidationSharedFlow.asSharedFlow()

    private val _validationInProcess = MutableSharedFlow<Boolean>()
    val validationInProgress = _validationInProcess.asSharedFlow()

    fun onAcceptClick(apiKey: String) {
        viewModelScope.launch {
            _validationInProcess.emit(true)
            val valid = ApiKeyRepository.validateApiKey(apiKey)
            if (valid) {
                viewModelScope.launch {
                    ApiKeyRepository.insertApiKey(apiKey)
                }
            }
            _apiKeyValidationSharedFlow.emit(valid)
            _validationInProcess.emit(false)
        }
    }
}