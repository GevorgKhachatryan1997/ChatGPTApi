package com.chatgpt.letaithink.data

import com.chatgpt.letaithink.model.remoteModelts.TextCompletion
import com.chatgpt.letaithink.model.remoteModelts.ImageModel

sealed interface ConversationItem

data class AiThinking(val message: String) : ConversationItem
data class UserMessage(val message: String) : ConversationItem
data class AiMessage(val textCompletion: TextCompletion) : ConversationItem
data class AiImage(val image: ImageModel) : ConversationItem