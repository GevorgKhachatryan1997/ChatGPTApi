package com.chatgpt.letaithink.data

import com.chatgpt.letaithink.exception.PurchaseNotExists
import com.chatgpt.letaithink.manager.PaymentManager

object PurchaseRepository {
    private val localDataSource = LocalDataSource()

    suspend fun updatePurchase(purchaseJson: String) {
        deletePurchase()
        localDataSource.insertPurchase(purchaseJson)
    }

    suspend fun deletePurchase() {
        localDataSource.deletePuchase()
    }

    @Throws(PurchaseNotExists::class)
    suspend fun ensurePurchase() {
        if (purchaseExpired()) {
            throw PurchaseNotExists()
        }
    }

    private suspend fun purchaseExpired(): Boolean {
        val purchase = localDataSource.getPurchase() ?: return true
        return PaymentManager.purchaseExpired(purchase.purchaseTime ?: 0)
    }
}