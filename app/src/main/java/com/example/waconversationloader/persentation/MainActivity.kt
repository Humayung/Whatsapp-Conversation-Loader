package com.example.waconversationloader.persentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.example.waconversationloader.eventDispatcher.MainEvent
import com.example.waconversationloader.eventDispatcher.MainUiEvent
import com.example.waconversationloader.persentation.chatRoom.ChatRoomViewModel
import com.example.waconversationloader.persentation.components.FlipBottomSheet
import com.example.waconversationloader.persentation.components.rememberFlipBottomSheetState
import com.example.waconversationloader.persentation.nav.Navigation
import com.example.waconversationloader.persentation.theme.Theme
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel by viewModels<MainViewModel>()
            val sheetBottomState = rememberFlipBottomSheetState()

            Theme {
                CompositionLocalProvider(
                    LocalMainEventBus provides viewModel.eventBus,
                    LocalMainUiEventBus provides viewModel.uiEventBus
                ) {
                    val scope = rememberCoroutineScope()
                    val uiEventBus = LocalMainUiEventBus.current
                    LaunchedEffect(Unit) {
                        uiEventBus.subscribe(scope) { uiEvent ->
                            when (uiEvent) {
                                is MainUiEvent.CollapseBottomSheet -> {
                                    scope.launch {
                                        sheetBottomState.collapse()
                                    }
                                }

                                is MainUiEvent.ExpandBottomSheet -> {
                                    scope.launch {
                                        sheetBottomState.expand()
                                    }
                                }
                            }
                        }

                    }
                    Surface {
                        FlipBottomSheet(
                            sheetContent = viewModel.sheetContent,
                            sheetState = sheetBottomState
                        ) {
                            Navigation()
                        }
                    }
                }
            }
        }
    }
}