package com.example.waconversationloader.eventDispatcher

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

interface Event
class EventDispatcher<T : Event> {
    val _listeners: MutableList<EventListener<T>> = mutableListOf()
    val listener: List<EventListener<T>> = _listeners

    val sharedFlow: MutableSharedFlow<T> = MutableSharedFlow()
    fun subscribe(scope: CoroutineScope, block: (T) -> Unit) {
        scope.launch {
            sharedFlow.collect {
                block(it)
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun onEvent(event: T) {
        GlobalScope.launch {
            sharedFlow.emit(event)
        }
    }
}

fun interface EventListener<in T : Event> {
    fun onEvent(e: T)
}