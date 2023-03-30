package com.chatgpt.letaithink

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.chatgpt.letaithink.data.RemoteDataSource.Companion.RESPONSE_CODE_INVALID_API_KEY
import com.chatgpt.letaithink.exception.ApiError
import com.chatgpt.letaithink.exception.NoConnectionException
import com.chatgpt.letaithink.ui.dialog.ErrorDialog
import com.chatgpt.letaithink.ui.dialog.InvalidApiKeyDialog
import com.chatgpt.letaithink.ui.screen_fragment.*
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), InvalidApiKeyDialog.Listener {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.toolbar))

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.screenNavigationFlow.collect { screen ->
                    navigateToScreen(screen)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.exceptionSharedFlow.collect { exception ->
                    when (exception) {
                        is NoConnectionException -> {
                            showErrorDialog(getString(R.string.no_internet_connection))
                        }
                        is ApiError -> {
                            when (exception.errorCode) {
                                RESPONSE_CODE_INVALID_API_KEY -> {
                                    InvalidApiKeyDialog.newInstance(exception.message ?: "")
                                        .show(supportFragmentManager)
                                }
                                else -> showErrorDialog(exception.message)
                            }
                        }
                        else -> showErrorDialog(exception.message)
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        viewModel.onActivityStart()
    }

    override fun onUpdateApiKey() {
        clearStack()
        showApiKeyFragment()
    }

    private fun clearStack() {
        val fragmentManager: FragmentManager = supportFragmentManager
        while (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStackImmediate()
        }
    }

    private fun navigateToScreen(screen: MainViewModel.Screen) {
        when (screen) {
            is MainViewModel.ChatsHistory -> {
                showChatsHistory()
            }
            is MainViewModel.AiChatScreen -> {
                showAiChatFragment(screen.sessionId)
            }
            is MainViewModel.LoginScreen -> {
                clearStack()
                showLoginFragment()
            }
            is MainViewModel.SettingScreen -> {
                showSettingFragment()
            }
            is MainViewModel.ApiKeyScreen -> {
                showApiKeyFragment()
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

    private fun showLoginFragment() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view)
        if (currentFragment is LoginFragment) return
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.fragment_container_view, LoginFragment.newInstance())
        }
    }

    private fun showSettingFragment() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view)
        if (currentFragment is SettingFragment) return
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.fragment_container_view, SettingFragment())
            addToBackStack(null)
        }
    }

    private fun showApiKeyFragment() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view)
        if (currentFragment is ApiKeyFragment) return
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.fragment_container_view, ApiKeyFragment())
        }
    }

    private fun showErrorDialog(message: String?) {
        message?.let {
            ErrorDialog.newInstance(it).show(supportFragmentManager)
        }
    }
}