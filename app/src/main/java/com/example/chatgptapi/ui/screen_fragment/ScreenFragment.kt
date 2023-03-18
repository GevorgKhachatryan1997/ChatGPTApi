package com.example.chatgptapi.ui.screen_fragment

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.chatgptapi.MainViewModel

abstract class ScreenFragment(@LayoutRes contentLayoutId: Int): Fragment(contentLayoutId) {

    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onStart() {
        super.onStart()
        mainViewModel.currentScreen = screen
    }
    abstract val screen: MainViewModel.Screen
}