package com.chatgpt.letaithink.exception

class ApiError(val errorCode: Int, message: String): Exception(message)