package com.example.chatgptapi

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.chatgptapi.ui.model.UiAiModel
import com.example.chatgptapi.ui.screen_fragment.AiModelSelectionFragment
import com.example.chatgptapi.ui.screen_fragment.ChatFragment
import com.example.chatgptapi.ui.screen_fragment.GoogleAuthenticationFragment
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
            is MainViewModel.AiModelSelection -> {
                showAiModelSelectionFragment()
            }
            is MainViewModel.AiChatScreen -> {
                showAiChatFragment(screen.model)
            }
            is MainViewModel.AuthenticationScreen -> {
                showAuthenticationFragment()
            }
        }
    }

    private fun showAiModelSelectionFragment() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view)
        if (currentFragment is AiModelSelectionFragment) return
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.fragment_container_view, AiModelSelectionFragment())
        }
    }

    private fun showAiChatFragment(model: UiAiModel) {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view)
        if (currentFragment is ChatFragment) return
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.fragment_container_view, ChatFragment.newInstance(model.id))
            addToBackStack(null)
        }
    }

    private fun showAuthenticationFragment() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view)
        if (currentFragment is GoogleAuthenticationFragment) return
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.fragment_container_view, GoogleAuthenticationFragment.newInstance())
        }
    }
}