package com.example.waconversationloader.persentation.chatList

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.waconversationloader.data.model.ChatroomModel
import com.example.waconversationloader.data.model.MessageModel
import com.example.waconversationloader.domain.Repository
import com.example.waconversationloader.domain.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ChatListViewModel : ViewModel(), KoinComponent {
    val repository: Repository by inject()
    val createChatroomState: MutableSharedFlow<CreateChatroomState> = MutableSharedFlow()
    val getCharoomsState: MutableSharedFlow<GetCharoomsState> = MutableSharedFlow()
    var chatrooms by mutableStateOf<List<ChatroomModel>>(listOf())

    sealed interface CreateChatroomState {
        data class Error(val message: String) : CreateChatroomState
        data object Success : CreateChatroomState
        data object Loading : CreateChatroomState
        data object None : CreateChatroomState
    }

    sealed interface GetCharoomsState {
        data class Error(val message: String) : GetCharoomsState
        data object Success : GetCharoomsState
        data object Loading : GetCharoomsState
        data object None : CreateChatroomState
    }


    fun loadChatrooms() {
        repository.getChatRooms()
            .onEach { res ->
                when (res) {
                    is Resource.Error -> getCharoomsState.emit(GetCharoomsState.Error("Cannot get chatlist, something went wrong"))
                    is Resource.Loading -> getCharoomsState.emit(GetCharoomsState.Loading)
                    is Resource.Success -> {
                        getCharoomsState.emit(GetCharoomsState.Success)
                        chatrooms = res.data ?: listOf()
                    }
                }

            }.launchIn(viewModelScope)
    }

    fun loadChats(context: Context, fileUri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            createChatroomState.emit(CreateChatroomState.Loading)
            val inputStream = context.contentResolver.openInputStream(fileUri)
            val lines: List<String>
            val messages = try {
                val r = BufferedReader(InputStreamReader(inputStream))
                var prevDate: LocalDateTime? = null
                lines = r.readLines()
                lines.map {
                    val parsed = parse(it)
                    parsed.showDate =
                        prevDate?.toLocalDate()?.equals(parsed.date?.toLocalDate()) == false
                    prevDate = parsed.date
                    parsed
                }
            } catch (e: java.lang.Exception) {
                createChatroomState.emit(CreateChatroomState.Error("Something went wrong"))
                return@launch
            }
            val people = messages.mapNotNull { it.sender }.distinct()
            val title = if (people.size == 2) people.first() else "Group Chat"
            ChatroomModel(
                title = title,
                me = people.first(),
                id = lines.joinToString().hashCode(),
                people = people
            ).apply {
                repository.insertChatroom(this)
                    .onEach { insertChatroomRes ->
                        val chatroomInsert = consumeCreateChatroomResource(insertChatroomRes)
                        chatroomInsert?.let {
                            messages.onEach { it.roomId = this.id }
                            repository.insertMessages(messages)
                                .onEach(::consumeInsertMessagesRes)
                                .launchIn(this@launch)
                        }
                    }
                    .launchIn(this@launch)
            }

        }
    }

    private suspend fun consumeInsertMessagesRes(insertMessagesRes: Resource<Boolean>): Boolean {
        when (insertMessagesRes) {
            is Resource.Error -> createChatroomState.emit(CreateChatroomState.Error("Cannot import messages, something went wrong"))
            is Resource.Loading -> createChatroomState.emit(CreateChatroomState.Loading)
            is Resource.Success -> {
                createChatroomState.emit(CreateChatroomState.Success)
                loadChatrooms()
                return true
            }
        }
        return false
    }

    private suspend fun consumeCreateChatroomResource(chatroomRes: Resource<ChatroomModel>): ChatroomModel? {
        when (chatroomRes) {
            is Resource.Error -> {
                createChatroomState.emit(CreateChatroomState.Error("Cannot create room, something went wrong"))
            }

            is Resource.Loading -> createChatroomState.emit(CreateChatroomState.Loading)
            is Resource.Success -> {
                createChatroomState.emit(CreateChatroomState.Success)
                return chatroomRes.data
            }
        }
        return null
    }

    private fun parse(line: String): MessageModel {
        try {
            val dateEndStr = ", "
            val dateEnd = line.indexOf(dateEndStr)
            val date = line.substring(0, dateEnd)
            val timeEndStr = " - "
            val timeEnd = line.indexOf(timeEndStr)
            val time = line.substring(dateEnd + dateEndStr.length, timeEnd)
            val nameEndStr = ": "
            val nameEnd = line.indexOf(nameEndStr)
            val name = line.substring(timeEnd + timeEndStr.length, nameEnd)
            val text = line.substring(nameEnd + nameEndStr.length)
            return MessageModel(
                date = LocalDateTime.parse(
                    "$date, $time",
                    DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm")
                ),
                sender = name,
                message = text,
                roomId = null
            )
        } catch (e: java.lang.Exception) {
            return MessageModel(
                isSender = true,
                date = null,
                message = line,
                roomId = null
            )
        }
    }
}