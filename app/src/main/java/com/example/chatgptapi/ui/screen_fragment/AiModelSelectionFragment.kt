package com.example.chatgptapi.ui.screen_fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatgptapi.MainViewModel
import com.example.chatgptapi.R
import com.example.chatgptapi.ui.adapter.ChatGPTAdapter
import com.example.chatgptapi.ui.adapter.OnAiModelClickListener
import com.example.chatgptapi.ui.model.UiAiModel
import com.example.chatgptapi.ui.AiSelectionViewModel

class AiModelSelectionFragment : ScreenFragment(R.layout.ai_model_fragment) {

    private val mainViewModel: MainViewModel by viewModels(ownerProducer = { requireActivity() })
    private val chatGPTViewModel: AiSelectionViewModel by viewModels()
    private val chatGptAdapter = ChatGPTAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chatGptAdapter.itemClickListener = OnAiModelClickListener { uIaIModel ->
            showAiChatScreen(uIaIModel)
        }

        initRecycleView(view)
        chatGPTViewModel.loadUiAiModels()

        lifecycleScope.launchWhenCreated {
            chatGPTViewModel.stateFlowUiAiModels.collect { uiAiModels ->
                chatGptAdapter.update(uiAiModels)
            }
        }
    }

    override val screen: MainViewModel.Screen
        get() = MainViewModel.AiModelSelection

    private fun initRecycleView(view: View) {
        view.findViewById<RecyclerView>(R.id.recyclerViewUiModels).apply {
            adapter = chatGptAdapter
            layoutManager = GridLayoutManager(context, 1, GridLayoutManager.VERTICAL, false)
        }
    }

    private fun showAiChatScreen(model: UiAiModel) {
        mainViewModel.showScreen(MainViewModel.AiChatScreen(model))
    }
}