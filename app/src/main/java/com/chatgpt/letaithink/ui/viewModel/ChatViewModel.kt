package com.chatgpt.letaithink.ui.viewModel

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chatgpt.letaithink.model.remoteModelts.IMAGE_SIZE_1024
import com.chatgpt.letaithink.model.remoteModelts.ImageGenerationRequest
import com.chatgpt.letaithink.R
import com.chatgpt.letaithink.data.*
import com.chatgpt.letaithink.model.*
import com.chatgpt.letaithink.model.databaseModels.SessionEntity
import com.chatgpt.letaithink.model.remoteModelts.CompletionRequest
import com.chatgpt.letaithink.utils.ImageUtils
import com.chatgpt.letaithink.utils.toConversationItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import com.chatgpt.letaithink.utils.emit
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

// TODO Handle nonull calls
// TODO don't allow mutable questions at same time
class ChatViewModel : ViewModel() {

    private val _conversationItems = MutableStateFlow<List<ConversationItem>>(emptyList())
    val conversationItems = _conversationItems.asStateFlow()

    private val _progressLoading = MutableStateFlow(false)
    val progressLoading = _progressLoading.asStateFlow()

    private val _exceptionFlow = MutableSharedFlow<Throwable>()
    val exceptionFlow = _exceptionFlow.asSharedFlow()

    private val _requestInProgress = MutableStateFlow(false)
    val requestInProgress = _requestInProgress.asStateFlow()

    private val _chatModes = MutableStateFlow<List<ChatMode>>(emptyList()).apply {
        val modes = ChatRepository.chatModes.mapIndexed { index, chatMode ->
            if (index == 0) chatMode.copy(selected = true) else chatMode
        }
        emit(modes, viewModelScope)
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        _requestInProgress.emit(false, viewModelScope)
        _exceptionFlow.emit(exception, viewModelScope)
    }

    val chatModes = _chatModes.asStateFlow()

    var session: SessionEntity? = null

    fun setChatSession(sessionId: String?) {
        viewModelScope.launch(exceptionHandler) {
            sessionId?.let {
                session = ChatRepository.getChatSession(sessionId)
                val conversation =
                    ChatRepository.getSessionConversation(sessionId).messages.map { it.toConversationItem() }
                _conversationItems.emit(conversation)
            }
        }
    }

    fun onSendClick(text: String) {
        viewModelScope.launch(exceptionHandler) {
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

        viewModelScope.launch(exceptionHandler) {
            _requestInProgress.emit(value = true)

            val question = UserMessage(text)
            updateConversation(question)
            updateConversation(AiThinking("Wait a second )"))

            val completion = CompletionRequest(
                UserRepository.getUser()?.userId!!,
                mode.model,
                generateTextPrompts(text),
                100,
                0.3F
            )
            val result = ChatRepository.askQuestion(completion)
            val answer = AiMessage(result)

            replaceLastConversationItem(answer)
            ChatRepository.saveConversationItem(session!!, question, answer)

            _requestInProgress.emit(value = false)
        }
    }

    fun onDownloadClick(context: Context, bitmap: Bitmap) {
        viewModelScope.launch(exceptionHandler) {
            _progressLoading.emit(true)
            withContext(Dispatchers.IO) {
                try {
                    ImageUtils.saveImageToGallery(context, bitmap)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, R.string.image_saved, Toast.LENGTH_LONG).show()
                    }
                } catch (e: IOException) {
                    Toast.makeText(context, R.string.image_save_fail, Toast.LENGTH_LONG).show()
                }
            }
            _progressLoading.emit(value = false)
        }
    }

    private fun onImageGenerate(description: String) {
        session ?: return

        viewModelScope.launch(exceptionHandler) {
            _requestInProgress.emit(value = true)

            val question = UserMessage(description)
            updateConversation(question)
            updateConversation(AiThinking("Wait a second )"))

            val imageDescription = ImageGenerationRequest(
                UserRepository.getUser()?.userId!!,
                description,
                IMAGE_SIZE_1024,
                1
            )
            val result = ChatRepository.generateImage(imageDescription)
            val answer = AiImage(result)

            replaceLastConversationItem(answer)
            ChatRepository.saveConversationItem(session!!, question, answer)

            _requestInProgress.emit(value = false)
        }
    }

    private suspend fun updateConversation(conversationItem: ConversationItem) {
        val newConversation = _conversationItems.value + conversationItem
        _conversationItems.emit(newConversation)
    }

    private fun replaceLastConversationItem(conversationItem: ConversationItem) {
        val newConversation = _conversationItems.value.dropLast(1) + conversationItem
        _conversationItems.emit(newConversation, viewModelScope)
    }

    private fun getSelectedChatMode() = chatModes.value.first { it.selected }

    // TODO uses only first choice
    private fun generateTextPrompts(prompt: String): String {

        fun StringBuilder.newLine(): StringBuilder {
            append("\n")
            return this
        }

        fun StringBuilder.appendMessage(userMessage: String?): StringBuilder {
            append(userMessage)
            newLine()
            return this
        }

        val result = StringBuilder()
        conversationItems.value.forEach {
            when (it) {
                is UserMessage -> result.appendMessage(it.message)
                is AiMessage -> result.appendMessage(it.textCompletion.choices?.first()?.text)
                is AiImage -> result.append(it.image.data)
                else -> {}
            }
        }

        result.appendMessage(prompt)

        return result.toString()
    }
}