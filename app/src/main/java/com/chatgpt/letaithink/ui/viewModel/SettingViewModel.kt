package com.chatgpt.letaithink.ui.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.chatgpt.letaithink.domain.ApplicationManager
import com.chatgpt.letaithink.domain.GoogleAuthenticationHelper

class SettingViewModel : ViewModel() {

    private val googleAuthenticationHelper = GoogleAuthenticationHelper()

    fun onLogoutClick(context: Context) {
        googleAuthenticationHelper.signOutRequest(context)
        ApplicationManager.deleteAllData()
    }
}