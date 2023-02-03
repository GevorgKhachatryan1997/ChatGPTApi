package com.example.chatgptapi.model

import com.google.gson.annotations.SerializedName

data class AiModel(
    var id: String?,
    @SerializedName("object")
    var model: String?,
    @SerializedName("organization-owner")
    var organization: String?,
    @SerializedName("permission")
    var permissions : List<Any>?
)

/*
"id": "model-id-0",
"object": "model",
"owned_by": "organization-owner",
"permission": [...]*/
