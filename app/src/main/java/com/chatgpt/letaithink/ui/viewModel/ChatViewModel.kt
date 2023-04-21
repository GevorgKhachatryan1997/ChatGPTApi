package com.chatgpt.letaithink.ui.viewModel

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chatgpt.letaithink.R
import com.chatgpt.letaithink.data.AiChatMessage
import com.chatgpt.letaithink.data.AiImage
import com.chatgpt.letaithink.data.AiMessage
import com.chatgpt.letaithink.data.AiThinking
import com.chatgpt.letaithink.data.ChatRepository
import com.chatgpt.letaithink.data.ConversationItem
import com.chatgpt.letaithink.data.ResourcesRepository
import com.chatgpt.letaithink.data.UserMessage
import com.chatgpt.letaithink.data.UserRepository
import com.chatgpt.letaithink.model.CHAT_MODE_CHAT_COMPLETION
import com.chatgpt.letaithink.model.CHAT_MODE_IMAGE_GENERATION
import com.chatgpt.letaithink.model.CHAT_MODE_TEXT_COMPLETION
import com.chatgpt.letaithink.model.ChatMode
import com.chatgpt.letaithink.model.databaseModels.SessionEntity
import com.chatgpt.letaithink.model.remoteModels.ChatCompletionRequest
import com.chatgpt.letaithink.model.remoteModels.CompletionRequest
import com.chatgpt.letaithink.model.remoteModels.IMAGE_SIZE_1024
import com.chatgpt.letaithink.model.remoteModels.ImageGenerationRequest
import com.chatgpt.letaithink.utils.ImageUtils
import com.chatgpt.letaithink.utils.emit
import com.chatgpt.letaithink.utils.toConversationItem
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

// TODO Handle nonull calls
class ChatViewModel : ViewModel() {

    companion object {
        private const val USER = "user"
        private const val ASSISTANT = "assistant"
    }

    private val _conversationItems = MutableStateFlow<List<ConversationItem>>(emptyList())
    val conversationItems = _conversationItems.asStateFlow()

    private val _progressLoading = MutableStateFlow(false)
    val progressLoading = _progressLoading.asStateFlow()

    private val _exceptionFlow = MutableSharedFlow<Throwable>()
    val exceptionFlow = _exceptionFlow.asSharedFlow()

    private val _chatModes = MutableStateFlow<List<ChatMode>>(emptyList()).apply {
        val modes = ChatRepository.chatModes.mapIndexed { index, chatMode ->
            if (index == 0) chatMode.copy(selected = true) else chatMode
        }
        emit(modes, viewModelScope)
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        if (conversationItems.value.lastOrNull() is AiThinking) {
            removeLastItem()
        }
        _progressLoading.emit(false, viewModelScope)
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
        if (progressLoading.value) return

        viewModelScope.launch(exceptionHandler) {
            if (session == null) {
                session = ChatRepository.createSession(text)
            }
            val selectedMode = getSelectedChatMode()
            when (selectedMode.mode) {
                CHAT_MODE_TEXT_COMPLETION -> {
                    onTextGeneration(text, selectedMode)
                }

                CHAT_MODE_CHAT_COMPLETION -> {
                    onChatMessage(text, selectedMode)
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
            _progressLoading.emit(value = true)

            val question = UserMessage(text)
            updateConversation(question)
            updateConversation(AiThinking(ResourcesRepository.getRandomUserWaitMessage()))

            val completion = CompletionRequest(
                UserRepository.getUser()?.userId!!,
                mode.model,
                generateTextPrompts(text),
                mode.maxTokens,
                mode.temperature
            )

            val result = ChatRepository.askQuestion(completion)
            val answer = AiMessage(result)

            replaceLastConversationItem(answer)
            ChatRepository.saveConversationItem(session!!, question, answer)

            _progressLoading.emit(value = false)
        }
    }

    private fun onChatMessage(text: String, mode: ChatMode) {
        session ?: return

        viewModelScope.launch(exceptionHandler) {
            _progressLoading.emit(value = true)

            val question = UserMessage(text)
            updateConversation(question)
            updateConversation(AiThinking(ResourcesRepository.getRandomUserWaitMessage()))

            val chatCompletion = ChatCompletionRequest(
                mode.model,
                generateChatPrompts(),
                mode.temperature,
                mode.maxTokens,
                UserRepository.getUser()?.userId!!
            )

            val result = ChatRepository.askChatQuestion(chatCompletion)
            val answer = AiChatMessage(result)

            replaceLastConversationItem(answer)
            ChatRepository.saveConversationItem(session!!, question, answer)

            _progressLoading.emit(value = false)
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
            _progressLoading.emit(value = true)

            val question = UserMessage(description)
            updateConversation(question)
            updateConversation(AiThinking(ResourcesRepository.getRandomUserWaitMessage()))

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

            _progressLoading.emit(value = false)
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

    private fun removeLastItem() {
        with(_conversationItems) {
            if (value.isNotEmpty()) {
                val newConversation = value.dropLast(1)
                _conversationItems.emit(newConversation, viewModelScope)
            }
        }
    }

    private fun getSelectedChatMode() = chatModes.value.first { it.selected }

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
                is AiChatMessage -> result.appendMessage(it.chatCompletion.choices?.first()?.message?.content)
                is AiImage -> result.append(it.image.data)
                else -> {}
            }
        }

        result.appendMessage(prompt)

        return result.toString()
    }

    private fun generateChatPrompts(): List<ChatCompletionRequest.ChatMessage> {
        return buildList {
            conversationItems.value.forEach {
                val model: ChatCompletionRequest.ChatMessage? = when (it) {
                    is UserMessage -> ChatCompletionRequest.ChatMessage(USER, it.message)
                    is AiMessage -> ChatCompletionRequest.ChatMessage(ASSISTANT, it.textCompletion.choices?.first()?.text)
                    is AiChatMessage -> ChatCompletionRequest.ChatMessage(ASSISTANT, it.chatCompletion.choices?.first()?.message?.content)
                    is AiImage -> ChatCompletionRequest.ChatMessage(ASSISTANT, it.image.data.toString())
                    is AiThinking -> null
                }
                if (model != null) {
                    add(model)
                }
            }
        }
    }
}