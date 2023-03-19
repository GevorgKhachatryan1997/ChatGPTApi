package com.chatgpt.letaithink

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chatgpt.letaithink.data.ApiKeyRepository
import com.chatgpt.letaithink.data.UserRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import com.chatgpt.letaithink.utils.emit

class MainViewModel : ViewModel() {

    var currentScreen: Screen? = null

    private val _screenNavigationFlow = MutableSharedFlow<Screen>()
    val screenNavigationFlow = _screenNavigationFlow.asSharedFlow()

    private val _exceptionMutableSharedFlow = MutableSharedFlow<Throwable>()
    val exceptionSharedFlow = _exceptionMutableSharedFlow.asSharedFlow()

    fun onActivityStart() {
        if (currentScreen == null) {
            viewModelScope.launch {
                if (!UserRepository.isUserAuthenticated()) {
                    showScreen(LoginScreen)
                } else if (!ApiKeyRepository.hasApiKey()) {
                    showScreen(ApiKeyScreen)
                } else {
                    showScreen(ChatsHistory)
                }
            }
        }
    }

    fun showScreen(screen: Screen) {
        currentScreen = screen
        _screenNavigationFlow.emit(screen, viewModelScope)
    }

    fun handleException(exception: Throwable) {
        _exceptionMutableSharedFlow.emit(exception, viewModelScope)
    }

    sealed class Screen

    object LoginScreen : Screen()
    object SettingScreen : Screen()
    object ChatsHistory : Screen()
    object ApiKeyScreen : Screen()
    class AiChatScreen(val sessionId: String? = null) : Screen()
}