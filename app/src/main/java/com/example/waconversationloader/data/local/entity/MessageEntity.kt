package com.example.waconversationloader.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.waconversationloader.data.model.MessageModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID

@Entity
data class MessageEntity(
    val sender: String?,
    val message: String,
    val date: Long,
    var roomId: Int?,
    val isSender: Boolean,
    val showDate: Boolean,
    @PrimaryKey val id: Int? = null
) {
    fun toMessageModel(): MessageModel {
        return MessageModel(
            sender = sender,
            message = message,
            date = run {
                val instant = Instant.ofEpochMilli(date)
                LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
            },
            isSender = isSender,
            showDate = showDate,
            roomId = roomId
        )
    }
}