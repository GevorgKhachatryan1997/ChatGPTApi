package com.chatgpt.letaithink.data

import com.chatgpt.letaithink.R
import com.chatgpt.letaithink.model.*
import com.chatgpt.letaithink.model.databaseModels.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class LocalDataSource {

    companion object {
        // TODO use latest version
        const val TEXT_DAVINCI_MODEL = "text-davinci-003"
        const val GPT_TURBO_MODEL = "gpt-3.5-turbo"
    }

    private val appDb = AppDatabase.getInstance()

    val chatModes = listOf(
        // ChatMode(CHAT_MODE_TEXT_COMPLETION, R.string.text, R.drawable.icon_chat, TEXT_DAVINCI_MODEL, 0.3F, 500),
        ChatMode(CHAT_MODE_CHAT_COMPLETION, R.string.text, R.drawable.icon_chat, GPT_TURBO_MODEL, 0.3F, 100),
        ChatMode(CHAT_MODE_IMAGE_GENERATION, R.string.image, R.drawable.icon_image, "image generation", 0F),
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

    suspend fun insertApiKey(apiKey: String) {
        withContext(Dispatchers.IO) {
            val apiKeyEntity = ApiKeyEntity(apiKey)
            appDb.apiKeyDao().insertApiKey(apiKeyEntity)
        }
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