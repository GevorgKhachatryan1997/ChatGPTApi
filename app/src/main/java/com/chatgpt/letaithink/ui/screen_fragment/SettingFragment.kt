package com.chatgpt.letaithink.ui.screen_fragment

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.chatgpt.letaithink.MainViewModel
import com.chatgpt.letaithink.R
import com.chatgpt.letaithink.data.ApiKeyRepository
import com.chatgpt.letaithink.ui.dialog.RemoveApiKeyConfirmationDialog
import com.chatgpt.letaithink.utils.EmailUtils
import com.chatgpt.letaithink.utils.NotificationUtils
import com.chatgpt.letaithink.utils.PermissionUtils
import com.chatgpt.letaithink.utils.addToClipboard
import kotlinx.coroutines.launch

class SettingFragment : Fragment(R.layout.setting_fragment),
    RemoveApiKeyConfirmationDialog.Listener {

    private var cvBubble: CardView? = null
    private var cvApiKey: CardView? = null
    private var btnBubbleView: Button? = null
    private var btnSupport: Button? = null
    private var btnUpdateApiKey: Button? = null
    private var btnRemoveApiKey: Button? = null
    private var tvSupportEmail: TextView? = null
    private var tvApiKey: TextView? = null
    private var ivCopyApiKey: ImageView? = null

    private val mainViewModel: MainViewModel by activityViewModels()

    private val notificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            showBubbleViewNotification()
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cvBubble = view.findViewById(R.id.cvBubble)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            cvBubble?.visibility = View.GONE
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

        btnUpdateApiKey = view.findViewById<Button>(R.id.btnUpdateApiKey).apply {
            setOnClickListener {
                mainViewModel.onUpdateApiKey()
            }
        }
        btnRemoveApiKey = view.findViewById<Button>(R.id.btnRemoveApiKey).apply {
            setOnClickListener {
                RemoveApiKeyConfirmationDialog.newInstance().show(childFragmentManager)
            }
        }

        tvSupportEmail = view.findViewById<TextView>(R.id.tv_support_email).apply {
            text = EmailUtils.SUPPORT_EMAIL
        }

        cvApiKey = view.findViewById(R.id.apiKeyCardView)
        tvApiKey = view.findViewById(R.id.tvApiKey)
        lifecycleScope.launch {
            val userApiKey = ApiKeyRepository.getApiKey()
            if (userApiKey != null) {
                cvApiKey?.visibility = View.VISIBLE
                tvApiKey?.text = userApiKey
            } else {
                cvApiKey?.visibility = View.GONE
            }
        }
        ivCopyApiKey = view.findViewById<ImageView?>(R.id.ivCopyApikey).apply {
            setOnClickListener {
                view.context.addToClipboard(tvApiKey?.text.toString())
            }
        }
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

    override fun onRemoveApiKey() {
        cvApiKey?.visibility = View.GONE
        mainViewModel.onRemoveApiKey()
    }
}