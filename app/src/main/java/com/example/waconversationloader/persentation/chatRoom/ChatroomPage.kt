package com.example.waconversationloader.persentation.chatRoom

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.waconversationloader.R
import com.example.waconversationloader.eventDispatcher.MainEvent
import com.example.waconversationloader.persentation.LocalGlobalState
import com.example.waconversationloader.persentation.LocalMainEventBus
import com.example.waconversationloader.persentation.components.LetterProfileImage
import com.example.waconversationloader.persentation.nav.LocalNavController
import com.example.waconversationloader.utils.generateColorFromHashCode
import io.skipday.takan.extensions.contrasted
import io.skipday.takan.extensions.setClipboard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatroomPage(roomId: Int) {
    val activity = LocalContext.current as ComponentActivity
    val viewModel by activity.viewModels<ChatRoomViewModel>()
    val context = LocalContext.current
    val eventBus = LocalMainEventBus.current
    LaunchedEffect(Unit) {
        viewModel.loadMessages(roomId)
    }
    val listState = rememberLazyListState()
    LaunchedEffect(viewModel.positionFound, viewModel.searchCount) {
        viewModel.positionFound?.let {
            if (it == -1) {
                Toast.makeText(context, "Couldn't find message", Toast.LENGTH_SHORT).show()
            } else {
                listState.animateScrollToItem(it)
            }
        }
    }
    LaunchedEffect(viewModel.chats) {
        if (viewModel.chats.isNotEmpty()) {
            listState.scrollToItem(viewModel.chats.lastIndex)
        }
    }
    val loadMessagesState by viewModel.loadMessagesState.collectAsState(initial = ChatRoomViewModel.LoadMessagesState.Loading)

    Scaffold(
        topBar = {
            TopBar(
                profileImage = { LetterProfileImage(viewModel.receiverName) },
                modifier = Modifier.fillMaxWidth(),
                name = viewModel.receiverName,
                onSearchPrev = {
                    viewModel.find(listState.firstVisibleItemIndex, it, forward = false)
                },
                onSearchNext = {
                    viewModel.find(listState.firstVisibleItemIndex, it, forward = true)
                },
                onClickMenu = {
                    eventBus.onEvent(MainEvent.ExpandBottomSheet(
                        sheetContent = {
                            if (viewModel.chats.isNotEmpty()) {
                                eventBus.onEvent(MainEvent.ExpandBottomSheet(sheetContent = {
                                    SelectMeDialog(
                                        viewModel.people.toList(),
                                        onItemClick = { viewModel.setMe(it, roomId) }
                                    )
                                }))
                            }

                        }
                    ))
                })
        },
        bottomBar = {
            Text(
                modifier = Modifier
                    .background(Color(0xfff7f7f7))
                    .padding(vertical = 12.dp, horizontal = 16.dp)
                    .fillMaxWidth(),
                text = "This conversation was loaded from whatsapp chat history",
                textAlign = TextAlign.Center,
                fontSize = 12.sp
            )
        },
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            Image(
                painter = painterResource(id = R.drawable.wallpaper),
                contentDescription = "wallpaper",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            if (loadMessagesState == ChatRoomViewModel.LoadMessagesState.Loading) CircularProgressIndicator(
                modifier = Modifier
                    .size(32.dp)
                    .align(
                        Alignment.Center
                    )
            ) else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxSize(),
                    state = listState,
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(viewModel.chats, key = { it.id }) { item ->
                        Bubble(
                            isRight = item.isSender,
                            text = item.message,
                            dateTime = item.date,
                            showDate = item.showDate,
                            sender = item.sender,
                            showName = viewModel.people.size > 2,
                            onClick = {},
                            onLongClick = {
                                context.setClipboard(item.message, "chat")
                            }
                        )
                    }
                }

            }
        }
    }
}


@Composable
fun SelectMeDialog(toList: List<String>, onItemClick: (String) -> Unit) {
    val eventBus = LocalMainEventBus.current
    Column {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = "Your name",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (toList.isEmpty()) {
            Text(
                text = "Cannot retrieve member info",
                fontStyle = FontStyle.Italic,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        } else
            LazyColumn {
                items(toList) {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            eventBus.onEvent(MainEvent.CollapseBottomSheet)
                            onItemClick(it)
                        }
                        .padding(vertical = 8.dp, horizontal = 16.dp)) {
                        Text(text = it)
                    }
                }
            }
    }
}


@Preview
@Composable
fun ChatroomPagePreview() {
    ChatroomPage(1)
}