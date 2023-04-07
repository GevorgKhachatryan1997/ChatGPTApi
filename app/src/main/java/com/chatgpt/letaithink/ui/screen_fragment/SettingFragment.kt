package com.chatgpt.letaithink.ui.screen_fragment

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.chatgpt.letaithink.MainViewModel
import com.chatgpt.letaithink.R
import com.chatgpt.letaithink.data.ApiKeyRepository
import com.chatgpt.letaithink.ui.dialog.LogoutDialog
import com.chatgpt.letaithink.ui.viewModel.SettingViewModel
import com.chatgpt.letaithink.utils.EmailUtils
import com.chatgpt.letaithink.utils.NotificationUtils
import com.chatgpt.letaithink.utils.PermissionUtils
import com.chatgpt.letaithink.utils.addToClipboard

class SettingFragment : ScreenFragment(R.layout.setting_fragment), LogoutDialog.Listener {

    private var btnLogOut: Button? = null
    private var btnBubbleView: Button? = null
    private var btnSupport: Button? = null
    private var btnUpdateApiKey: Button? = null
    private var tvSupportEmail: TextView? = null
    private var tvApiKey: TextView? = null
    private var ivCopyApiKey: ImageView? = null

    private val mainViewModel: MainViewModel by activityViewModels()
    private val settingViewModel: SettingViewModel by viewModels()

    override val screen: MainViewModel.Screen
        get() = MainViewModel.SettingScreen

    private val notificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            showBubbleViewNotification()
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnLogOut = view.findViewById<Button>(R.id.btn_logout).apply {
            setOnClickListener {
                LogoutDialog().show(childFragmentManager, LogoutDialog.TAG)
            }
        }

        btnBubbleView = view.findViewById<Button>(R.id.btn_bubble_view).apply {
            setOnClickListener {
                makeAppBubbleView()
            }
        }

        btnSupport = view.findViewById<Button>(R.id.btn_support).apply {
            setOnClickListener {
                EmailUtils.composeEmail(requireContext())
            }
        }

        btnUpdateApiKey = view.findViewById<Button?>(R.id.btnUpdateApiKey).apply {
            setOnClickListener {
                mainViewModel.showScreen(MainViewModel.ApiKeyScreen)

            }
        }

        tvSupportEmail = view.findViewById<TextView>(R.id.tv_support_email).apply {
            text = EmailUtils.SUPPORT_EMAIL
        }

        tvApiKey = view.findViewById(R.id.tvApiKey)
        lifecycleScope.launchWhenCreated {
            tvApiKey?.text = ApiKeyRepository.getApiKey()
        }
        ivCopyApiKey = view.findViewById<ImageView?>(R.id.ivCopyApikey).apply {
            setOnClickListener {
                view.context.addToClipboard(tvApiKey?.text.toString())
            }
        }
    }

    override fun onDialogLogout() {
        settingViewModel.onLogoutClick(requireContext())
        showLoginScreen()
    }

    private fun showLoginScreen() {
        mainViewModel.showScreen(MainViewModel.LoginScreen)
    }

    private fun makeAppBubbleView() {
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