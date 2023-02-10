package com.example.chatgptapi.ui

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatgptapi.data.UserRepository
import com.example.chatgptapi.domain.GoogleAuthenticationHelper
import com.example.chatgptapi.domain.Listener
import com.example.chatgptapi.model.databaseModels.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _authenticationMutableSharedFlow = MutableSharedFlow<Authentication>()
    val authenticationSharedFlow = _authenticationMutableSharedFlow.asSharedFlow()

    private val googleAuthenticationHelper = GoogleAuthenticationHelper().apply {
        onAuthenticationListener = object : Listener {
            override fun onLoginSuccess(user: UserEntity) {
                viewModelScope.launch {
                    _authenticationMutableSharedFlow.emit(AuthenticationSuccess)
                    insertUser(user)
                }
            }

            override fun onLoginFailure() {
                viewModelScope.launch {
                    _authenticationMutableSharedFlow.emit(AuthenticationFailed)
                }
            }
        }
    }

    fun insertUser(userEntity: UserEntity) {
        viewModelScope.launch {
            UserRepository.insertUser(userEntity)
        }
    }

    fun onAuthenticationResult(data: Intent?) {
        googleAuthenticationHelper.onAuthenticationResult(data)
    }

    fun signInRequest(activity: Activity, googleSignInRequest: (Intent) -> Unit) {
        googleAuthenticationHelper.signInRequest(activity, googleSignInRequest)
    }

    sealed class Authentication

    object AuthenticationSuccess : Authentication()
    object AuthenticationFailed : Authentication()
}