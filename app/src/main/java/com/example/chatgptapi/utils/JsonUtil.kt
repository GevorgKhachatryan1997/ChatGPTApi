package com.example.chatgptapi.utils

import com.google.gson.GsonBuilder

object JsonUtil {

    private val gson = GsonBuilder().create()

    fun toJson(obj: Any): String {
        return gson.toJson(obj)
    }
}