package com.example.chatgptapi.data

import com.example.chatgptapi.model.AiModels
import com.example.chatgptapi.model.CompletionRequest
import com.example.chatgptapi.model.TextCompletion
import com.example.chatgptapi.model.UiAiModel

object ChatGptRepository {

    private val remoteDataSource = RemoteDataSource()
    private val localDataSource = LocalDataSource()

    fun getRemoteModels(): AiModels? {
        return remoteDataSource.getModels()
    }

    fun askQuestion(completion: CompletionRequest): TextCompletion? {
        return remoteDataSource.getCompletion(completion)
    }

    fun getUiAiModels(): List<UiAiModel>{
        return localDataSource.uiAiModelList
    }

    fun findUiAiModel(modelId: String): UiAiModel? = localDataSource.findUiAIModel(modelId)
}