package com.chatgpt.letaithink.data

import com.chatgpt.letaithink.model.remoteModels.ChatCompletion
import com.chatgpt.letaithink.model.remoteModels.TextCompletion
import com.chatgpt.letaithink.model.remoteModels.ImageModel

sealed interface ConversationItem

data class AiThinking(val message: String) : ConversationItem
data class UserMessage(val message: String) : ConversationItem
data class AiMessage(val textCompletion: TextCompletion) : ConversationItem
data class AiChatMessage(val chatCompletion: ChatCompletion) : ConversationItem
data class AiImage(val image: ImageModel) : ConversationItem