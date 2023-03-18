package com.example.chatgptapi.ui.screen_fragment

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.chatgptapi.MainViewModel
import com.example.chatgptapi.R
import com.example.chatgptapi.domain.GoogleAuthenticationHelper
import com.example.chatgptapi.ui.viewModel.SettingViewModel

class SettingFragment : ScreenFragment(R.layout.setting_fragment) {

    private var btnLogOut: Button? = null
    private val mainViewModel: MainViewModel by activityViewModels()
    private val settingViewModel: SettingViewModel by viewModels()

    override val screen: MainViewModel.Screen
        get() = MainViewModel.SettingScreen

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnLogOut = view.findViewById(R.id.btnLogout)
        btnLogOut?.setOnClickListener {
            settingViewModel.onLogoutClick(requireContext())
            showLoginScreen()
        }
    }

    private fun showLoginScreen() {
        mainViewModel.showScreen(MainViewModel.LoginScreen)
    }
}