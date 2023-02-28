package com.example.chatgptapi.ui.screen_fragment

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.example.chatgptapi.MainViewModel
import com.example.chatgptapi.R
import com.example.chatgptapi.domain.GoogleAuthenticationHelper
import com.example.chatgptapi.domain.Listener
import com.example.chatgptapi.ui.LoginViewModel

class LoginFragment :
    ScreenFragment(R.layout.login_fragment) {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private var btnGoogleAuthentication: Button? = null
    private val mainViewModel: MainViewModel by viewModels(ownerProducer = { requireActivity() })
    private val loginViewModel: LoginViewModel by viewModels(ownerProducer = { requireActivity() })
    private val googleAuthenticationHelper = GoogleAuthenticationHelper()
    private val loginResultHandler =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult? ->
            googleAuthenticationHelper.loadGoogleAuthentication(result?.data)
        }

    private var onAuthenticationSuccessListener = object : Listener {
        override fun onLoginSuccess() {
            showAiModelScreen()
            Toast.makeText(requireContext(), "authentication complete", Toast.LENGTH_SHORT).show()
        }

        override fun onLoginFailure() {
            Toast.makeText(requireContext(), "authentication fail", Toast.LENGTH_SHORT).show()
        }
    }

    override val screen: MainViewModel.Screen
        get() = MainViewModel.SignInScreen


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnGoogleAuthentication = view.findViewById(R.id.btnGoogleAuthentication)
        btnGoogleAuthentication?.setOnClickListener {
            googleAuthenticationHelper.signInRequest(requireActivity()) {
                loginResultHandler.launch(it)
            }
            googleAuthenticationHelper.onAuthenticationSuccessListener =
                onAuthenticationSuccessListener

            loginViewModel.insertUser(googleAuthenticationHelper.getUser())
        }
    }

    private fun showAiModelScreen() {
        mainViewModel.showScreen(MainViewModel.AiModelSelection)
    }
}