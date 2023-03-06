package com.example.chatgptapi.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatgptapi.data.*
import com.example.chatgptapi.model.*
import com.example.chatgptapi.model.databaseModels.SessionEntity
import com.example.chatgptapi.model.remoteModelts.CompletionRequest
import com.example.chatgptapi.utils.emit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// TODO Handle nonull calls
// TODO don't allow mutable questions at same time
class ChatViewModel : ViewModel() {

    private val _updateConversationItem = MutableStateFlow<List<ConversationItem>>(emptyList())
    val updateConversation = _updateConversationItem.asStateFlow()

    private val _chatModes = MutableStateFlow<List<ChatMode>>(emptyList()).apply {
        val modes = ChatRepository.chatModes.mapIndexed { index, chatMode -> if (index == 0) chatMode.copy(selected = true) else chatMode }
        emit(modes, viewModelScope)
    }
    val chatModes = _chatModes.asStateFlow()

    var session: SessionEntity? = null

    fun setChatSession(sessionId: String?) {
        viewModelScope.launch {
            sessionId?.let {
                session = ChatRepository.getChatSession(sessionId)
            }
        }
    }

    fun onSendClick(text: String) {
        viewModelScope.launch {
            if (session == null) {
                session = ChatRepository.createSession(text)
            }
            val selectedMode = getSelectedChatMode()
            when (selectedMode.mode) {
                CHAT_MODE_TEXT_COMPLETION,
                CHAT_MODE_CODE_COMPLETION -> {
                    onTextGeneration(text, selectedMode)
                }
                CHAT_MODE_IMAGE_GENERATION -> {
                    onImageGenerate(text)
                }
            }
        }
    }

    fun onChatModeSelected(chatMode: ChatMode) {
        val newMods = chatModes.value.map {
            it.copy(selected = it.mode == chatMode.mode)
        }
        _chatModes.emit(newMods, viewModelScope)
    }

    private fun onTextGeneration(text: String, mode: ChatMode) {
        session ?: return

        viewModelScope.launch {
            val question = UserMessage(text)
            updateConversation(question)
            updateConversation(AiThinking("Wait a second )"))

            val completion = CompletionRequest(UserRepository.getUser()?.userId!!, mode.model, text, 100, 0.3F)
            val result = ChatRepository.askQuestion(completion)
            val answer = AiMessage(result)

            replaceLastConversationItem(answer)
            ChatRepository.saveConversationItem(session!!, question, answer)
        }
    }

    private fun onImageGenerate(description: String) {
        session ?: return

        viewModelScope.launch {
            val question = UserMessage(description)
            updateConversation(question)
            updateConversation(AiThinking("Wait a second )"))

            val imageDescription = ImageGenerationRequest(UserRepository.getUser()?.userId!!, description, IMAGE_SIZE_512, 1)
            val result = ChatRepository.generateImage(imageDescription)
            val answer = AiImage(result!!)

            replaceLastConversationItem(answer)
            ChatRepository.saveConversationItem(session!!, question, answer)
        }
    }

    private suspend fun updateConversation(conversationItem: ConversationItem) {
        val newConversation = updateConversation.value + conversationItem
        _updateConversationItem.emit(newConversation)
    }

    private fun replaceLastConversationItem(conversationItem: ConversationItem) {
        val newConversation = updateConversation.value.dropLast(1) + conversationItem
        _updateConversationItem.emit(newConversation, viewModelScope)
    }

    private fun getSelectedChatMode() = chatModes.value.first { it.selected }
}