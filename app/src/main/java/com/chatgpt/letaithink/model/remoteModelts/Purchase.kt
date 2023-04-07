package com.chatgpt.letaithink.model.remoteModelts

data class Purchase(
    val orderId: String?,
    val packageName: String?,
    val productId: String?,
    val purchaseTime: Long?,
    val purchaseState: Int?,
    val developerPayload: String?,
    val purchaseToken: String?,
    val autoRenewing: Boolean?,
    val acknowledged: Boolean?,
    val isSubscription: Boolean?,
    val signature: String?,
    val json: String?
)