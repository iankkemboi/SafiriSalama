package com.safirisalama.bot.android.chat.domain.usecase

import android.os.Build
import androidx.annotation.RequiresApi
import com.safirisalama.bot.android.chat.data.ConversationRepository
import com.safirisalama.bot.android.chat.data.GPTAction
import com.safirisalama.bot.android.chat.data.GPTResponse
import com.safirisalama.bot.android.chat.data.Message
import com.safirisalama.bot.android.chat.data.MessageStatus
import com.safirisalama.bot.android.chat.data.api.OpenAIRepository
import com.safirisalama.bot.android.chat.data.extractAction

class SendChatRequestUseCase(
    private val openAIRepository: OpenAIRepository,
    private val conversationRepository: ConversationRepository,
) {

    @RequiresApi(Build.VERSION_CODES.O)
    suspend operator fun invoke(
        prompt: String,
    ) {
        val message = Message(
            text = prompt,
            isFromUser = true,
            messageStatus = MessageStatus.Sending,
        )
        val response = displayMessage(prompt, message)
        conversationRepository.setMessageStatusToSent(message.id)
        val text: String?
        when (response) {
            is GPTResponse.Action ->
                text = when (response.action) {
                    is GPTAction.DestinationSelection ->
                        "Great,what is your vibe?"

                    is GPTAction.FlightPricesSelection ->
                        "Sure,let me look below for flight prices"

                    is GPTAction.Transactions ->
                        ""
                }

            is GPTResponse.Text ->
                text = response.text

            else -> {
                text = "Completed Else"
            }
        }
        val newMessage = Message(
            text = text,
            isFromUser = false,
            messageStatus = MessageStatus.Sent,
        )
         conversationRepository.addMessage(newMessage)
        conversationRepository.setMessageStatusToSent(newMessage.id)
    }

    private suspend fun displayMessage(prompt: String, message: Message): GPTResponse? {
        val conversation = conversationRepository.addMessage(message)

        when (
            val action = extractAction(prompt)
        ) {
            is GPTAction.DestinationSelection ->
                return GPTResponse.Action(action)

            is GPTAction.FlightPricesSelection ->
                return GPTResponse.Action(action)

            is GPTAction.Transactions,
            ->
                return GPTResponse.Action(action)

            else -> {
                try {
                    val reply = openAIRepository.sendChatRequest(conversation)

                    return GPTResponse.Text(reply.text)
                } catch (exception: Exception) {
                    conversationRepository.setMessageStatusToError(message.id)
                }
            }
        }
        return null
    }
}
