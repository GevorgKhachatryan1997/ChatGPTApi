package com.example.chatgptapi.data

import com.example.chatgptapi.model.AiModels
import com.example.chatgptapi.model.UiAiModel

object ChatGptRepository {

    private val remoteDataSource = RemoteDataSource()

    suspend fun getModels(): AiModels?{
        return remoteDataSource.getModels()
    }

    fun getUiAiModels(): List<UiAiModel>{
        return remoteDataSource.getUiAiModels()
    }
}