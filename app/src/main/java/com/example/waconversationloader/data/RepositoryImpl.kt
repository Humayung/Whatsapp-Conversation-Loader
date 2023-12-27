package com.example.waconversationloader.data

import com.example.waconversationloader.data.local.ChatsDao
import com.example.waconversationloader.data.model.ChatroomModel
import com.example.waconversationloader.data.model.MessageModel
import com.example.waconversationloader.domain.Repository
import com.example.waconversationloader.domain.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RepositoryImpl(private val dao: ChatsDao) : Repository {
    override fun getMessages(
        roomId: Int,
        page: Int,
        limit: Int
    ): Flow<Resource<List<MessageModel>>> = flow {
        try {
            emit(Resource.Loading())
            val localData = dao.getAllMessages(roomId)
            emit(Resource.Success(localData.map { it.toMessageModel() }))
        } catch (e: Exception) {
            emit(Resource.Error(exception = e, data = null))
        }
    }

    override fun getChatRooms(): Flow<Resource<List<ChatroomModel>>> = flow {
        try {
            emit(Resource.Loading())
            val localData = dao.getAllChatrooms()
            emit(Resource.Success(localData.map { it.toChatroomModel() }))
        } catch (e: Exception) {
            emit(Resource.Error(exception = e, data = null))
        }
    }

    override fun setChatroomMe(me: String, roomId: Int): Flow<Resource<Boolean>> = flow {
        try {
            emit(Resource.Loading())
            dao.setChatroomMe(me, roomId)
            emit(Resource.Success(true))
        } catch (e: Exception) {
            emit(Resource.Error(exception = e, data = null))
        }
    }

    override fun setChatroomTitle(title: String, roomId: Int): Flow<Resource<Boolean>> = flow {
        try {
            emit(Resource.Loading())
            dao.setChatroomTitle(title, roomId)
            emit(Resource.Success(true))
        } catch (e: Exception) {
            emit(Resource.Error(exception = e, data = null))
        }
    }

    override fun getChatRoom(roomId: Int): Flow<Resource<ChatroomModel>> = flow {
        try {
            emit(Resource.Loading())
            val localData = dao.getChatroom(roomId)
            emit(Resource.Success(localData.toChatroomModel()))
        } catch (e: Exception) {
            emit(Resource.Error(exception = e, data = null))
        }
    }

    override fun insertMessages(messages: List<MessageModel>): Flow<Resource<Boolean>> =
        flow {
            try {
                emit(Resource.Loading())
                dao.insertMessages(messages.map { it.toMessageEntity() })
                emit(Resource.Success(true))
            } catch (e: Exception) {
                emit(Resource.Error(exception = e, data = null))
            }
        }

    override fun insertChatroom(chatroomModel: ChatroomModel): Flow<Resource<ChatroomModel>> =
        flow {
            try {
                emit(Resource.Loading())
                dao.insertChatroom(chatroomModel.toChatroomEntity())
                emit(Resource.Success(chatroomModel))
            } catch (e: Exception) {
                emit(Resource.Error(exception = e, data = null))
            }
        }
}