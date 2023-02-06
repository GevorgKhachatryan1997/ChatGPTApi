package com.example.chatgptapi.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatgptapi.R
import com.example.chatgptapi.adapter.ChatGPTAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AiModelSelectionFragment : Fragment(R.layout.ai_model_fragment) {

    private val chatGPTViewModel: ChatGPTViewModel by lazy { ViewModelProvider(this)[ChatGPTViewModel::class.java] }
    private val chatGptAdapter = ChatGPTAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecycleView(view)
        chatGPTViewModel.loadUiAiModels()

        lifecycleScope.launch(Dispatchers.IO) {
            chatGPTViewModel.stateFlowUiAiModel.collect { uiAiModels ->
                chatGptAdapter.update(uiAiModels)
            }
        }

    }

    private fun initRecycleView(view: View) {
        view.findViewById<RecyclerView>(R.id.recyclerViewUiModels).apply {
            adapter = chatGptAdapter
            layoutManager = GridLayoutManager(context, 1, GridLayoutManager.VERTICAL, false)
        }
    }
}