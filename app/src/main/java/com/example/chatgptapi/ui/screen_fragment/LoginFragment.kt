package com.example.chatgptapi.ui.screen_fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.chatgptapi.MainViewModel
import com.example.chatgptapi.R
import com.example.chatgptapi.domain.GoogleAuthenticationHelper
import com.google.firebase.auth.FirebaseAuth

class LoginFragment :
    ScreenFragment(R.layout.login_fragment) {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private var etMail: EditText? = null
    private var etPassword: EditText? = null
    private var btnLogin: Button? = null
    private var btnCreate: Button? = null
    private var btnGoogleAuthentication: Button? = null
    private var fireBaseAuth: FirebaseAuth? = null
    private val mainViewModel: MainViewModel by viewModels(ownerProducer = { requireActivity() })

    override val screen: MainViewModel.Screen
        get() = MainViewModel.SignInScreen

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etMail = view.findViewById(R.id.etLogin)
        etPassword = view.findViewById(R.id.etPassword)
        btnLogin = view.findViewById(R.id.btnLogin)
        btnCreate = view.findViewById(R.id.btnCrate)
        btnGoogleAuthentication = view.findViewById(R.id.btnGoogleAuthentication)

        GoogleAuthenticationHelper.createSignInRequest(view.context)
        GoogleAuthenticationHelper.signInRequest(
            requireActivity(),
            GoogleAuthenticationHelper.REQ_ONE_TAP
        )

        btnLogin?.setOnClickListener {
            loginUser()
        }

        btnCreate?.setOnClickListener {
            mainViewModel.showScreen(screen)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        btnGoogleAuthentication?.setOnClickListener {
            GoogleAuthenticationHelper.loadGoogleAuthentication(requestCode, data)
        }
    }

    private fun loginUser() {
        if (!etMail?.text.isNullOrBlank() && !etPassword?.text.isNullOrBlank()) {
            fireBaseAuth?.signInWithEmailAndPassword(
                etMail?.text.toString(),
                etPassword?.text.toString()
            )
        }else{
            Toast.makeText(requireContext(), "invalid mail or password", Toast.LENGTH_SHORT).show()
        }
    }
}