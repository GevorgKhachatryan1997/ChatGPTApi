package com.example.chatgptapi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatgptapi.ui.model.UiAiModel
import com.example.chatgptapi.utils.emit
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class MainViewModel: ViewModel() {

    var currentScreen: Screen? = null

    private val _screenNavigationFlow = MutableSharedFlow<Screen>()
    val screenNavigationFlow = _screenNavigationFlow.asSharedFlow()

    fun onActivityStart() {
        if (currentScreen == null) {
            showScreen(AuthenticationScreen)
        }
    }

    fun showScreen(screen: Screen) {
        currentScreen = screen
        _screenNavigationFlow.emit(screen, viewModelScope)
    }

    sealed class Screen

    object AiModelSelection: Screen()
    object AuthenticationScreen: Screen()
    class AiChatScreen(val model: UiAiModel): Screen()
}