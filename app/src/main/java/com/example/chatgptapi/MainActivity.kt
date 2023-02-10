package com.example.chatgptapi

import android.content.Intent
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
import kotlinx.coroutines.launch
import com.example.chatgptapi.domain.GoogleAuthenticationHelper
import com.example.chatgptapi.domain.GoogleAuthenticationHelper.REQ_ONE_TAP
import com.example.chatgptapi.ui.AiModelSelectionFragment

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        GoogleAuthenticationHelper.createSignInRequest(this)
        GoogleAuthenticationHelper.signInRequest(this, REQ_ONE_TAP)

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQ_ONE_TAP -> {
                GoogleAuthenticationHelper.loadGoogleAuthentication(requestCode, data)
            }
        }
    }

    private fun navigateToScreen(screen: MainViewModel.Screen) {
        when (screen) {
            is MainViewModel.AiModelSelection -> {
                showAiModelSelectionFragment()
            }
            is MainViewModel.AiChatScreen -> {
                showAiChatFragment(screen.model)
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
}