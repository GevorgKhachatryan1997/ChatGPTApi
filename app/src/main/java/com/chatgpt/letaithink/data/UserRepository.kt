package com.chatgpt.letaithink.data

import com.chatgpt.letaithink.model.databaseModels.UserEntity

object UserRepository {

    fun getUser(): UserEntity {
        return UserEntity(
            userId = "777",
            name = "James",
            lastname = "Bond",
            "james.bond@mail.com"
        )
    }
}