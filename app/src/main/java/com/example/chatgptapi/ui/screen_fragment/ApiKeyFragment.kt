package com.example.chatgptapi.ui.screen_fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.chatgptapi.MainViewModel
import com.example.chatgptapi.R
import com.example.chatgptapi.ui.viewModel.ApiKeyViewModel
import kotlinx.coroutines.launch

class ApiKeyFragment : ScreenFragment(R.layout.api_key_fragment) {

    companion object {
        private const val API_KEY_LINK = "https://platform.openai.com/account/api-keys"
    }

    var tvApiKeyLink: TextView? = null
    var etApiKey: EditText? = null
    var btnAcceptApiKey: Button? = null
    private val apiKeyViewModel: ApiKeyViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    override val screen: MainViewModel.Screen
        get() = MainViewModel.ApiKeyScreen

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvApiKeyLink = view.findViewById(R.id.tvApiKeyLink)
        etApiKey = view.findViewById(R.id.etApiKey)
        btnAcceptApiKey = view.findViewById(R.id.btnAcceptApiKey)

        tvApiKeyLink?.setOnClickListener {
            showUrl(API_KEY_LINK)
        }

        btnAcceptApiKey?.setOnClickListener {
            if (etApiKey?.text?.isNotBlank() == true) {
                val apiKey = etApiKey?.text.toString().trim()
                apiKeyViewModel.onAcceptClick(apiKey)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                apiKeyViewModel.apiKeyValidationSharedFlow.collect { valid ->
                    if (valid) {
                        mainViewModel.showScreen(MainViewModel.ChatsHistory)
                    } else {
                        etApiKey?.error = getString(R.string.api_key_is_invalid)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                apiKeyViewModel.validationInProgress.collect { inProgress ->
                    btnAcceptApiKey?.isEnabled = !inProgress
                    etApiKey?.isEnabled = !inProgress
                }
            }
        }
    }

    private fun showUrl(link: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
    }
}