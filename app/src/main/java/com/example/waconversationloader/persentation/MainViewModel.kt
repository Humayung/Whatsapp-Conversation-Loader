package com.example.waconversationloader.persentation

import android.util.Log
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.waconversationloader.eventDispatcher.Event
import com.example.waconversationloader.eventDispatcher.EventDispatcher
import com.example.waconversationloader.eventDispatcher.MainEvent
import com.example.waconversationloader.eventDispatcher.MainUiEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.random.Random


class MainViewModel : ViewModel() {
    var sheetContent by mutableStateOf<@Composable ColumnScope.() -> Unit>({})
    val uiEventBus = EventDispatcher<MainUiEvent>()
    val eventBus = EventDispatcher<MainEvent>()

    init {
        eventBus.subscribe(viewModelScope){ event ->
            when(event){
                MainEvent.CollapseBottomSheet -> collapseBottomSheet()
                is MainEvent.ExpandBottomSheet -> expandBottomSheet(event)
            }
        }
    }

    private fun collapseBottomSheet() {
        uiEventBus.onEvent(MainUiEvent.CollapseBottomSheet(Random.nextFloat()))
    }

    private fun expandBottomSheet(event: MainEvent.ExpandBottomSheet) {
        Log.d("TAG", "expandBottomSheet: ${event.sheetContent}")
        sheetContent = event.sheetContent
        uiEventBus.onEvent(MainUiEvent.ExpandBottomSheet(Random.nextFloat()))
    }
}