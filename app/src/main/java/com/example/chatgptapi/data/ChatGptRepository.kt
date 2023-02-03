package com.example.chatgptapi.data

import com.example.chatgptapi.model.AiModels

object ChatGptRepository {

    private val remoteDataSource = RemoteDataSource()

    suspend fun getModels(): AiModels?{
        return remoteDataSource.getModels()
    }
}