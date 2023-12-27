package com.example.waconversationloader.data.model

import com.example.waconversationloader.data.local.entity.ChatroomEntity
import java.time.LocalDateTime
import java.util.UUID

data class ChatroomModel(
    val title: String,
    val me: String,
    val id: Int,
    val people: List<String>
){
    fun toChatroomEntity(): ChatroomEntity{
        return ChatroomEntity(
            title = title,
            me = me,
            id = id,
            people = people.joinToString(",")
        )
    }
}