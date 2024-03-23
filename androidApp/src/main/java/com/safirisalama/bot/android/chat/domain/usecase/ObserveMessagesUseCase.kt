package com.safirisalama.bot.android.chat.domain.usecase

import com.safirisalama.bot.android.chat.data.ConversationRepository

class ObserveMessagesUseCase(
    private val conversationRepository: ConversationRepository,
) {

    operator fun invoke() = conversationRepository.conversationFlow
}
