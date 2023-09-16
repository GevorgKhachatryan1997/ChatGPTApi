package com.chatgpt.letaithink.domain

import com.chatgpt.letaithink.data.ApiKeyRepository
import com.chatgpt.letaithink.data.ChatRepository
import com.chatgpt.letaithink.data.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

object ApplicationManager {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun deleteAllData() {
        scope.launch {
            UserRepository.deleteUserData()
            ApiKeyRepository.deleteApiKey()
            ChatRepository.deleteAllData()
        }
    }
}