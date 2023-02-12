package com.example.chatgptapi.data

import com.example.chatgptapi.R
import com.example.chatgptapi.model.UiAiModel

class LocalDataSource {

    val uiAiModelList = buildList {
        add(UiAiModel(RemoteDataSource.DAVINSI_ID, R.string.davinci, R.string.davinci_info, R.mipmap.ic_launcher))
        add(UiAiModel(RemoteDataSource.CURIE_ID, R.string.curie, R.string.curie_info, R.mipmap.ic_launcher))
        add(UiAiModel(RemoteDataSource.BABBAGE_ID, R.string.babbage, R.string.babbage_info, R.mipmap.ic_launcher))
        add(UiAiModel(RemoteDataSource.ADA_ID, R.string.ada, R.string.ada_info, R.mipmap.ic_launcher))
    }

    fun findUiAIModel(modelId: String): UiAiModel? = uiAiModelList.find { it.id == modelId }
}