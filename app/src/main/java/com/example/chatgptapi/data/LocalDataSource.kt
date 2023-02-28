package com.example.chatgptapi.data

import com.example.chatgptapi.R
import com.example.chatgptapi.model.databaseModels.MessageEntity
import com.example.chatgptapi.model.databaseModels.SessionEntity
import com.example.chatgptapi.model.databaseModels.UserEntity
import com.example.chatgptapi.ui.model.UiAiModel

class LocalDataSource {

    companion object {
        const val DAVINSI_ID = "text-davinci-003"
        const val CURIE_ID = "text-curie-001"
        const val BABBAGE_ID = "text-babbage-001"
        const val ADA_ID = "text-ada-001"
        const val IMAGE_GENERATION = "image_generation"
    }

    private val conversationDb = ConversationDatabase.getInstance()
    private val userDb = UserDatabase.getInstance()

    val uiAiModelList = buildList {
        add(UiAiModel(DAVINSI_ID, R.string.davinci, R.string.davinci_info, R.mipmap.ic_launcher))
        add(UiAiModel(CURIE_ID, R.string.curie, R.string.curie_info, R.mipmap.ic_launcher))
        add(UiAiModel(BABBAGE_ID, R.string.babbage, R.string.babbage_info, R.mipmap.ic_launcher))
        add(UiAiModel(ADA_ID, R.string.ada, R.string.ada_info, R.mipmap.ic_launcher))
        add(
            UiAiModel(
                IMAGE_GENERATION,
                R.string.image_generation,
                R.string.image_generation_info,
                R.mipmap.ic_launcher
            )
        )
    }

    fun findUiAIModel(modelId: String): UiAiModel? = uiAiModelList.find { it.id == modelId }

    fun insertSession(session: SessionEntity) {
        conversationDb.conversationDao().insertSession(session)
    }

    fun insertConversationItem(question: MessageEntity, answer: MessageEntity) {
        with(conversationDb.conversationDao()) {
            insertMessage(question)
            insertMessage(answer)
        }
    }

    fun isUserAuthentication(): Boolean {
        return userDb.userDao().isExists()
    }

    fun insertUser(userEntity: UserEntity) {
        userDb.userDao().insertUser(userEntity)
    }

    fun deleteUserData() {
        userDb.userDao().deleteUserData()
    }

    fun getUser(): UserEntity? {
        return userDb.userDao().getUser()
    }
}