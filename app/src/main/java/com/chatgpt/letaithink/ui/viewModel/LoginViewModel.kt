package com.chatgpt.letaithink.ui.viewModel

import android.app.Activity
import android.content.Intent
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chatgpt.letaithink.data.UserRepository
import com.chatgpt.letaithink.domain.GoogleAuthenticationHelper
import com.chatgpt.letaithink.domain.Listener
import com.chatgpt.letaithink.model.databaseModels.UserEntity
import com.chatgpt.letaithink.utils.generateUserId
import com.google.android.gms.auth.api.identity.SignInCredential
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _authenticationMutableSharedFlow = MutableSharedFlow<Boolean>()
    val authenticationSharedFlow = _authenticationMutableSharedFlow.asSharedFlow()

    private val googleAuthenticationHelper = GoogleAuthenticationHelper().apply {
        onAuthenticationListener = object : Listener {
            override fun onLoginSuccess(credential: SignInCredential) {
                viewModelScope.launch {
                    val user = UserEntity(generateUserId(), credential.displayName, credential.familyName, credential.id)
                    UserRepository.insertUser(user)
                    _authenticationMutableSharedFlow.emit(true)
                }
            }

            override fun onLoginFailure() {
                viewModelScope.launch {
                    _authenticationMutableSharedFlow.emit(false)
                }
            }
        }
    }

    fun onAuthenticationResult(activity: Activity, data: Intent?) {
        googleAuthenticationHelper.onAuthenticationResult(activity, data)
    }

    fun signInRequest(activity: Activity, googleSignInRequest: (IntentSenderRequest) -> Unit) {
        googleAuthenticationHelper.signInRequest(activity, googleSignInRequest)
    }
}