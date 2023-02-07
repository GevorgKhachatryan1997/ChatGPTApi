package com.example.chatgptapi.data

import com.example.chatgptapi.model.AiModels
import com.example.chatgptapi.model.UiAiModel

object ChatGptRepository {

    private val remoteDataSource = RemoteDataSource()

    fun getModels(): AiModels?{
        return remoteDataSource.getModels()
    }

    fun getUiAiModels(): List<UiAiModel>{
        return remoteDataSource.uiAiModelList
    }

    fun findUiAiModel(modelId: String): UiAiModel? = remoteDataSource.findUiAIModel(modelId)
}