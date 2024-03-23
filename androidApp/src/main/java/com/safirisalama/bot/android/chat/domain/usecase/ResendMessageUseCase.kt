package com.safirisalama.bot.android.chat.domain.usecase

import com.safirisalama.bot.android.chat.data.ConversationRepository
import com.safirisalama.bot.android.chat.data.Message
import com.safirisalama.bot.android.chat.data.api.OpenAIRepository

class ResendMessageUseCase(
    private val openAIRepository: OpenAIRepository,
    private val conversationRepository: ConversationRepository,
) {

    suspend operator fun invoke(
        message: Message,
    ) {
        val conversation = conversationRepository.resendMessage(message)

        try {
            val reply = openAIRepository.sendChatRequest(conversation)
            conversationRepository.setMessageStatusToSent(message.id)
            conversationRepository.addMessage(reply)
        } catch (exception: Exception) {
            conversationRepository.setMessageStatusToError(message.id)
        }
    }
}
