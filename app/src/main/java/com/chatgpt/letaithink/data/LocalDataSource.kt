package com.chatgpt.letaithink.data

import com.chatgpt.letaithink.R
import com.chatgpt.letaithink.model.*
import com.chatgpt.letaithink.model.databaseModels.Conversation
import com.chatgpt.letaithink.model.databaseModels.MessageEntity
import com.chatgpt.letaithink.model.databaseModels.SessionEntity
import com.chatgpt.letaithink.model.databaseModels.UserEntity
import com.chatgpt.letaithink.model.databaseModels.ApiKeyEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class LocalDataSource {

    companion object {
        const val TEXT_DAVINCI_MODEL = "text-davinci-003"
        const val CODE_DAVINCI_MODEL = "code-davinci-002"
    }

    private val appDb = AppDatabase.getInstance()

    val chatModes = listOf(
        ChatMode(CHAT_MODE_TEXT_COMPLETION, R.string.text, R.drawable.icon_chat, TEXT_DAVINCI_MODEL),
        ChatMode(CHAT_MODE_CODE_COMPLETION, R.string.code, R.drawable.icon_code, CODE_DAVINCI_MODEL),
        ChatMode(CHAT_MODE_IMAGE_GENERATION, R.string.image, R.drawable.icon_image, "image generation")
    )

    suspend fun insertSession(session: SessionEntity) = withContext(Dispatchers.IO) {
        appDb.conversationDao().insertSession(session)
    }

    suspend fun insertConversationItem(question: MessageEntity, answer: MessageEntity) = withContext(Dispatchers.IO) {
        with(appDb.conversationDao()) {
            insertMessage(question)
            insertMessage(answer)
        }
    }

    fun getAllSessions(): Flow<List<SessionEntity>> = appDb.conversationDao().getAllSessions()

    suspend fun getChatSession(id: String): SessionEntity? = withContext(Dispatchers.IO) {
        appDb.conversationDao().getSession(id)
    }

    suspend fun getSessionConversation(sessionId: String): Conversation = withContext(Dispatchers.IO) {
        appDb.conversationDao().getConversationBySessionId(sessionId)
    }

    suspend fun deleteChatSession(session: SessionEntity) = withContext(Dispatchers.IO) {
        appDb.conversationDao().deleteSession(session)
    }

    suspend fun updateSessionName(session: SessionEntity, name: String) = withContext(Dispatchers.IO) {
        appDb.conversationDao().updateSession(session.copy(sessionName = name))
    }

    suspend fun isUserAuthenticated(): Boolean = withContext(Dispatchers.IO) {
        return@withContext appDb.userDao().isExists()
    }

    suspend fun insertUser(userEntity: UserEntity) {
        withContext(Dispatchers.IO) {
            appDb.userDao().insertUser(userEntity)
        }
    }

    suspend fun deleteUserData() {
        withContext(Dispatchers.IO) {
            appDb.userDao().deleteUserData()
        }
    }

    suspend fun getUser(): UserEntity? {
        return appDb.userDao().getUser()
    }

    suspend fun insertApiKey(apiKey: String) {
        val apiKeyEntity = ApiKeyEntity(apiKey)
        appDb.apiKeyDao().insertApiKey(apiKeyEntity)
    }

    suspend fun getApiKey(): ApiKeyEntity? = withContext(Dispatchers.IO) {
        appDb.apiKeyDao().getApiKey()
    }

    suspend fun deleteApiKey() {
        withContext(Dispatchers.IO) {
            appDb.apiKeyDao().deleteApiKey()
        }
    }

    suspend fun deleteChatData() {
        withContext(Dispatchers.IO) {
            appDb.conversationDao().deleteAllSessions()
        }
    }
}