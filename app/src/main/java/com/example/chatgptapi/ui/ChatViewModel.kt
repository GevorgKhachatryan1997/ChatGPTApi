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
class ChatViewModel: ViewModel() {

    lateinit var model: UiAiModel

    private val conversation = ArrayList<ConversationItem>()

    private val sessionEntity = SessionEntity(
        "session id",
        "session name",
        "user id"
    ) // TODO update session id

    private val _updateConversationItem = MutableSharedFlow<List<ConversationItem>>()
    val updateConversation = _updateConversationItem.asSharedFlow()

    fun onSendClick(test: String) {
        when(model.id) {
            LocalDataSource.IMAGE_GENERATION -> generateImage(test)
            else -> sendMessage(test)
        }
    }

    private fun sendMessage(text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val completion = CompletionRequest(UserInfo.userId, model.id, text, 100, 0.3F)
            val question = UserMessage(text)
            updateConversation(question)
            updateConversation(AiThinking("Wait a second )"))
            val result = ChatGptRepository.askQuestion(completion)
            val answer = AiMessage(result)
            replaceLastConversationItem(answer)
            ChatGptRepository.saveConversationItem(sessionEntity, question, answer)
        }
    }

    private fun generateImage(description: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val question = UserMessage(description)
            updateConversation(question)
            updateConversation(AiThinking("Wait a second )"))
            val imageDescription = ImageGenerationRequest(UserInfo.userId, description, IMAGE_SIZE_512, 1)
            val result = ChatGptRepository.generateImage(imageDescription)
            val answer = AiImage(result!!)
            replaceLastConversationItem(answer)
            ChatGptRepository.saveConversationItem(sessionEntity, question, answer)
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
}