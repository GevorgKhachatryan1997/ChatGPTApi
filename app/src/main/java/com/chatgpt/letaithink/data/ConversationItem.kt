package com.chatgpt.letaithink.data

import com.openai.api.models.ChatCompletion
import com.openai.api.models.TextCompletion
import com.openai.api.models.ImageModel

sealed interface ConversationItem

data class AiThinking(val message: String) : ConversationItem
data class UserMessage(val message: String) : ConversationItem
data class AiMessage(val textCompletion: TextCompletion) : ConversationItem
data class AiChatMessage(val chatCompletion: ChatCompletion) : ConversationItem
data class AiImage(val image: ImageModel) : ConversationItem