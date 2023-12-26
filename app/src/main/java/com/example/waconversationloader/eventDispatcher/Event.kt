package com.example.waconversationloader.eventDispatcher

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import kotlin.random.Random

sealed interface MainUiEvent : Event {
    data class CollapseBottomSheet(val randomNumber: Float = Random.nextFloat()) : MainUiEvent
    data class ExpandBottomSheet(val randomNumber: Float = Random.nextFloat()) : MainUiEvent
}

sealed interface MainEvent : Event {
    data class ExpandBottomSheet(val sheetContent: @Composable ColumnScope.() -> Unit) : MainEvent
    data object CollapseBottomSheet : MainEvent
}
