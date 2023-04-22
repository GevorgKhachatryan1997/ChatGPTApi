package com.chatgpt.letaithink.ui.screen_fragment

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
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
import com.chatgpt.letaithink.ui.dialog.DeleteChatConfirmationDialog
import com.chatgpt.letaithink.ui.viewModel.ChatsHistoryViewModel
import kotlinx.coroutines.launch

class ChatsHistoryFragment : Fragment(R.layout.chats_history_fragment),
    MenuProvider,
    DeleteChatConfirmationDialog.Listener {

    private val chatsHistoryAdapter = ChatsHistoryListAdapter().apply {
        itemClickListener = object : ChatsHistoryListAdapter.OnSessionClickListener {
            override fun onSessionClick(session: SessionEntity) {
                mainViewModel.onSessionClick(session.sessionId)
            }

            override fun onSessionDeleteClick(session: SessionEntity) {
                DeleteChatConfirmationDialog.newInstance(session.sessionId).show(childFragmentManager)
            }

            override fun updateSessionName(session: SessionEntity, name: String) {
                chatsHistoryViewModel.onUpdateSessionName(session, name)
            }
        }
    }
    private val chatsHistoryViewModel: ChatsHistoryViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    private var btnCreateNewChat: CardView? = null
    private var rvChatsHistory: RecyclerView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        btnCreateNewChat = view.findViewById<CardView>(R.id.cv_new_chat).also {
            it.setOnClickListener {
                mainViewModel.onSessionClick()
            }
        }
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

    override fun onStart() {
        super.onStart()

        requireActivity().findViewById<Toolbar>(R.id.toolbar).setTitle(R.string.chats)
    }

    override fun onStop() {
        super.onStop()

        requireActivity().findViewById<Toolbar>(R.id.toolbar).setTitle(R.string.app_name)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.chat_history, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        val handled = when (menuItem.itemId) {
            R.id.menu_settings -> {
                mainViewModel.onSettingClick()
                true
            }
            else -> false
        }

        return handled
    }

    override fun onDialogChatDelete(sessionId: String) {
        chatsHistoryViewModel.onSessionDeleteClick(sessionId)
    }
}