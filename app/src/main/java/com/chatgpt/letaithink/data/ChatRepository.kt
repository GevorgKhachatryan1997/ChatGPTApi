package com.chatgpt.letaithink.data

import com.chatgpt.letaithink.exception.ApiError
import com.chatgpt.letaithink.exception.NoConnectionException
import com.chatgpt.letaithink.model.ChatMode
import com.chatgpt.letaithink.model.remoteModelts.ImageGenerationRequest
import com.chatgpt.letaithink.model.TextCompletion
import com.chatgpt.letaithink.model.databaseModels.Conversation
import com.chatgpt.letaithink.model.databaseModels.SessionEntity
import com.chatgpt.letaithink.model.remoteModelts.CompletionRequest
import com.chatgpt.letaithink.model.remoteModelts.ImageModel
import com.chatgpt.letaithink.utils.toMessageEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.*

// TODO add suspend keyword for methods which should run on IO thread
object ChatRepository {

    private val remoteDataSource = RemoteDataSource()
    private val localDataSource = LocalDataSource()

    val chatModes: List<ChatMode> = localDataSource.chatModes

    @Throws(NoConnectionException::class, ApiError::class)
    suspend fun askQuestion(completion: CompletionRequest): TextCompletion = withContext(Dispatchers.IO) {
        remoteDataSource.getCompletion(completion)
    }

    @Throws(NoConnectionException::class, ApiError::class)
    suspend fun generateImage(imageParams: ImageGenerationRequest): ImageModel = withContext(Dispatchers.IO) {
        remoteDataSource.generateImage(imageParams)
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
}