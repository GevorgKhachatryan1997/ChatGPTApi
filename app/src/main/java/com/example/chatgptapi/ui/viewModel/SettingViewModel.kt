package com.example.chatgptapi.ui.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatgptapi.data.ApiKeyRepository
import com.example.chatgptapi.data.UserRepository
import com.example.chatgptapi.domain.GoogleAuthenticationHelper
import kotlinx.coroutines.launch

class SettingViewModel : ViewModel() {

    private val googleAuthenticationHelper = GoogleAuthenticationHelper()

    fun onLogoutClick(context: Context) {
        googleAuthenticationHelper.signOutRequest(context)
        viewModelScope.launch {
            UserRepository.deleteUserData()
            ApiKeyRepository.deleteApiKey()
        }
    }
}