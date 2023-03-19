package com.chatgpt.letaithink.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chatgpt.letaithink.data.ApiKeyRepository
import com.chatgpt.letaithink.utils.emit
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class ApiKeyViewModel : ViewModel() {

    private val _apiKeyValidationSharedFlow = MutableSharedFlow<Boolean>()
    val apiKeyValidationSharedFlow = _apiKeyValidationSharedFlow.asSharedFlow()

    private val _validationInProcess = MutableSharedFlow<Boolean>()
    val validationInProgress = _validationInProcess.asSharedFlow()

    private val _exceptionFlow = MutableSharedFlow<Throwable>()
    val exceptionFlow = _exceptionFlow.asSharedFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        _exceptionFlow.emit(exception, viewModelScope)
    }

    fun onAcceptClick(apiKey: String) {
        viewModelScope.launch(exceptionHandler) {
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