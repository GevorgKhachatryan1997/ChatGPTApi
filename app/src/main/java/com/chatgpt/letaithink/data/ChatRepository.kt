package com.chatgpt.letaithink.data

import com.chatgpt.letaithink.model.ChatMode
import com.chatgpt.letaithink.model.databaseModels.Conversation
import com.chatgpt.letaithink.model.databaseModels.SessionEntity
import com.chatgpt.letaithink.utils.toMessageEntity
import com.openai.api.OpenAIDataSource
import com.openai.api.exception.ApiError
import com.openai.api.exception.NoConnectionException
import com.openai.api.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.*

object ChatRepository {

    private val openAIDataSource = OpenAIDataSource()
    private val localDataSource = LocalDataSource()

    val chatModes: List<ChatMode> = localDataSource.chatModes

    @Throws(NoConnectionException::class, ApiError::class)
    suspend fun askQuestion(completion: CompletionRequest): TextCompletion = withContext(Dispatchers.IO) {
        ensureUserState()

        openAIDataSource.getCompletion(completion)
    }

    @Throws(NoConnectionException::class, ApiError::class)
    suspend fun askChatQuestion(chatCompletion: ChatCompletionRequest): ChatCompletion = withContext(Dispatchers.IO) {
        ensureUserState()

        openAIDataSource.getChatCompletion(chatCompletion)
    }

    @Throws(NoConnectionException::class, ApiError::class)
    suspend fun generateImage(imageParams: ImageGenerationRequest): ImageModel = withContext(Dispatchers.IO) {
        ensureUserState()

        openAIDataSource.generateImage(imageParams)
    }

    suspend fun createSession(name: String): SessionEntity {
        val session = SessionEntity(UUID.randomUUID().toString(), name, UserRepository.getUser()?.userId!!)
        localDataSource.insertSession(session)
        return session
    }

    suspend fun saveConversationItem(session: SessionEntity, questionItem: ConversationItem, answerItem: ConversationItem) {
        val question = questionItem.toMessageEntity(session.sessionId)
        val answer = answerItem.toMessageEntity(session.sessionId)
        localDataSource.insertConversationItem(question, answer)
    }

    fun getChatSessions(): Flow<List<SessionEntity>> = localDataSource.getAllSessions()

    suspend fun getChatSession(id: String): SessionEntity? = localDataSource.getChatSession(id)

    suspend fun getSessionConversation(sessionId: String): Conversation = localDataSource.getSessionConversation(sessionId)

    suspend fun deleteSession(session: SessionEntity) {
        localDataSource.deleteChatSession(session)
    }

    suspend fun updateSessionName(session: SessionEntity, name: String) {
        localDataSource.updateSessionName(session, name)
    }

    suspend fun deleteAllData() {
        localDataSource.deleteChatData()
    }

    private suspend fun ensureUserState() {
        if (ApiKeyRepository.getApiKey() == null) {
            // PurchaseRepository.ensurePurchase()
        }
    }
}