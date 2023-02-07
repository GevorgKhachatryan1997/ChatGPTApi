package com.example.chatgptapi.data

import com.example.chatgptapi.R
import com.example.chatgptapi.data.ChatGPTApi.Companion.BASE_URL
import com.example.chatgptapi.model.AiModels
import com.example.chatgptapi.model.UiAiModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RemoteDataSource {

    companion object {
        const val DAVINSI_ID = "text-davinci-003"
        const val CURIE_ID = "text-curie-001"
        const val BABBAGE_ID = "text-babbage-001"
        const val ADA_ID = "text-ada-001"
    }

    private val chatGPTService = Retrofit
        .Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ChatGPTApi::class.java)

    val uiAiModelList = buildList {
        add(UiAiModel(DAVINSI_ID, R.string.davinci, R.string.davinci_info, R.mipmap.ic_launcher))
        add(UiAiModel(CURIE_ID, R.string.curie, R.string.curie_info, R.mipmap.ic_launcher))
        add(UiAiModel(BABBAGE_ID, R.string.babbage, R.string.babbage_info, R.mipmap.ic_launcher))
        add(UiAiModel(ADA_ID, R.string.ada, R.string.ada_info, R.mipmap.ic_launcher))
    }

    fun getModels(): AiModels? {
        val response = chatGPTService.getModels().execute()
        if (response.isSuccessful) {
            val aiModels = response.body()
            return aiModels
        }
        //TODO trow exeption for fail case
        return null
    }

    fun findUiAIModel(modelId: String): UiAiModel? = uiAiModelList.find { it.id == modelId }

}