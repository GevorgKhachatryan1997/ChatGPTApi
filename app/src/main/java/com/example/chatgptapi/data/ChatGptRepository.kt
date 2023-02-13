package com.example.chatgptapi.data

import com.example.chatgptapi.model.*

object ChatGptRepository {

    private val remoteDataSource = RemoteDataSource()
    private val localDataSource = LocalDataSource()

    fun getRemoteModels(): AiModels? {
        return remoteDataSource.getModels()
    }

    fun askQuestion(completion: CompletionRequest): TextCompletion? {
        return remoteDataSource.getCompletion(completion)
    }

    fun generateImage(imageParams: ImageGenerationRequest) = remoteDataSource.generateImage(imageParams)

    fun getUiAiModels(): List<UiAiModel>{
        return localDataSource.uiAiModelList
    }

    fun findUiAiModel(modelId: String): UiAiModel? = localDataSource.findUiAIModel(modelId)
}