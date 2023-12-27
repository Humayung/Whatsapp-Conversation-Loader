package com.example.waconversationloader.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.waconversationloader.data.local.entity.ChatroomEntity
import com.example.waconversationloader.data.local.entity.MessageEntity

@Database(
    entities = [ChatroomEntity::class, MessageEntity::class],
    version = 1
)
abstract class ChatsDatabase : RoomDatabase() {
    abstract val dao: ChatsDao
}