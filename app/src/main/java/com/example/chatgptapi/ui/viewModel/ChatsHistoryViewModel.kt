package com.example.chatgptapi.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatgptapi.data.ChatRepository
import com.example.chatgptapi.model.databaseModels.SessionEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatsHistoryViewModel : ViewModel() {

    private val _chatsHistoryStateFlow = MutableStateFlow(emptyList<SessionEntity>())
    val chatsHistoryStateFlow = _chatsHistoryStateFlow.asStateFlow()

    init {
        viewModelScope.launch {
            ChatRepository.getChatSessions().collect {
                _chatsHistoryStateFlow.emit(it)
            }
        }
    }

    fun onSessionDeleteClick(session: SessionEntity) {
        viewModelScope.launch {
            ChatRepository.deleteSession(session)
        }
    }

    fun onUpdateSessionName(session: SessionEntity, name: String) {
        viewModelScope.launch {
            ChatRepository.updateSessionName(session, name)
        }
    }
}