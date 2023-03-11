package com.example.chatgptapi.utils

import com.example.chatgptapi.data.AiImage
import com.example.chatgptapi.data.AiMessage
import com.example.chatgptapi.data.ConversationItem
import com.example.chatgptapi.data.UserMessage
import com.example.chatgptapi.model.ImageModel
import com.example.chatgptapi.model.TextCompletion
import com.example.chatgptapi.model.databaseModels.MessageEntity
import com.example.chatgptapi.model.databaseModels.MessageType
import com.google.gson.JsonSyntaxException
import java.util.*

// TODO Fix exception cases in usages
@Throws(IllegalStateException::class)
fun ConversationItem.toMessageEntity(sessionId: String): MessageEntity {
    val currentTime = Calendar.getInstance().timeInMillis
    return when (this) {
        is UserMessage -> MessageEntity(sessionId, MessageType.USER_INPUT, message, currentTime)
        is AiMessage -> MessageEntity(sessionId, MessageType.AI_COMPLETION, JsonUtil.toJson(textCompletion), currentTime)
        is AiImage -> MessageEntity(sessionId, MessageType.AI_IMAGE_GENERATION, JsonUtil.toJson(image), currentTime)
        else -> throw IllegalStateException("Unknown conversation item: ${javaClass.simpleName}")
    }
}

@Throws(JsonSyntaxException::class)
fun MessageEntity.toConversationItem(): ConversationItem {
    val conversationItem = when (type) {
        MessageType.USER_INPUT -> UserMessage(content)
        MessageType.AI_COMPLETION -> AiMessage(JsonUtil.fromJson(content, TextCompletion::class.java))
        MessageType.AI_IMAGE_GENERATION -> AiImage(JsonUtil.fromJson(content, ImageModel::class.java))
    }

    return conversationItem
}