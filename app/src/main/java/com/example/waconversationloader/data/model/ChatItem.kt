package com.example.waconversationloader.data.model

import java.time.LocalDateTime

data class ChatItem(
    var sender: String? = null,
    var message: String,
    var date: LocalDateTime?,
    var isSender: Boolean = false,
    var showDate: Boolean = false
)