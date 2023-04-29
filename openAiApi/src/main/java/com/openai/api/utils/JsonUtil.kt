package com.openai.api.utils

import com.openai.api.exception.InvalidJsonExeption
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException

object JsonUtil {

    private val gson = GsonBuilder().create()

    fun toJson(obj: Any): String {
        return gson.toJson(obj)
    }

    @Throws(InvalidJsonExeption::class)
    fun <T> fromJson(json: String, classOfT: Class<T>): T {

        try {
            return gson.fromJson(json, classOfT)
        } catch (e: JsonSyntaxException) {
            throw InvalidJsonExeption(e.message, e)
        }
    }
}