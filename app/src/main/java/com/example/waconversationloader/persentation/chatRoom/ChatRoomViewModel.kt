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
import com.example.waconversationloader.data.model.ChatItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ChatRoomViewModel : ViewModel() {
    var chats by mutableStateOf(listOf<ChatItem>(), policy = neverEqualPolicy())
    var positionFound by mutableStateOf<Int?>(null)
    var searchCount by mutableStateOf(0)
    var people: HashSet<String> by mutableStateOf(hashSetOf())
    var receiverName by mutableStateOf("Someone")

    fun loadChats(context: Context, fileUri: Uri) {
        people.clear()
        chats = listOf()
        viewModelScope.launch(Dispatchers.IO) {
            val inputStream = context.contentResolver.openInputStream(fileUri)
            chats = try {
                val r = BufferedReader(InputStreamReader(inputStream))
                var prevDate: LocalDateTime? = null
                r.readLines().map {
                    val parsed = parse(it)
                    parsed.showDate =
                        prevDate?.toLocalDate()?.equals(parsed.date?.toLocalDate()) == false
                    prevDate = parsed.date
                    parsed
                }
            } catch (e: java.lang.Exception) {
                listOf()
            }
            Log.d("TAG", "loadChats: $people")
        }
    }

    fun setMe(me: String) {
        chats = chats.map { chatItem ->
            chatItem.isSender = me == chatItem.sender
            chatItem
        }
        receiverName = if (people.size == 2) {
            people.first { it != me }
        } else {
            "Group chat"
        }
    }

    fun find(firstVisible: Int, query: String, forward: Boolean) {
        if (query.isEmpty()) return
        viewModelScope.launch {
            searchCount++
            val predicate: (ChatItem) -> Boolean =
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
            if (forward && indexFound != -1) positionFound = start + indexFound
            else positionFound = indexFound
        }
    }


    private fun parse(line: String): ChatItem {
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
            people.add(name)
            return ChatItem(
                date = LocalDateTime.parse(
                    "$date, $time",
                    DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm")
                ),
                sender = name,
                message = text,
            )
        } catch (e: java.lang.Exception) {
            return ChatItem(
                isSender = true,
                date = null,
                message = line,
            )
        }
    }
}