package com.openai.api.interceptor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

abstract class AuthorizationInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request: Request = chain.request()
        runBlocking {
            request = withContext(Dispatchers.IO) {
                request
                    .newBuilder()
                    .addHeader("Authorization", "Bearer ${getAPIKey()}")
                    .build()
            }
        }

        return chain.proceed(request)
    }

    abstract fun getAPIKey(): String
}