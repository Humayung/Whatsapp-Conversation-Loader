package com.example.waconversationloader.persentation.chatList

import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.Navigator
import androidx.navigation.compose.rememberNavController
import com.example.waconversationloader.R
import com.example.waconversationloader.persentation.LocalGlobalState
import com.example.waconversationloader.persentation.nav.LocalNavController
import com.example.waconversationloader.persentation.nav.Scene
import com.example.waconversationloader.utils.uniformTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListPage() {
    val navController = LocalNavController.current
    val context = LocalContext.current
    val globalState = LocalGlobalState.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.data?.let { uri ->
                    globalState.selectedFile = uri
                    navController?.navigate(Scene.ChatRoom.route)
                } ?: run {
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                }
            }
        }
    )
    Scaffold { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
        ) {
            Image(
                painter = painterResource(id = R.drawable.screenshot),
                contentDescription = "screenshot",
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "To get this working, start by exporting your WhatsApp chat first. Then click load history.",
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