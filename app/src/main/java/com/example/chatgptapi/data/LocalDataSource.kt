package com.example.chatgptapi.data

import com.example.chatgptapi.R
import com.example.chatgptapi.model.databaseModels.MessageEntity
import com.example.chatgptapi.model.databaseModels.SessionEntity
import com.example.chatgptapi.model.databaseModels.UserEntity
import com.example.chatgptapi.ui.model.UiAiModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class LocalDataSource {

    companion object {
        const val DAVINCI_ID = "text-davinci-003"
        const val CURIE_ID = "text-curie-001"
        const val BABBAGE_ID = "text-babbage-001"
        const val ADA_ID = "text-ada-001"
        const val IMAGE_GENERATION = "image_generation"
    }

    private val appDb = AppDatabase.getInstance()

    val uiAiModelList = buildList {
        add(UiAiModel(DAVINCI_ID, R.string.davinci, R.string.davinci_info, R.mipmap.ic_launcher))
        add(UiAiModel(CURIE_ID, R.string.curie, R.string.curie_info, R.mipmap.ic_launcher))
        add(UiAiModel(BABBAGE_ID, R.string.babbage, R.string.babbage_info, R.mipmap.ic_launcher))
        add(UiAiModel(ADA_ID, R.string.ada, R.string.ada_info, R.mipmap.ic_launcher))
        add(UiAiModel(IMAGE_GENERATION, R.string.image_generation, R.string.image_generation_info, R.mipmap.ic_launcher))
    }

    fun findUiAIModel(modelId: String): UiAiModel? = uiAiModelList.find { it.id == modelId }

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
}