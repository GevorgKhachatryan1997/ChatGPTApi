package com.chatgpt.letaithink.ui.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chatgpt.letaithink.data.ApiKeyRepository
import com.chatgpt.letaithink.data.ChatRepository
import com.chatgpt.letaithink.data.UserRepository
import com.chatgpt.letaithink.domain.GoogleAuthenticationHelper
import kotlinx.coroutines.launch

class SettingViewModel : ViewModel() {

    private val googleAuthenticationHelper = GoogleAuthenticationHelper()

    fun onLogoutClick(context: Context) {
        googleAuthenticationHelper.signOutRequest(context)
        viewModelScope.launch {
            UserRepository.deleteUserData()
            ApiKeyRepository.deleteApiKey()
            ChatRepository.deleteAllData()
        }
    }
}