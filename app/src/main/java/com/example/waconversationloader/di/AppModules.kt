package com.example.waconversationloader.di

import android.content.Context
import androidx.room.Dao
import androidx.room.Room
import com.example.waconversationloader.data.RepositoryImpl
import com.example.waconversationloader.data.local.ChatsDao
import com.example.waconversationloader.data.local.ChatsDatabase
import com.example.waconversationloader.domain.Repository

fun provideRepository(db: ChatsDatabase): Repository {
    return RepositoryImpl(
        dao = db.dao
    )
}

fun provideChatsDatabase(context: Context): ChatsDatabase {
    return Room.databaseBuilder(
        context = context,
        klass = ChatsDatabase::class.java,
        name = "chats_db"
    ).build()
}