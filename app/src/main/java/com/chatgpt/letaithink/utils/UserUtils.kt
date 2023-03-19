package com.chatgpt.letaithink.utils

import java.util.UUID

const val USER_ID_PREFIX = "user"

fun generateUserId() = USER_ID_PREFIX + "_${UUID.randomUUID()}"