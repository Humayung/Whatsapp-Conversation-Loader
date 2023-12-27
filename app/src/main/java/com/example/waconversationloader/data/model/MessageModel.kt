package com.example.waconversationloader.data.model

import com.example.waconversationloader.data.local.entity.MessageEntity
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID

data class MessageModel(
    var sender: String? = null,
    var message: String,
    var date: LocalDateTime?,
    var isSender: Boolean = false,
    var showDate: Boolean = false,
    var roomId: Int?,
    val id: String = UUID.randomUUID().toString()
) {
    fun toMessageEntity(): MessageEntity {
        return MessageEntity(
            sender = sender,
            message = message,
            date = date?.toInstant(ZoneOffset.UTC)?.toEpochMilli() ?: 0,
            roomId = roomId,
            isSender = isSender,
            showDate = showDate,

        )
    }

}