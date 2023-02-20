package com.example.chatgptapi.data

import com.example.chatgptapi.model.ImageModel
import com.example.chatgptapi.model.TextCompletion

sealed interface ConversationItem

data class AiThinking(val message: String) : ConversationItem
data class UserMessage(val message: String) : ConversationItem
data class AiMessage(val textCompletion: TextCompletion) : ConversationItem
data class AiImage(val image: ImageModel) : ConversationItem