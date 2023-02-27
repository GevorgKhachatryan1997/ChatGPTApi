package com.example.chatgptapi

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.chatgptapi.ui.screen_fragment.ChatFragment
import com.example.chatgptapi.ui.screen_fragment.ChatsHistoryFragment
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.screenNavigationFlow.collect { screen ->
                    navigateToScreen(screen)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        viewModel.onActivityStart()
    }

    private fun navigateToScreen(screen: MainViewModel.Screen) {
        when (screen) {
            is MainViewModel.ChatsHistory -> {
                showChatsHistory()
            }
            is MainViewModel.AiChatScreen -> {
                showAiChatFragment(screen.sessionId)
            }
        }
    }

    private fun showChatsHistory() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view)
        if (currentFragment is ChatsHistoryFragment) return
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.fragment_container_view, ChatsHistoryFragment())
        }
    }

    private fun showAiChatFragment(sessionId: String?) {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view)
        if (currentFragment is ChatFragment) return
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.fragment_container_view, ChatFragment.newInstance(sessionId))
            addToBackStack(null)
        }
    }
}