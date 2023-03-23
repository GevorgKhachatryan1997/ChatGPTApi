package com.chatgpt.letaithink.ui.screen_fragment

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chatgpt.letaithink.MainViewModel
import com.chatgpt.letaithink.R
import com.chatgpt.letaithink.data.UserMessage
import com.chatgpt.letaithink.ui.adapter.ChatListAdapter
import com.chatgpt.letaithink.ui.adapter.ChatModsAdapter
import com.chatgpt.letaithink.ui.viewModel.ChatViewModel
import com.chatgpt.letaithink.utils.hideKeyboard
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
    private lateinit var rvChatModes: RecyclerView
    private lateinit var pbLoading: ProgressBar

    private val chatViewModel: ChatViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    private val chatAdapter = ChatListAdapter().apply {
        onChatListener = object : ChatListAdapter.ChatListListener {
            override fun onDownloadClick(image: Bitmap) {
                chatViewModel.onDownloadClick(requireContext(), image)
            }

            override fun onEditClick(message: UserMessage) {
                etChatInput.setText(message.message)
            }

        }
    }
    private val chatModesAdapter = ChatModsAdapter().apply {
        chatModeListener = ChatModsAdapter.OnChatModeListener {
            chatViewModel.onChatModeSelected(it)
        }
    }

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
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = chatAdapter
        }

        rvChatModes = view.findViewById<RecyclerView>(R.id.rvChatModes).apply {
            adapter = chatModesAdapter
        }

        pbLoading = view.findViewById(R.id.pbLoading)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                chatViewModel.conversationItems.collect {
                    chatAdapter.submitList(it)
                    rvChat.smoothScrollToPosition(it.size)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                chatViewModel.chatModes.collect {
                    chatModesAdapter.updateMods(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                chatViewModel.progressLoading.collect { visible ->
                    pbLoading.visibility = if (visible) View.VISIBLE else View.GONE
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                chatViewModel.exceptionFlow.collect {
                    mainViewModel.handleException(it)
                }
            }
        }
    }
}