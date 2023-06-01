package com.chatgpt.letaithink

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chatgpt.letaithink.data.ApiKeyRepository
import com.chatgpt.letaithink.data.UserRepository
import com.chatgpt.letaithink.utils.emit
import com.openai.api.OpenAIManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _screenNavigationFlow = MutableSharedFlow<Screen>()
    val screenNavigationFlow = _screenNavigationFlow.asSharedFlow()

    private val _exceptionMutableSharedFlow = MutableSharedFlow<Throwable>()
    val exceptionSharedFlow = _exceptionMutableSharedFlow.asSharedFlow()

    init {
        viewModelScope.launch {
            val apiKey = ApiKeyRepository.getApiKey() ?: BuildConfig.OPENAI_API_KEY
            OpenAIManager.setAPIKey(apiKey)
        }
    }

    fun onActivityStart(firstRun: Boolean) {
        if (firstRun) {
            viewModelScope.launch {
                showCorrespondingScreen()
            }
        }
    }

    fun onAuthorizationSuccess() {
        showScreen(ChatsHistory)
    }

    fun onApiKeySet() {
        showScreen(ChatsHistory)
    }

    fun onSessionClick(sessionId: String? = null) {
        showScreen(AiChatScreen(sessionId))
    }

    fun onSettingClick() {
        showScreen(SettingScreen)
    }

    fun onPaymentSuccess() {
        showScreen(ChatsHistory)
    }

    fun onUpdateApiKey() {
        showScreen(ApiKeyScreen)
    }

    fun onLogout() {
        showScreen(LoginScreen)
    }

    fun showTextEditFragment(){
        showScreen(TextEditScreen)
    }

    private suspend fun showCorrespondingScreen() {
        if (!UserRepository.isUserAuthenticated()) {
            showScreen(LoginScreen)
        } else {
            showScreen(ChatsHistory)
        }
    }

    private fun showScreen(screen: Screen) {
        _screenNavigationFlow.emit(screen, viewModelScope)
    }

    fun handleException(exception: Throwable) {
        _exceptionMutableSharedFlow.emit(exception, viewModelScope)
    }

    fun onRemoveApiKey() {
        viewModelScope.launch {
            ApiKeyRepository.deleteApiKey()
        }
    }

    fun onPaymentSetApiKey() {
        showScreen(ApiKeyScreen)
    }

    sealed class Screen

    object LoginScreen : Screen()
    object ApiKeyScreen : Screen()
    object PaymentScreen : Screen()
    object ChatsHistory : Screen()
    object TextEditScreen: Screen()
    class AiChatScreen(val sessionId: String? = null) : Screen()
    object SettingScreen : Screen()
}