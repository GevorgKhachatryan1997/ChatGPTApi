package com.example.chatgptapi.ui.screen_fragment

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.chatgptapi.MainViewModel
import com.example.chatgptapi.R
import com.example.chatgptapi.data.ChatGptRepository
import com.example.chatgptapi.ui.ChatViewModel

class ChatFragment : ScreenFragment(R.layout.chat_fragment) {

    private val chatViewModel: ChatViewModel by viewModels()

    companion object {
        private const val ARG_AI_MODEL_ID = "arg.ai_model_id"

        fun newInstance(modelId: String) = ChatFragment().apply {
            arguments = bundleOf(ARG_AI_MODEL_ID to modelId)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val modelId = requireArguments().getString(ARG_AI_MODEL_ID) ?: throw IllegalArgumentException("model id is null")
        ChatGptRepository.findUiAiModel(modelId)?.let {
            chatViewModel.model = it
        } // TODO handle casi if model not found
    }

    override val screen: MainViewModel.Screen
        get() = MainViewModel.AiChatScreen(chatViewModel.model)
}