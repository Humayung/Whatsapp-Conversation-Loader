package com.example.waconversationloader.persentation.chatRoom

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.waconversationloader.data.model.MessageModel
import com.example.waconversationloader.domain.Repository
import com.example.waconversationloader.domain.Resource
import com.example.waconversationloader.persentation.chatList.ChatListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ChatRoomViewModel : ViewModel(), KoinComponent {
    var job: Job? = null
    val repository: Repository by inject()
    var chats by mutableStateOf(listOf<MessageModel>(), policy = neverEqualPolicy())
    var positionFound by mutableStateOf<Int?>(null)
    var searchCount by mutableStateOf(0)
    var people: List<String> by mutableStateOf(listOf())
    var receiverName by mutableStateOf("Someone")
    val loadMessagesState: MutableSharedFlow<LoadMessagesState> =
        MutableSharedFlow()

    sealed interface LoadMessagesState {
        data class Error(val message: String) : LoadMessagesState
        data object Success : LoadMessagesState
        data object Loading : LoadMessagesState
        data object None : LoadMessagesState
    }

    private fun getPeople(roomId: Int) {
        viewModelScope.launch {
            repository.getChatRoom(roomId).collect { res ->
                when (res) {
                    is Resource.Error -> Unit
                    is Resource.Loading -> Unit
                    is Resource.Success -> {
                        res.data?.let { chatroom ->
                            people = chatroom.people
                            setTitle(chatroom.title, roomId)
                            updateMe(chatroom.me)
                        }
                    }
                }
            }
        }
    }

    fun loadMessages(roomId: Int) {
        job?.cancel()
        job = viewModelScope.launch {
            repository.getMessages(roomId, limit = 0, page = 0)
                .cancellable()
                .collect { res ->
                    Log.d("TAG", "loadMessages: $res")
                    when (res) {
                        is Resource.Error -> loadMessagesState.emit(
                            LoadMessagesState.Error(
                                "Cannot get chatlist, something went wrong"
                            )
                        )

                        is Resource.Loading -> loadMessagesState.emit(LoadMessagesState.Loading)
                        is Resource.Success -> {
                            loadMessagesState.emit(LoadMessagesState.Success)
                            Log.d("TAG", "loadMessages: ${res.data}")
                            chats = res.data ?: listOf()
                            getPeople(roomId)
                        }
                    }
                }
        }
    }


    fun setMe(me: String, roomId: Int) {
        viewModelScope.launch {
            repository.setChatroomMe(me, roomId).collect {
                when (it) {
                    is Resource.Error -> Unit
                    is Resource.Loading -> Unit
                    is Resource.Success -> {
                        updateMe(me)
                        Log.d("TAG", "setMe: $people")
                        if (people.size == 2) {
                            setTitle(people.first { person -> person != me }, roomId)
                        }
                    }
                }
            }
        }
    }

    private fun setTitle(title: String, roomId: Int) {
        viewModelScope.launch {
            repository.setChatroomTitle(title, roomId).collect {
                when (it) {
                    is Resource.Error -> Unit
                    is Resource.Loading -> Unit
                    is Resource.Success -> {
                        receiverName = title
                    }
                }
            }
        }
    }

    private fun updateMe(me: String) {
        chats = chats.map { chatItem ->
            chatItem.isSender = me == chatItem.sender
            chatItem
        }
    }

    fun find(firstVisible: Int, query: String, forward: Boolean) {
        if (query.isEmpty()) return
        viewModelScope.launch {
            searchCount++
            val predicate: (MessageModel) -> Boolean =
                { it.message.lowercase().contains(query.lowercase()) }
            val (start, end) = run {
                if (forward) firstVisible + 1 to chats.lastIndex
                else 0 to firstVisible
            }
            val searchRange = chats.subList(start, end)
            val indexFound = if (forward) searchRange.indexOfFirst(predicate)
            else searchRange.indexOfLast(
                predicate
            )
            positionFound = if (forward && indexFound != -1) start + indexFound
            else indexFound
        }
    }

}