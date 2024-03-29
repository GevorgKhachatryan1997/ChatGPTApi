package com.chatgpt.letaithink.data

import com.chatgpt.letaithink.model.databaseModels.UserEntity

object UserRepository {
    private val localDataSource = LocalDataSource()

    suspend fun isUserAuthenticated(): Boolean {
        return localDataSource.isUserAuthenticated()
    }

    suspend fun insertUser(userEntity: UserEntity) {
        localDataSource.insertUser(userEntity)
    }

    suspend fun deleteUserData() {
        localDataSource.deleteUserData()
    }

    suspend fun getUser(): UserEntity? {
        return localDataSource.getUser()
    }
}