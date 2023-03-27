package com.chatgpt.letaithink.utils

import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException

object JsonUtil {

    private val gson = GsonBuilder().create()

    fun toJson(obj: Any): String {
        return gson.toJson(obj)
    }

    @Throws(JsonSyntaxException::class)
    fun <T> fromJson(json: String, classOfT: Class<T>): T {
        return gson.fromJson(json, classOfT)
    }
}