package com.example.chatgptapi.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatgptapi.data.ChatGptRepository
import com.example.chatgptapi.model.CompletionRequest
import com.example.chatgptapi.model.TextCompletion
import com.example.chatgptapi.model.UiAiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class ChatViewModel: ViewModel() {

    lateinit var model: UiAiModel

    val conversations = ArrayList<Conversation>()

    private val _updateConversation = MutableSharedFlow<Unit>()
    val updateConversation = _updateConversation.asSharedFlow()

    fun sendMessage(text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val completion = CompletionRequest("artavazd1234", model.id, text, 100, 0.2F)
            val result = ChatGptRepository.askQuestion(completion)

            Log.d("completion","Completion: ${result.toString()}")

            conversations.add(Conversation(completion, result))
            _updateConversation.emit(Unit)
        }
    }

    data class Conversation(val completionRequest: CompletionRequest, val textCompletion: TextCompletion?)
}