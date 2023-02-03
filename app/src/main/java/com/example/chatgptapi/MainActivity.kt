package com.example.chatgptapi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val chatGPTViewModel: ChatGPTViewModel by lazy { ViewModelProvider(this)[ChatGPTViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        chatGPTViewModel.loadAiModels()

        lifecycleScope.launch {
            chatGPTViewModel.stateFlow.collect { aiModels ->
                aiModels.forEach { aiModel ->
                    aiModel.model?.let { Log.d("aiModel", it) }
                }
            }
        }
    }
}