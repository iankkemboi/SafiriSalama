package com.safirisalama.bot.android

import android.app.Application
import com.safirisalama.bot.android.di.chatModule
import com.safirisalama.bot.android.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class ChatGptBotApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin()
    }

    private fun startKoin() {
        startKoin {
            // Log Koin into Android logger
            androidLogger()
            // Reference Android context
            androidContext(this@ChatGptBotApplication)
            // Load modules
            modules(
                listOf(
                    networkModule,
                    chatModule,
                ),
            )
        }
    }
}
