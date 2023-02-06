package com.example.chatgptapi

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

    fun loadAiModels() {
        viewModelScope.launch(Dispatchers.IO) {
            ChatGptRepository.getModels()?.let {
                //TODO show error dialog for fail case
                _mutableStateFlow.emit(it.data ?: emptyList())
            }
        }
    }

    fun getUiAiModel(): List<UiAiModel> {
        return ChatGptRepository.getUiAiModels()
    }
}