package com.openai.api.exception

class ApiError(val errorCode: Int, message: String): Exception(message)