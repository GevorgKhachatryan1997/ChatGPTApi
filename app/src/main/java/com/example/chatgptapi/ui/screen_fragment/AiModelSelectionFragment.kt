package com.example.chatgptapi.ui.screen_fragment

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatgptapi.MainViewModel
import com.example.chatgptapi.R
import com.example.chatgptapi.ui.AiSelectionViewModel
import com.example.chatgptapi.ui.adapter.ChatGPTAdapter
import com.example.chatgptapi.ui.adapter.OnAiModelClickListener
import com.example.chatgptapi.ui.model.UiAiModel

class AiModelSelectionFragment : ScreenFragment(R.layout.ai_model_fragment) {

    private val mainViewModel: MainViewModel by viewModels(ownerProducer = { requireActivity() })
    private val chatGPTViewModel: AiSelectionViewModel by viewModels()
    private val chatGptAdapter = ChatGPTAdapter()
    private var btnSetting: Button? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chatGptAdapter.itemClickListener = OnAiModelClickListener { uIaIModel ->
            showAiChatScreen(uIaIModel)
        }

        btnSetting = view.findViewById(R.id.btnSetting)
        btnSetting?.setOnClickListener {
            showSettingScreen()
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

    private fun showSettingScreen() {
        mainViewModel.showScreen(MainViewModel.SettingScreen)
    }
}