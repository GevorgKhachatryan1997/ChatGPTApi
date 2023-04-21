package com.chatgpt.letaithink.utils

import com.chatgpt.letaithink.data.AiChatMessage
import com.chatgpt.letaithink.data.AiImage
import com.chatgpt.letaithink.data.AiMessage
import com.chatgpt.letaithink.data.ConversationItem
import com.chatgpt.letaithink.data.UserMessage
import com.chatgpt.letaithink.model.remoteModels.ImageModel
import com.chatgpt.letaithink.model.remoteModels.TextCompletion
import com.chatgpt.letaithink.model.databaseModels.MessageEntity
import com.chatgpt.letaithink.model.databaseModels.MessageType
import com.chatgpt.letaithink.model.remoteModels.ChatCompletion
import com.google.gson.JsonSyntaxException
import java.util.*

@Throws(IllegalStateException::class)
fun ConversationItem.toMessageEntity(sessionId: String): MessageEntity {
    val currentTime = Calendar.getInstance().timeInMillis
    return when (this) {
        is UserMessage -> MessageEntity(sessionId, MessageType.USER_INPUT, message, currentTime, -1)
        is AiMessage -> MessageEntity(
            sessionId,
            MessageType.AI_COMPLETION,
            JsonUtil.toJson(textCompletion),
            currentTime,
            textCompletion.usage?.total_tokens ?: -1
        )

        is AiChatMessage -> MessageEntity(
            sessionId,
            MessageType.AI_CHAT_COMPLETION,
            JsonUtil.toJson(chatCompletion),
            currentTime,
            chatCompletion.usage?.total_tokens ?: -1
        )

        is AiImage -> MessageEntity(
            sessionId,
            MessageType.AI_IMAGE_GENERATION,
            JsonUtil.toJson(image),
            currentTime,
            -1
        )

        else -> throw IllegalStateException("Unknown conversation item: ${javaClass.simpleName}")
    }
}

@Throws(JsonSyntaxException::class)
fun MessageEntity.toConversationItem(): ConversationItem {
    val conversationItem = when (type) {
        MessageType.USER_INPUT -> UserMessage(content)
        MessageType.AI_COMPLETION -> AiMessage(JsonUtil.fromJson(content, TextCompletion::class.java))
        MessageType.AI_CHAT_COMPLETION -> AiChatMessage(JsonUtil.fromJson(content, ChatCompletion::class.java))
        MessageType.AI_IMAGE_GENERATION -> AiImage(JsonUtil.fromJson(content, ImageModel::class.java))
    }

    return conversationItem
}