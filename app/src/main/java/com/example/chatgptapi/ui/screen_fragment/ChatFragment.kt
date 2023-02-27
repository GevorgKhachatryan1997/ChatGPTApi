package com.example.chatgptapi.ui.screen_fragment

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatgptapi.MainViewModel
import com.example.chatgptapi.R
import com.example.chatgptapi.ui.ChatViewModel
import com.example.chatgptapi.ui.adapter.ChatListAdapter
import com.example.chatgptapi.utils.hideKeyboard
import kotlinx.coroutines.launch

class ChatFragment : ScreenFragment(R.layout.chat_fragment) {

    companion object {
        private const val ARG_CHAT_SESSION = "arg.chat_session"

        fun newInstance(sessionId: String?) = ChatFragment().apply {
            arguments = bundleOf(ARG_CHAT_SESSION to sessionId)
        }
    }

    private lateinit var ivSend: ImageView
    private lateinit var etChatInput: EditText
    private lateinit var rvChat: RecyclerView

    private val chatViewModel: ChatViewModel by viewModels()

    private val chatAdapter = ChatListAdapter()

    override val screen: MainViewModel.Screen
        get() = MainViewModel.AiChatScreen(chatViewModel.session?.sessionId)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chatViewModel.setChatSession(requireArguments().getString(ARG_CHAT_SESSION))

        etChatInput = view.findViewById(R.id.etChatInput)
        ivSend = view.findViewById<ImageView?>(R.id.ivSend).apply {
            setOnClickListener {
                // TODO check network
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
}