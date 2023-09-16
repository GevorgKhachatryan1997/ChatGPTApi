package com.chatgpt.letaithink

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.chatgpt.letaithink.ui.dialog.ErrorDialog
import com.chatgpt.letaithink.ui.dialog.ExceededYourQuota
import com.chatgpt.letaithink.ui.dialog.InvalidApiKeyDialog
import com.chatgpt.letaithink.ui.screen_fragment.ApiKeyFragment
import com.chatgpt.letaithink.ui.screen_fragment.ChatFragment
import com.chatgpt.letaithink.ui.screen_fragment.ChatsHistoryFragment
import com.chatgpt.letaithink.ui.screen_fragment.LoginFragment
import com.chatgpt.letaithink.ui.screen_fragment.SettingFragment
import com.chatgpt.letaithink.utils.OpenAIUtils
import com.openai.api.OpenAIManager.RESPONSE_CODE_INVALID_API_KEY
import com.openai.api.OpenAIManager.RESPONSE_CODE_RATE_LIMIT_REACHED
import com.openai.api.exception.ApiError
import com.openai.api.exception.NoConnectionException
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(),
    InvalidApiKeyDialog.Listener,
    ExceededYourQuota.Listener {

    private val viewModel: MainViewModel by viewModels()

    private val currentFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.fragment_container_view)

    private val backPressCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (supportFragmentManager.backStackEntryCount <= 1) {
                finish()
            } else {
                isEnabled = false
                onBackPressedDispatcher.onBackPressed()
                isEnabled = true
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.toolbar))

        onBackPressedDispatcher.addCallback(this, backPressCallback)

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

                                RESPONSE_CODE_RATE_LIMIT_REACHED -> {
                                    ExceededYourQuota.newInstance(exception.message ?: "")
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

        viewModel.onActivityStart(currentFragment == null)
    }

    override fun onUpdateApiKey() {
        clearStack()
        showApiKeyFragment()
    }

    override fun onCheckPlans() {
        OpenAIUtils.navigateToUsagePage(this)
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
                clearStack()
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

    // TODO fix code duplication
    private fun showChatsHistory() {
        if (currentFragment is ChatsHistoryFragment) return
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.fragment_container_view, ChatsHistoryFragment())
            addToBackStack(null)
        }
    }

    private fun showAiChatFragment(sessionId: String?) {
        if (currentFragment is ChatFragment) return
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.fragment_container_view, ChatFragment.newInstance(sessionId))
            addToBackStack(null)
        }
    }

    private fun showLoginFragment() {
        if (currentFragment is LoginFragment) return
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.fragment_container_view, LoginFragment.newInstance())
            addToBackStack(null)
        }
    }

    private fun showSettingFragment() {
        if (currentFragment is SettingFragment) return
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.fragment_container_view, SettingFragment())
            addToBackStack(null)
        }
    }

    private fun showApiKeyFragment() {
        if (currentFragment is ApiKeyFragment) return
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.fragment_container_view, ApiKeyFragment())
            addToBackStack(null)
        }
    }

    private fun showErrorDialog(message: String?) {
        message?.let {
            ErrorDialog.newInstance(it).show(supportFragmentManager)
        }
    }
}