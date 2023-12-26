package com.example.waconversationloader.persentation

import androidx.compose.runtime.staticCompositionLocalOf
import com.example.waconversationloader.eventDispatcher.EventDispatcher
import com.example.waconversationloader.eventDispatcher.MainEvent
import com.example.waconversationloader.eventDispatcher.MainUiEvent

val LocalMainEventBus = staticCompositionLocalOf { EventDispatcher<MainEvent>() }
val LocalMainUiEventBus = staticCompositionLocalOf { EventDispatcher<MainUiEvent>() }
val LocalGlobalState = staticCompositionLocalOf { GlobalState() }