package com.example.chatgptapi.ui.screen_fragment

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.chatgptapi.MainViewModel
import com.example.chatgptapi.R
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class SignInFragment : ScreenFragment(R.layout.sign_in_fragment) {
    companion object {
        fun newInstance() = SignInFragment()
    }

    override val screen: MainViewModel.Screen
        get() = MainViewModel.LoginScreen

    private var etCreateLogin: EditText? = null
    private var etPassword: EditText? = null
    private var btnCreateUser: Button? = null
    private var mailAuthentication: FirebaseAuth? = null
    private val mainViewModel: MainViewModel by viewModels(ownerProducer = { requireActivity() })

    override fun onStart() {
        super.onStart()
        val user = mailAuthentication?.currentUser
        if (user != null){

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        etCreateLogin = view.findViewById(R.id.etCreateLogin)
        etPassword = view.findViewById(R.id.etCreatePassword)
        btnCreateUser = view.findViewById(R.id.btnCrateUser)
        FirebaseApp.initializeApp(requireContext())
        mailAuthentication = FirebaseAuth.getInstance()
        btnCreateUser?.setOnClickListener {
            createUser(etCreateLogin?.text.toString(), etPassword?.text.toString())
        }
    }

    private fun createUser(email: String, password: String) {
        if (email.isEmpty() && password.isNotEmpty()) {
            mailAuthentication
                ?.createUserWithEmailAndPassword(email, password)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(requireContext(), "user added", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "email or password has not correct",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }
}