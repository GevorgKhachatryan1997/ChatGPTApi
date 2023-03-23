package com.chatgpt.letaithink.ui.screen_fragment

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.chatgpt.letaithink.MainViewModel
import com.chatgpt.letaithink.R
import com.chatgpt.letaithink.ui.viewModel.SettingViewModel
import com.chatgpt.letaithink.utils.NotificationUtils
import com.chatgpt.letaithink.utils.PermissionUtils

class SettingFragment : ScreenFragment(R.layout.setting_fragment) {

    private var btnLogOut: Button? = null
    private var btnBubbleView: Button? = null

    private val mainViewModel: MainViewModel by activityViewModels()
    private val settingViewModel: SettingViewModel by viewModels()

    override val screen: MainViewModel.Screen
        get() = MainViewModel.SettingScreen

    private val notificationPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        showBubbleViewNotification()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnLogOut = view.findViewById<Button>(R.id.btnLogout).apply {
            setOnClickListener {
                settingViewModel.onLogoutClick(requireContext())
                showLoginScreen()
            }
        }

        btnBubbleView = view.findViewById<Button>(R.id.betBubbleView).apply {
            setOnClickListener {
                showBubbleView()
            }
        }
    }

    private fun showLoginScreen() {
        mainViewModel.showScreen(MainViewModel.LoginScreen)
    }

    private fun showBubbleView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            !PermissionUtils.hasPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
        ) {
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            showBubbleViewNotification()
        }
    }

    private fun showBubbleViewNotification() {
        NotificationUtils.showBubbleViewNotification()
        requireActivity().finish()
    }
}