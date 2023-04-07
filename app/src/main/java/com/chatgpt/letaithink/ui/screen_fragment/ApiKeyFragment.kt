package com.chatgpt.letaithink.ui.screen_fragment

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.chatgpt.letaithink.MainViewModel
import com.chatgpt.letaithink.R
import com.chatgpt.letaithink.data.PurchaseRepository
import com.chatgpt.letaithink.ui.viewModel.ApiKeyViewModel
import com.chatgpt.letaithink.utils.OpenAIUtils
import kotlinx.coroutines.launch

class ApiKeyFragment : Fragment(R.layout.api_key_fragment) {

    private var btnGenerateApiKey: Button? = null
    private var etApiKey: EditText? = null
    private var btnAcceptApiKey: Button? = null
    private val apiKeyViewModel: ApiKeyViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnGenerateApiKey = view.findViewById<Button>(R.id.btnGenerateApiKey).apply {
            setOnClickListener {
                OpenAIUtils.navigateToGenerateApiKeyPage(requireContext())
            }
        }
        etApiKey = view.findViewById(R.id.etApiKey)
        btnAcceptApiKey = view.findViewById(R.id.btnAcceptApiKey)

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
                        lifecycleScope.launch {
                            val screen = if (PurchaseRepository.purchaseInvalid()) {
                                MainViewModel.ChatsHistory
                            } else {
                                MainViewModel.PaymentScreen
                            }
                            mainViewModel.showScreen(screen)
                        }
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

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                apiKeyViewModel.exceptionFlow.collect {
                    mainViewModel.handleException(it)
                }
            }
        }
    }
}