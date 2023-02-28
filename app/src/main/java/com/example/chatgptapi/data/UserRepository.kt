package com.example.chatgptapi.data

import com.example.chatgptapi.model.databaseModels.UserEntity

object UserRepository {
    private val localDataSource = LocalDataSource()

    fun isUserAuthentication(): Boolean {
        return localDataSource.isUserAuthentication()
    }

    fun insertUser(userEntity: UserEntity) {
        localDataSource.insertUser(userEntity)
    }

    fun deleteUserData() {
        localDataSource.deleteUserData()
    }

    fun getUser(): UserEntity? {
        return localDataSource.getUser()
    }
}