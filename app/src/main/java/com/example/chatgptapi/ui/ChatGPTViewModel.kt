package com.example.chatgptapi.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatgptapi.data.ChatGptRepository
import com.example.chatgptapi.model.AiModel
import com.example.chatgptapi.model.UiAiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatGPTViewModel : ViewModel() {

    private val _mutableStateFlow = MutableStateFlow(emptyList<AiModel>())
    val stateFlow = _mutableStateFlow.asStateFlow()
    private val _mutableStateFlowUiAiModel= MutableStateFlow(emptyList<UiAiModel>())
    val stateFlowUiAiModel = _mutableStateFlowUiAiModel.asStateFlow()


    fun loadAiModels() {
        viewModelScope.launch(Dispatchers.IO) {
            ChatGptRepository.getModels()?.let {
                //TODO show error dialog for fail case
                _mutableStateFlow.emit(it.data ?: emptyList())
            }
        }
    }

    fun loadUiAiModels() {
        viewModelScope.launch(Dispatchers.IO) {
                _mutableStateFlowUiAiModel.emit(ChatGptRepository.getUiAiModels())
        }
    }
}