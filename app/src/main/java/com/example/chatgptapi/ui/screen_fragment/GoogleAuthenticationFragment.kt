package com.example.chatgptapi.ui.screen_fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.chatgptapi.MainViewModel
import com.example.chatgptapi.R
import com.example.chatgptapi.domain.GoogleAuthenticationHelper

class GoogleAuthenticationFragment :
    ScreenFragment(R.layout.google_authentication_fragment) {

    companion object{
        fun newInstance() = GoogleAuthenticationFragment()
    }

    var btnGoogleAuthentication: Button? = null

    override val screen: MainViewModel.Screen
        get() = MainViewModel.AuthenticationScreen

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnGoogleAuthentication = view.findViewById(R.id.btnGoogleAuthentication)
        GoogleAuthenticationHelper.createSignInRequest(view.context)
        GoogleAuthenticationHelper.signInRequest(
            requireActivity(),
            GoogleAuthenticationHelper.REQ_ONE_TAP
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        btnGoogleAuthentication?.setOnClickListener {
            GoogleAuthenticationHelper.loadGoogleAuthentication(requestCode, data)
        }
    }
}