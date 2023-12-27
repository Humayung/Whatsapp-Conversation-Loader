package com.example.waconversationloader.persentation.chatList

import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.waconversationloader.persentation.LocalGlobalState
import com.example.waconversationloader.persentation.nav.LocalNavController
import com.example.waconversationloader.persentation.nav.Scene
import com.example.waconversationloader.persentation.theme.Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListPage() {
    val navController = LocalNavController.current
    val context = LocalContext.current as ComponentActivity
    val viewModel = ChatListViewModel()
    val globalState = LocalGlobalState.current
    val snackbarHostState = remember { SnackbarHostState() }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.data?.let { uri ->
                    viewModel.loadChats(context, uri)
                } ?: run {
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                }
            }
        }
    )
    val insertMessagesState by viewModel.createChatroomState.collectAsState(initial = ChatListViewModel.CreateChatroomState.None)
    LaunchedEffect(Unit) {
        viewModel.loadChatrooms()
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            topBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(horizontal = 8.dp, vertical = 16.dp)
                ) {
                    Text(text = "WhatsApp History Loader", color = Color.White, fontSize = 16.sp)
                }
            },
            floatingActionButton = {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .then(if (insertMessagesState != ChatListViewModel.CreateChatroomState.Loading) Modifier.clickable {
                            showFileChooser(
                                context,
                                launcher
                            )
                        } else Modifier)
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "add",
                        tint = Color.White
                    )
                }
            },
            floatingActionButtonPosition = FabPosition.End
        ) { contentPadding ->

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                contentAlignment = Alignment.Center
            ) {
                if(viewModel.chatrooms.isEmpty()){
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(contentPadding),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "To get this working, start by exporting your WhatsApp chat first. Then click load history or + button.",
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Note that, not every messages will be correctly parsed at this moment. Contribute to the project to fix this problem.",
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Button(onClick = { showFileChooser(context, launcher) }) {
                                Text(text = "Load history")
                            }
                        }
                    }
                }else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(viewModel.chatrooms) {
                            ChatroomItem(
                                onClick = { navController?.navigate(Scene.ChatRoom.withArgs(it.id.toString())) },
                                name = it.title
                            )
                        }
                    }
                }
            }
            if (insertMessagesState == ChatListViewModel.CreateChatroomState.Loading) {
                Box(
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.3f))
                        .fillMaxSize()
                        .clickable(onClick = {}, indication = null, interactionSource = remember {
                            MutableInteractionSource()
                        }),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(60.dp))
                }
            }
        }

    }
}


private fun showFileChooser(context: Context, launcher: ActivityResultLauncher<Intent>) {
    val intent = Intent(Intent.ACTION_GET_CONTENT)
    intent.type = "*/*"
    intent.addCategory(Intent.CATEGORY_OPENABLE)
    try {
        launcher.launch(Intent.createChooser(intent, "Select a File to Upload"))
    } catch (ex: ActivityNotFoundException) {
        Toast.makeText(
            context, "Please install a File Manager.",
            Toast.LENGTH_SHORT
        ).show()
    }
}

@Preview
@Composable
fun ChatListPagePreview() {
    Theme {
        ChatListPage()
    }
}