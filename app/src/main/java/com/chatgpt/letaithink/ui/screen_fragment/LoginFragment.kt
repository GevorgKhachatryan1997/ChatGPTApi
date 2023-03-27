package com.chatgpt.letaithink.ui.screen_fragment

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.chatgpt.letaithink.MainViewModel
import com.chatgpt.letaithink.R
import com.chatgpt.letaithink.ui.dialog.ErrorDialog
import com.chatgpt.letaithink.ui.viewModel.LoginViewModel

class LoginFragment :
    ScreenFragment(R.layout.login_fragment) {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private var googleSignInImageView: ImageView? = null
    private val mainViewModel: MainViewModel by activityViewModels()
    private val loginViewModel: LoginViewModel by viewModels()
    private val loginResultHandler =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result: ActivityResult? ->
            loginViewModel.onAuthenticationResult(requireActivity(), result?.data)
        }

    override val screen: MainViewModel.Screen
        get() = MainViewModel.LoginScreen


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        googleSignInImageView = view.findViewById(R.id.googleSignInImageView)
        googleSignInImageView?.setOnClickListener {
            loginViewModel.signInRequest(requireActivity()) {
                loginResultHandler.launch(it)
            }
        }

        lifecycleScope.launchWhenCreated {
            loginViewModel.authenticationSharedFlow.collect {
                when (it) {
                    LoginViewModel.AuthenticationSuccess -> {
                        showApiKeyScreen()
                    }
                    LoginViewModel.AuthenticationFailed -> {
                        val message = getString(R.string.authentication_fail)
                        showErrorDialog(message)
                    }
                }
            }
        }
    }

    private fun showApiKeyScreen() {
        mainViewModel.showScreen(MainViewModel.ApiKeyScreen)
    }

    private fun showErrorDialog(message: String) {
        ErrorDialog.newInstance(message).show(childFragmentManager)
    }
}