package com.example.chatgptapi.ui.screen_fragment

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.chatgptapi.MainViewModel
import com.example.chatgptapi.R
import com.example.chatgptapi.ui.LoginViewModel

class LoginFragment :
    ScreenFragment(R.layout.login_fragment) {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private var btnGoogleAuthentication: Button? = null
    private val mainViewModel: MainViewModel by viewModels(ownerProducer = { requireActivity() })
    private val loginViewModel: LoginViewModel by viewModels()
    private val loginResultHandler =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result: ActivityResult? ->
            loginViewModel.onAuthenticationResult(requireActivity(), result?.data)
        }

    override val screen: MainViewModel.Screen
        get() = MainViewModel.LoginScreen


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnGoogleAuthentication = view.findViewById(R.id.btnGoogleAuthentication)
        btnGoogleAuthentication?.setOnClickListener {
            loginViewModel.signInRequest(requireActivity()) {
                loginResultHandler.launch(it)
            }
        }

        lifecycleScope.launchWhenCreated {
            loginViewModel.authenticationSharedFlow.collect {
                when (it) {
                    LoginViewModel.AuthenticationSuccess -> {
                        showChatsHistoryScreen()
                    }
                    LoginViewModel.AuthenticationFailed -> {
                        Toast.makeText(requireContext(), "authentication fail", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun showChatsHistoryScreen() {
        mainViewModel.showScreen(MainViewModel.ChatsHistory)
    }
}