package com.example.chatgptapi.data

import com.example.chatgptapi.model.databaseModels.UserEntity

// TODO use with context and set Dispatchers.IO for suspend methods
object UserRepository {
    private val localDataSource = LocalDataSource()

    suspend fun isUserAuthenticated(): Boolean {
        return true//localDataSource.isUserAuthenticated()
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