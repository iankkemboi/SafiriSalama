package com.safirisalama.bot.android.chat.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.safirisalama.bot.android.chat.ui.navigation.RootNavHost
import com.safirisalama.bot.android.ui.ChatGptBotAppTheme
import org.koin.androidx.viewmodel.ext.android.stateViewModel

class ChatActivity : ComponentActivity() {

    private val viewModel: ChatViewModel by stateViewModel(
        state = { intent?.extras ?: Bundle() },
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatGptBotAppTheme {
                RootNavHost()
            }
        }
    }
}
