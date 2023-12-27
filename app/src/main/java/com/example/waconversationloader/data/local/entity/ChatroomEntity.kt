package com.example.waconversationloader.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.waconversationloader.data.model.ChatroomModel

@Entity
data class ChatroomEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val me: String,
    val people: String
){
    fun toChatroomModel(): ChatroomModel{
        return ChatroomModel(
            title = title,
            me = me,
            id = id,
            people = people.split(",")
        )
    }
}