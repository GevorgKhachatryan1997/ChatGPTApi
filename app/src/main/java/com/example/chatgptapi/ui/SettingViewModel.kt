package com.example.chatgptapi.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatgptapi.data.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingViewModel : ViewModel() {

    fun removeUserData() {
        viewModelScope.launch(Dispatchers.IO) {
            UserRepository.deleteUserData()
        }
    }
}