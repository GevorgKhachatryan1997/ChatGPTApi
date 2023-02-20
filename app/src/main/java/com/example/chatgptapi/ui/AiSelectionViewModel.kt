package com.example.chatgptapi.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatgptapi.data.ChatGptRepository
import com.example.chatgptapi.ui.model.UiAiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AiSelectionViewModel : ViewModel() {

    private val _mutableStateFlowUiAiModels = MutableStateFlow(emptyList<UiAiModel>())
    val stateFlowUiAiModels = _mutableStateFlowUiAiModels.asStateFlow()

    fun loadUiAiModels() {
        viewModelScope.launch {
            _mutableStateFlowUiAiModels.emit(ChatGptRepository.getUiAiModels())
        }
    }
}