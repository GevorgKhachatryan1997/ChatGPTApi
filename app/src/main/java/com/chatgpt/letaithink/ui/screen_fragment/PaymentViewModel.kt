package com.chatgpt.letaithink.ui.screen_fragment

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.*
import com.chatgpt.letaithink.data.PurchaseRepository
import com.chatgpt.letaithink.manager.PaymentManager
import com.chatgpt.letaithink.manager.PaymentManager.isPurchased
import com.chatgpt.letaithink.utils.emit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PaymentViewModel : ViewModel() {

    companion object {
        private const val START_BILLING_CLIENT_CONNECTION_TRY_COUNT = 3
    }

    private var _launchBillingFlow = MutableSharedFlow<BillingFlowParams>()
    val launchBillingFlow = _launchBillingFlow.asSharedFlow()

    private var _productDetailsFlow = MutableStateFlow<List<ProductDetails>>(emptyList())
    val productDetailsFlow = _productDetailsFlow.asStateFlow()

    private var _paymentSuccessFlow = MutableSharedFlow<Unit>()
    val paymentSuccessFlow = _paymentSuccessFlow.asSharedFlow()

    private var _paymentFailFlow = MutableSharedFlow<Unit>()
    val paymentFailFlow = _paymentFailFlow.asSharedFlow()

    private val billingClient: BillingClient by lazy { PaymentManager.createBillingClient(purchasesUpdatedListener) }

    private var startBillingConnectionCount = 0

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK,
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                viewModelScope.launch {
                    handlePurchase(purchases)
                }
            }
            else -> {
                _paymentFailFlow.emit(Unit, viewModelScope)
            }
        }
    }

    private val billingClientListener = object : BillingClientStateListener {
        override fun onBillingSetupFinished(billingResult: BillingResult) {
            PaymentManager.updatePurchase(billingClient)

            when (billingResult.responseCode) {
                BillingClient.BillingResponseCode.OK,
                BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                    viewModelScope.launch {
                        requestSubscriptions()

                        val result = PaymentManager.getSubscriptionPurchases(billingClient)
                        handlePurchase(result.purchasesList)
                    }
                }
                else -> {
                    _paymentFailFlow.emit(Unit, viewModelScope)
                }
            }
        }

        override fun onBillingServiceDisconnected() {
            if (startBillingConnectionCount < START_BILLING_CLIENT_CONNECTION_TRY_COUNT) {
                startBillingConnectionCount++
                billingClient.startConnection(this)
            } else {
                _paymentFailFlow.emit(Unit, viewModelScope)
            }
        }
    }

    init {
        billingClient.startConnection(billingClientListener)
    }

    fun onLaunchBillingFlow(activity: Activity, billingParams: BillingFlowParams) {
        val billingResult = billingClient.launchBillingFlow(activity, billingParams)
    }

    suspend fun requestSubscriptions() {
        val productList = PaymentManager.SUPPORTED_SUBSCRIPTIONS.map {
            PaymentManager.creteSubscriptionProduct(it)
        }.toMutableList()

        val productDetailsResult = withContext(Dispatchers.IO) {
            billingClient.queryProductDetails(PaymentManager.createProductDetailsParams(productList))
        }

        productDetailsResult.productDetailsList?.let {
            _productDetailsFlow.emit(it)
        }
    }

    override fun onCleared() {
        super.onCleared()

        if (billingClient.isReady) {
            billingClient.endConnection()
        }
    }

    private suspend fun handlePurchase(purchases: List<Purchase>?) {
        val validPurchase = purchases?.find { it.isPurchased && it.isAcknowledged }
        if (validPurchase != null) {
            onSuccess(validPurchase)
            return
        }
        PaymentManager.acknowledgePurchase(
            billingClient,
            purchases ?: emptyList(),
            onSuccess = {
                viewModelScope.launch {
                    onSuccess(it)
                }
            },
            onFailure = {
                _paymentFailFlow.emit(Unit, viewModelScope)
            }
        )
    }

    private suspend fun onSuccess(purchase: Purchase) {
        PurchaseRepository.updatePurchase(purchase.originalJson)
        _paymentSuccessFlow.emit(Unit)
    }
}