package com.safirisalama.bot.android.di

import com.safirisalama.bot.android.chat.data.ConversationRepository
import com.safirisalama.bot.android.chat.data.api.OpenAIRepository
import com.safirisalama.bot.android.chat.domain.usecase.ObserveMessagesUseCase
import com.safirisalama.bot.android.chat.domain.usecase.ResendMessageUseCase
import com.safirisalama.bot.android.chat.domain.usecase.SendChatRequestUseCase
import com.safirisalama.bot.android.chat.ui.ChatViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val chatModule = module {
    viewModel {
        ChatViewModel(get(), get(), get())
    }
    single { OpenAIRepository(openAI = get()) }
    single { ConversationRepository() }

    single { SendChatRequestUseCase(openAIRepository = get(), conversationRepository = get()) }
    single { ResendMessageUseCase(openAIRepository = get(), conversationRepository = get()) }
    single { ObserveMessagesUseCase(conversationRepository = get()) }
}
