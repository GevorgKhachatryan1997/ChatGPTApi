package com.chatgpt.letaithink.manager

import android.annotation.SuppressLint
import android.content.Context
import com.android.billingclient.api.*
import com.android.billingclient.api.Purchase.PurchaseState
import com.chatgpt.letaithink.data.PurchaseRepository
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.TimeUnit

@SuppressLint("StaticFieldLeak")
object PaymentManager {

    lateinit var context: Context

    private const val MONTHLY_SUBSCRIPTION: String = "monthly_subscription"
    private val MONTHLY_BILLING_PERIOD = TimeUnit.DAYS.toMillis(31L)

    private val managerScope = CoroutineScope(Dispatchers.IO + SupervisorJob() + CoroutineName("PaymentManager"))

    val SUPPORTED_SUBSCRIPTIONS = listOf(MONTHLY_SUBSCRIPTION)

    val Purchase.isPurchased: Boolean
        get() = purchaseState == PurchaseState.PURCHASED

    fun init(context: Context) {
        // PaymentManager.context = context
    }

    fun createBillingClient(purchasesUpdatedListener: PurchasesUpdatedListener): BillingClient {
        return BillingClient.newBuilder(context)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()
    }

    fun creteSubscriptionProduct(productId: String) = QueryProductDetailsParams.Product.newBuilder()
        .setProductId(productId)
        .setProductType(BillingClient.ProductType.SUBS)
        .build()

    fun createProductDetailsParams(productDetails: MutableList<QueryProductDetailsParams.Product>) = QueryProductDetailsParams.newBuilder()
        .setProductList(productDetails)
        .build()

    suspend fun acknowledgePurchase(
        billingClient: BillingClient,
        purchases: List<Purchase>,
        onSuccess: (Purchase) -> Unit,
        onFailure: (Int) -> Unit
    ) {
        purchases
            .find { it.isPurchased && !it.isAcknowledged }
            ?.let { purchase ->
                val ackPurchaseResult = acknowledgePurchase(billingClient, purchase.purchaseToken)
                when (ackPurchaseResult.responseCode) {
                    BillingClient.BillingResponseCode.OK,
                    BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                        onSuccess(purchase)
                    }
                    else -> onFailure(ackPurchaseResult.responseCode)
                }
            }
    }

    fun purchaseExpired(purchaseTime: Long): Boolean {
        val duration = MONTHLY_BILLING_PERIOD
        val currentTime = Calendar.getInstance().timeInMillis
        if (purchaseTime > currentTime) return true
        return currentTime - duration > purchaseTime
    }

    fun updatePurchase(billingClient: BillingClient) {
        managerScope.launch {
            if (!billingClient.isReady) return@launch

            val result = getSubscriptionPurchases(billingClient)
            result.purchasesList
                .find { it.isPurchased && it.isAcknowledged }
                ?.let { PurchaseRepository.updatePurchase(it.originalJson) }
        }
    }

    suspend fun getSubscriptionPurchases(billingClient: BillingClient): PurchasesResult = billingClient.queryPurchasesAsync(
        QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()
    )

    private suspend fun acknowledgePurchase(billingClient: BillingClient, purchaseToken: String) = withContext(Dispatchers.IO) {
        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchaseToken)
        billingClient.acknowledgePurchase(acknowledgePurchaseParams.build())
    }
}