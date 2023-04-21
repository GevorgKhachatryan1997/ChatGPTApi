package com.chatgpt.letaithink

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chatgpt.letaithink.data.ApiKeyRepository
import com.chatgpt.letaithink.data.PurchaseRepository
import com.chatgpt.letaithink.data.UserRepository
import com.chatgpt.letaithink.utils.emit
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _screenNavigationFlow = MutableSharedFlow<Screen>()
    val screenNavigationFlow = _screenNavigationFlow.asSharedFlow()

    private val _exceptionMutableSharedFlow = MutableSharedFlow<Throwable>()
    val exceptionSharedFlow = _exceptionMutableSharedFlow.asSharedFlow()

    fun onActivityStart(firstRun: Boolean) {
        if (firstRun) {
            viewModelScope.launch {
                if (!UserRepository.isUserAuthenticated()) {
                    showScreen(ChatsHistory)
                } else if (!ApiKeyRepository.hasApiKey()) {
                    showScreen(ApiKeyScreen)
                } else if (PurchaseRepository.purchaseInvalid()) {
                    showScreen(PaymentScreen)
                } else {
                    showScreen(ChatsHistory)
                }
            }
        }
    }

    fun showScreen(screen: Screen) {
        _screenNavigationFlow.emit(screen, viewModelScope)
    }

    fun handleException(exception: Throwable) {
        _exceptionMutableSharedFlow.emit(exception, viewModelScope)
    }

    sealed class Screen

    object LoginScreen : Screen()
    object ApiKeyScreen : Screen()
    object PaymentScreen : Screen()
    object ChatsHistory : Screen()
    class AiChatScreen(val sessionId: String? = null) : Screen()
    object SettingScreen : Screen()
}