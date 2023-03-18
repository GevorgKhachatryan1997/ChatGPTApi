package com.example.chatgptapi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatgptapi.data.ApiKeyRepository
import com.example.chatgptapi.data.UserRepository
import com.example.chatgptapi.utils.emit
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    var currentScreen: Screen? = null

    private val _screenNavigationFlow = MutableSharedFlow<Screen>()
    val screenNavigationFlow = _screenNavigationFlow.asSharedFlow()

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

    sealed class Screen

    object LoginScreen : Screen()
    object SettingScreen : Screen()
    object ChatsHistory : Screen()
    object ApiKeyScreen : Screen()
    class AiChatScreen(val sessionId: String? = null) : Screen()
}