package com.example.chatgptapi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatgptapi.adapter.GPTAdapter

class MainActivity : AppCompatActivity() {

    private val chatGPTViewModel: ChatGPTViewModel by lazy { ViewModelProvider(this)[ChatGPTViewModel::class.java] }
    private val gptAdapter = GPTAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initRecycleView()
        val uiAiModels = chatGPTViewModel.getUiAiModel()

        if (savedInstanceState == null) {
            gptAdapter.update(uiAiModels)
        }
    }

    private fun initRecycleView() {
        findViewById<RecyclerView>(R.id.recyclerViewUiModels).apply {
            adapter = gptAdapter
            layoutManager = GridLayoutManager(context, 1, GridLayoutManager.VERTICAL, false)
        }
    }
}