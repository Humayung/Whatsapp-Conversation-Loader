package com.example.waconversationloader.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.waconversationloader.data.local.entity.ChatroomEntity
import com.example.waconversationloader.data.local.entity.MessageEntity

@Dao
interface ChatsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)

    @Query("SELECT * FROM MessageEntity WHERE roomId=:roomId")
    suspend fun getAllMessages(roomId: Int): List<MessageEntity>

    @Query("SELECT * FROM ChatroomEntity WHERE 1")
    suspend fun getAllChatrooms(): List<ChatroomEntity>

    @Query("UPDATE ChatroomEntity SET me=:me WHERE id=:id")
    suspend fun setChatroomMe(me: String, id: Int)

    @Query("UPDATE ChatroomEntity SET title=:title WHERE id=:id")
    suspend fun setChatroomTitle(title: String, id: Int)

    @Query("SELECT * FROM ChatroomEntity WHERE id=:id")
    suspend fun getChatroom(id: Int): ChatroomEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatroom(chatroom: ChatroomEntity)
}