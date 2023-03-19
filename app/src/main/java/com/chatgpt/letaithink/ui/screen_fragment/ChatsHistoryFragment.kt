package com.chatgpt.letaithink.ui.screen_fragment

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chatgpt.letaithink.MainViewModel
import com.chatgpt.letaithink.R
import com.chatgpt.letaithink.model.databaseModels.SessionEntity
import com.chatgpt.letaithink.ui.adapter.ChatsHistoryListAdapter
import com.chatgpt.letaithink.ui.viewModel.ChatsHistoryViewModel
import kotlinx.coroutines.launch

class ChatsHistoryFragment : ScreenFragment(R.layout.chats_history_fragment) {

    private val chatsHistoryAdapter = ChatsHistoryListAdapter().apply {
        itemClickListener = object : ChatsHistoryListAdapter.OnSessionClickListener {
            override fun onSessionClick(session: SessionEntity) {
                mainViewModel.showScreen(MainViewModel.AiChatScreen(session.sessionId))
            }

            override fun onSessionDeleteClick(session: SessionEntity) {
                // TODO show confirmation dialog before removing  session
                chatsHistoryViewModel.onSessionDeleteClick(session)
            }

            override fun updateSessionName(session: SessionEntity, name: String) {
                chatsHistoryViewModel.onUpdateSessionName(session, name)
            }
        }
    }
    private val chatsHistoryViewModel: ChatsHistoryViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    override val screen: MainViewModel.Screen
        get() = MainViewModel.ChatsHistory

    private var btnCreateNewChat: Button? = null
    private var tvChatsHistoryTitle: TextView? = null
    private var rvChatsHistory: RecyclerView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btnSetting)?.setOnClickListener {
            mainViewModel.showScreen(MainViewModel.SettingScreen)
        }

        btnCreateNewChat = view.findViewById<Button>(R.id.btn_create_new_chat).also {
            it.setOnClickListener {
                mainViewModel.showScreen(MainViewModel.AiChatScreen())
            }
        }
        tvChatsHistoryTitle = view.findViewById(R.id.tv_chats_history_title)
        rvChatsHistory = view.findViewById<RecyclerView>(R.id.rv_chats_history).also {
            it.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            it.adapter = chatsHistoryAdapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                chatsHistoryViewModel.chatsHistoryStateFlow.collect {
                    chatsHistoryAdapter.submitList(it)
                }
            }
        }
    }
}