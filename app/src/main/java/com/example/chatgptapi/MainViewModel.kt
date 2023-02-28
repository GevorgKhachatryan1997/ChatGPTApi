package com.example.chatgptapi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatgptapi.data.UserRepository
import com.example.chatgptapi.ui.model.UiAiModel
import com.example.chatgptapi.utils.emit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    var currentScreen: Screen? = null

    private val _screenNavigationFlow = MutableSharedFlow<Screen>()
    val screenNavigationFlow = _screenNavigationFlow.asSharedFlow()

    fun onActivityStart() {
        if (currentScreen == null) {
            viewModelScope.launch(Dispatchers.IO) {
                if (UserRepository.isUserAuthentication()) {
                    showScreen(AiModelSelection)
                } else {
                    showScreen(LoginScreen)
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
    object SignInScreen : Screen()
    object AiModelSelection : Screen()
    object SettingScreen : Screen()

    class AiChatScreen(val model: UiAiModel) : Screen()
}