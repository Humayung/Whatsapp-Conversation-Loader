package com.example.waconversationloader.data.model

import java.time.LocalDateTime
import java.util.UUID

data class ChatItem(
    var sender: String? = null,
    var message: String,
    var date: LocalDateTime?,
    var isSender: Boolean = false,
    var showDate: Boolean = false,
    val id: String = UUID.randomUUID().toString()
)