package com.chatgpt.letaithink.data

import com.chatgpt.letaithink.R
import com.chatgpt.letaithink.manager.EncryptionManager
import com.chatgpt.letaithink.model.*
import com.chatgpt.letaithink.model.databaseModels.*
import com.chatgpt.letaithink.model.Purchase
import com.openai.api.utils.JsonUtil
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
        //ChatMode(CHAT_MODE_TEXT_COMPLETION, R.string.text, R.drawable.icon_chat, TEXT_DAVINCI_MODEL, 0.3F, 500),
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

    suspend fun getPurchase(): Purchase? = withContext(Dispatchers.IO) {
        val purchaseEntity = appDb.purchaseDao().getPurchase() ?: return@withContext null
        val decryptedJson = EncryptionManager.decrypt(purchaseEntity.purchaseJson)
        return@withContext JsonUtil.fromJson(decryptedJson, Purchase::class.java)
    }

    suspend fun deletePuchase() {
        withContext(Dispatchers.IO) {
            appDb.purchaseDao().deletePurchase()
        }
    }

    suspend fun insertPurchase(purchaseJson: String) {
        val encryptedJson = EncryptionManager.encrypt(purchaseJson)
        val purchaseEntity = PurchaseEntity(encryptedJson)
        withContext(Dispatchers.IO) {
            appDb.purchaseDao().insert(purchaseEntity)
        }
    }
}