package com.chatgpt.letaithink.data

import com.chatgpt.letaithink.manager.PaymentManager

object PurchaseRepository {
    private val localDataSource = LocalDataSource()

    suspend fun updatePurchase(purchaseJson: String) {
        deletePurchase()
        localDataSource.insertPurchase(purchaseJson)
    }

    suspend fun purchaseInvalid(): Boolean {
        val purchase = localDataSource.getPurchase() ?: return true
        return PaymentManager.purchaseExpired(purchase.purchaseTime ?: 0)
    }

    suspend fun deletePurchase() {
        localDataSource.deletePuchase()
    }
}