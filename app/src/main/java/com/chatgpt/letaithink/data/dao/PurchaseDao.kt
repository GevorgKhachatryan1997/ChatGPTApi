package com.chatgpt.letaithink.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.chatgpt.letaithink.model.databaseModels.PurchaseEntity

@Dao
interface PurchaseDao {

    @Insert
    suspend fun insert(purchase: PurchaseEntity)

    @Query("SELECT* FROM purchase_table LIMIT 1")
    suspend fun getPurchase(): PurchaseEntity?

    @Query("DELETE FROM purchase_table")
    suspend fun deletePurchase()
}