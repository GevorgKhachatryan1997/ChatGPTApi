package com.chatgpt.letaithink.model.databaseModels

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "purchase_table")
class PurchaseEntity(
    val purchaseJson: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}