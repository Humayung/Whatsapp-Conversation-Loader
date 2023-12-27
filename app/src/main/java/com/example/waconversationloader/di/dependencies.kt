package com.example.waconversationloader.di

import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val dependencies = module {
    single { provideChatsDatabase(androidApplication()) }
    single { provideRepository(get()) }
}