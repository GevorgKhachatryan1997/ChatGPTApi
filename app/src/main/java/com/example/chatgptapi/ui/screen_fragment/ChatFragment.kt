package com.example.chatgptapi.ui.screen_fragment

import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatgptapi.MainViewModel
import com.example.chatgptapi.R
import com.example.chatgptapi.ui.adapter.ChatListAdapter
import com.example.chatgptapi.data.ChatGptRepository
import com.example.chatgptapi.ui.ChatViewModel
import com.example.chatgptapi.utils.hideKeyboard
import kotlinx.coroutines.launch

class ChatFragment : ScreenFragment(R.layout.chat_fragment) {

    private val chatViewModel: ChatViewModel by viewModels()

    private lateinit var ivSend: ImageView
    private lateinit var etChatInput: EditText
    private lateinit var rvChat: RecyclerView

    private val chatAdapter = ChatListAdapter()

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
        } // TODO handle case if model not found
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etChatInput = view.findViewById(R.id.etChatInput)
        ivSend = view.findViewById<ImageView?>(R.id.ivSend).apply {
            setOnClickListener {
                val text = etChatInput.text.toString()
                if (text.isNotBlank()) {
                    etChatInput.setText("")
                    chatViewModel.onSendClick(text)
                    etChatInput.hideKeyboard()
                } // TODO handle blank case
            }
        }
        rvChat = view.findViewById<RecyclerView>(R.id.rvChat).apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = chatAdapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                chatViewModel.updateConversation.collect {
                    chatAdapter.submitList(it)
                    chatAdapter.notifyDataSetChanged()
                    rvChat.smoothScrollToPosition(it.size - 1)

                }
            }
        }
    }

    override val screen: MainViewModel.Screen
        get() = MainViewModel.AiChatScreen(chatViewModel.model)
}