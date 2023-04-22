package com.chatgpt.letaithink.ui.screen_fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.BillingFlowParams
import com.chatgpt.letaithink.MainViewModel
import com.chatgpt.letaithink.R
import com.chatgpt.letaithink.manager.PaymentManager
import com.chatgpt.letaithink.ui.adapter.SubscriptionsAdapter
import kotlinx.coroutines.launch

class PaymentFragment : Fragment(R.layout.payment_fragment) {

    private lateinit var rvSubscriptions: RecyclerView

    private val paymentViewModel: PaymentViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    private val subscriptionsAdapter = SubscriptionsAdapter().apply {
        onItemClickListener = SubscriptionsAdapter.OnSubscriptionClickListener {
            val offerToken = it.subscriptionOfferDetails?.first()?.offerToken ?: return@OnSubscriptionClickListener
            val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(it)
                .setOfferToken(offerToken)
                .build()
            val productDetailsParamsList = mutableListOf(productDetailsParams)

            val billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build()

            paymentViewModel.onLaunchBillingFlow(requireActivity(), billingFlowParams)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvSubscriptions = view.findViewById<RecyclerView>(R.id.rv_subscriptions).apply {
            adapter = subscriptionsAdapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                paymentViewModel.productDetailsFlow.collect { productDetails ->
                    subscriptionsAdapter.updateData(productDetails)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                paymentViewModel.launchBillingFlow.collect { billingParams ->
                    paymentViewModel.onLaunchBillingFlow(requireActivity(), billingParams)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                paymentViewModel.paymentSuccessFlow.collect {
                    mainViewModel.onPaymentSuccess()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                paymentViewModel.paymentFailFlow.collect {
                    Toast.makeText(requireContext(), getString(R.string.payment_error_message), Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}