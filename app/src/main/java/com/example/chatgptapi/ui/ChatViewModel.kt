package com.example.chatgptapi.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatgptapi.data.ChatGptRepository
import com.example.chatgptapi.model.*
import com.example.chatgptapi.utils.emit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

// TODO Handle nonull calls
class ChatViewModel: ViewModel() {

    lateinit var model: UiAiModel

    private val conversation = ArrayList<ConversationItem>()

    private val _updateConversationItem = MutableSharedFlow<List<ConversationItem>>()
    val updateConversation = _updateConversationItem.asSharedFlow()

    fun sendMessage(text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val completion = CompletionRequest("artavazd1234", model.id, text, 100, 0.2F)
            updateConversation(UserMessage(text))
            updateConversation(AiThinking("Wait a second )"))
            val result = ChatGptRepository.askQuestion(completion)
            replaceLastConversationItem(AiMessage(result))
        }
    }

    fun generateImage(description: String) {
        viewModelScope.launch(Dispatchers.IO) {
            updateConversation(UserMessage(description))
            updateConversation(AiThinking("Wait a second )"))
            val imageDescription = ImageGenerationRequest("artavazd1234", description, IMAGE_SIZE_512, 1)
            val result = ChatGptRepository.generateImage(imageDescription)
            replaceLastConversationItem(AiImage(result!!))
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

    sealed interface ConversationItem

    data class AiThinking(val message: String): ConversationItem
    data class UserMessage(val message: String): ConversationItem
    data class AiMessage(val textCompletion: TextCompletion?): ConversationItem
    data class AiImage(val image: ImageModel): ConversationItem
}