package com.openai.api.models

class ErrorBody(val error: Error? = null)

class Error(
    val message: String? = null,
    val type: String? = null,
    val param: String? = null,
    val code: String? = null
)
