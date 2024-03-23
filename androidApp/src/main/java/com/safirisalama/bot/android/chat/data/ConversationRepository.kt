package com.safirisalama.bot.android.chat.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.UUID
import java.util.regex.Pattern

class ConversationRepository {

    private var messagesList: MutableList<Message> = mutableListOf()

    private val _conversationFlow = MutableStateFlow(
        value = Conversation(list = messagesList),
    )
    val conversationFlow = _conversationFlow.asStateFlow()

    fun addMessage(message: Message): Conversation {
        messagesList.add(message)
        return updateConversationFlow(messagesList)
    }

    fun resendMessage(message: Message): Conversation {
        messagesList.remove(message)
        messagesList.add(message)
        return updateConversationFlow(messagesList)
    }

    fun setMessageStatusToSent(messageId: String) {
        val index = messagesList.indexOfFirst { it.id == messageId }
        if (index != -1) {
            messagesList[index] = messagesList[index].copy(messageStatus = MessageStatus.Sent)
        }

        updateConversationFlow(messagesList)
    }

    fun setMessageStatusToError(messageId: String) {
        val index = messagesList.indexOfFirst { it.id == messageId }
        if (index != -1) {
            messagesList[index] = messagesList[index].copy(messageStatus = MessageStatus.Error)
        }

        updateConversationFlow(messagesList)
    }

    private fun updateConversationFlow(messagesList: List<Message>): Conversation {
        val conversation = Conversation(
            list = messagesList,
        )
        _conversationFlow.value = conversation

        return conversation
    }
}

class Conversation(
    val list: List<Message>,
)

data class Message(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isFromUser: Boolean,
    val messageStatus: MessageStatus = MessageStatus.Sending,
)

data class Transactions(
    val id: String = UUID.randomUUID().toString(),
    val vendorName: String,
    val date: LocalDate,
    val amount: Int,
) {
    val formattedDate: String
        get() {
            val dateFormatter = SimpleDateFormat("dd MMM")
            return dateFormatter.format(date)
        }
}

sealed class MessageStatus {
    object Sending : MessageStatus()
    object Error : MessageStatus()
    object Sent : MessageStatus()
}

sealed class GPTResponse {
    data class Text(val text: String) : GPTResponse()
    data class Action(
        val action: GPTAction,
    ) : GPTResponse()
}

sealed class GPTAction {
    class DestinationSelection() : GPTAction()
    class FlightPricesSelection() : GPTAction()

    class Transactions(val count: Int, val description: String) : GPTAction()
}

fun extractAction(message: String): GPTAction? {
    val lowercasedMessage = message.lowercase()
    val transferPattern = "Great give me excellent destination tips to (\\w+)"
    val transactionsPattern = "last (\\d+) transactions paid for (\\w+)"
    val canBeTransfer = lowercasedMessage.contains("great give me excellent destination tips to")
    val flightPrices = lowercasedMessage.contains("flight prices")

    val regex = Pattern.compile(transferPattern)
    val matcher = regex.matcher(lowercasedMessage)

    if (canBeTransfer) {
        return GPTAction.DestinationSelection()
    }
    if (flightPrices) {
        return GPTAction.FlightPricesSelection()
    }
    return null
}
