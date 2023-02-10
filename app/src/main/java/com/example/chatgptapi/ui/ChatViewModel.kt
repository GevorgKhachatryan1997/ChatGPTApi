package com.example.chatgptapi.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatgptapi.data.*
import com.example.chatgptapi.model.*
import com.example.chatgptapi.model.databaseModels.SessionEntity
import com.example.chatgptapi.model.remoteModelts.CompletionRequest
import com.example.chatgptapi.ui.model.UiAiModel
import com.example.chatgptapi.utils.emit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

// TODO Handle nonull calls
// TODO don't allow mutable questions at same time
class ChatViewModel : ViewModel() {

    var model: UiAiModel = ChatRepository.getUiAiModels().first()

    private val conversation = ArrayList<ConversationItem>()

    var session: SessionEntity? = null

    private val _updateConversationItem = MutableSharedFlow<List<ConversationItem>>()
    val updateConversation = _updateConversationItem.asSharedFlow()

    fun setChatSession(sessionId: String?) {
        viewModelScope.launch {
            sessionId?.let {
                session = ChatRepository.getChatSession(sessionId)
            }
        }
    }

    fun onSendClick(test: String) {
        viewModelScope.launch {
            when (model.id) {
                LocalDataSource.IMAGE_GENERATION -> generateImage(test)
                else -> sendMessage(test)
            }
        }
    }

    private fun sendMessage(text: String) {
        session?.let {
            viewModelScope.launch {
                val completion = CompletionRequest(UserRepository.getUser()?.userId!!, model.id, text, 100, 0.3F)
                val question = UserMessage(text)
                updateConversation(question)
                updateConversation(AiThinking("Wait a second )"))
                val result = ChatRepository.askQuestion(completion)
                val answer = AiMessage(result)
                replaceLastConversationItem(answer)
                ChatRepository.saveConversationItem(it, question, answer)
            }
        } ?: run {
            createSession(text)
        }
    }

    private fun generateImage(description: String) {
        viewModelScope.launch {
            session?.let {
                val question = UserMessage(description)
                updateConversation(question)
                updateConversation(AiThinking("Wait a second )"))
                val imageDescription = ImageGenerationRequest(UserRepository.getUser()?.userId!!, description, IMAGE_SIZE_512, 1)
                val result = ChatRepository.generateImage(imageDescription)
                val answer = AiImage(result!!)
                replaceLastConversationItem(answer)
                ChatRepository.saveConversationItem(it, question, answer)
            } ?: run {
                createSession(description)
            }
        }
    }

    private fun updateConversation(conversationItem: ConversationItem) {
        conversation.add(conversationItem)
        _updateConversationItem.emit(conversation, viewModelScope)
    }

    private fun replaceLastConversationItem(conversationItem: ConversationItem) {
        with(conversation) {
            removeLast()
            updateConversation(conversationItem)
        }
    }

    private fun createSession(name: String) {
        viewModelScope.launch {
            session = ChatRepository.createSession(name)
        }
    }
}