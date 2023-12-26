package com.example.waconversationloader.persentation

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class GlobalState {
    var selectedFile: Uri? by mutableStateOf(null)
}