package com.chatgpt.letaithink.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch

fun <T>FlowCollector<T>.emit(value: T, scope: CoroutineScope) {
    scope.launch {
        emit(value)
    }
}