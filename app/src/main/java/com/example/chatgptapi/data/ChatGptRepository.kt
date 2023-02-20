package com.example.chatgptapi.data

import com.example.chatgptapi.model.*
import com.example.chatgptapi.model.databaseModels.MessageEntity
import com.example.chatgptapi.model.databaseModels.MessageType
import com.example.chatgptapi.model.databaseModels.SessionEntity
import com.example.chatgptapi.model.remoteModelts.AiModels
import com.example.chatgptapi.model.remoteModelts.CompletionRequest
import com.example.chatgptapi.ui.model.UiAiModel
import com.example.chatgptapi.utils.JsonUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

object ChatGptRepository {

    private val remoteDataSource = RemoteDataSource()
    private val localDataSource = LocalDataSource()

    fun getRemoteModels(): AiModels? {
        return remoteDataSource.getModels()
    }

    fun askQuestion(completion: CompletionRequest): TextCompletion {
        return remoteDataSource.getCompletion(completion)!! // TODO unsafe call
    }

    fun generateImage(imageParams: ImageGenerationRequest) = remoteDataSource.generateImage(imageParams)

    fun getUiAiModels(): List<UiAiModel> {
        return localDataSource.uiAiModelList
    }

    fun findUiAiModel(modelId: String): UiAiModel? = localDataSource.findUiAIModel(modelId)

    suspend fun createSession(name: String): SessionEntity = withContext(Dispatchers.IO) {
        val session = SessionEntity(UUID.randomUUID().toString(), name, UserInfo.userId)
        localDataSource.insertSession(session)
        return@withContext session
    }

    suspend fun saveConversationItem(session: SessionEntity, questionItem: ConversationItem, answerItem: ConversationItem) =
        withContext(Dispatchers.IO) {
            val question = questionItem.toMessageEntity(session.sessionId)
            val answer = answerItem.toMessageEntity(session.sessionId)
            localDataSource.insertConversationItem(question, answer)
        }

    @Throws(IllegalStateException::class)
    private fun ConversationItem.toMessageEntity(sessionId: String): MessageEntity {
        val currentTime = Calendar.getInstance().timeInMillis
        return when (this) {
            is UserMessage -> MessageEntity(sessionId, MessageType.USER_INPUT, message, currentTime)
            is AiMessage -> MessageEntity(sessionId, MessageType.AI_COMPLETION, JsonUtil.toJson(textCompletion), currentTime)
            is AiImage -> MessageEntity(sessionId, MessageType.AI_IMAGE_GENERATION, JsonUtil.toJson(image), currentTime)
            else -> throw IllegalStateException("Unknown conversation item: ${javaClass.simpleName}")
        }
    }
}