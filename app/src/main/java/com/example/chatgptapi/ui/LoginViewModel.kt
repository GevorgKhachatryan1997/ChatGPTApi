package com.example.chatgptapi.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatgptapi.data.UserRepository
import com.example.chatgptapi.model.databaseModels.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    fun insertUser(userEntity: UserEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            UserRepository.insertUser(userEntity)
        }
    }
}