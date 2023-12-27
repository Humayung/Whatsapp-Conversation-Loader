package com.example.waconversationloader.domain

import com.example.waconversationloader.data.model.ChatroomModel
import com.example.waconversationloader.data.model.MessageModel
import kotlinx.coroutines.flow.Flow

interface Repository {
    fun getMessages(
        roomId: Int,
        page: Int,
        limit: Int
    ): Flow<Resource<List<MessageModel>>>

    fun getChatRooms(): Flow<Resource<List<ChatroomModel>>>
    fun setChatroomMe(me: String, roomId: Int): Flow<Resource<Boolean>>
    fun setChatroomTitle(title: String, roomId: Int): Flow<Resource<Boolean>>
    fun getChatRoom(roomId: Int): Flow<Resource<ChatroomModel>>

    fun insertMessages(messageModel: List<MessageModel>): Flow<Resource<Boolean>>
    fun insertChatroom(chatroomModel: ChatroomModel): Flow<Resource<ChatroomModel>>
}