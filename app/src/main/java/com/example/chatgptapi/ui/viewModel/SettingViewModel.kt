package com.example.chatgptapi.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatgptapi.data.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingViewModel : ViewModel() {

    fun removeUserData() {
        viewModelScope.launch {
            UserRepository.deleteUserData()
        }
    }
}