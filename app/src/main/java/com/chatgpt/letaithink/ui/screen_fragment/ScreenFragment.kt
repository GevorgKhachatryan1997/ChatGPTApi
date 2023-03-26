package com.chatgpt.letaithink.ui.screen_fragment

import android.content.Context
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.chatgpt.letaithink.MainViewModel

abstract class ScreenFragment(@LayoutRes contentLayoutId: Int): Fragment(contentLayoutId) {

    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mainViewModel.currentScreen = screen
    }
    // TODO try remove
    abstract val screen: MainViewModel.Screen
}