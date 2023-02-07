package com.example.chatgptapi.ui.screen_fragment

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.chatgptapi.MainViewModel

abstract class ScreenFragment(@LayoutRes contentLayoutId: Int): Fragment(contentLayoutId) {

    private val mainViewModel: MainViewModel by viewModels(ownerProducer = { requireActivity() })

    override fun onStart() {
        super.onStart()

        mainViewModel.currentScreen = screen
    }

    abstract val screen: MainViewModel.Screen
}