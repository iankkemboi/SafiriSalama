package com.safirisalama.bot.android.chat.data.api

import android.util.Log
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.safirisalama.bot.android.chat.data.Conversation
import com.safirisalama.bot.android.chat.data.Message
import com.safirisalama.bot.android.chat.data.MessageStatus

@OptIn(BetaOpenAI::class)
class OpenAIRepository(private val openAI: OpenAI) {

    @Throws(NoChoiceAvailableException::class)
    suspend fun sendChatRequest(
        conversation: Conversation,
    ): Message {
        val systemChatMessage = ChatMessage(
            content = "You are a travel helper, is dedicated to providing assistance and information strictly related to African travel. It maintains a catchy and fun tone, focusing only on topics relevant to travel within the African borders. You will only answer questions directly related to African travel and nothing else.The helper can also ask further clarifying questions if the queries are vague and need more specific input.Please also suggest prompts i can ask chatgpt in the next query,all 3 prompts being in json format,in one json body.Please only submit the json without displaying any kind of description about the json or the future prompts.Only the future prompts will be in json the rest of the response stays the same",
            role =
            ChatRole.System,
        )
        val chatMessageList = conversation.toChatMessages().toMutableList()
        chatMessageList.add(systemChatMessage)
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-4"),
            messages = chatMessageList,
        )
        for (value in chatMessageList) {
            Log.v("Content", value.content)
        }

        val chatMessage = openAI.chatCompletion(chatCompletionRequest).choices.first().message
            ?: throw NoChoiceAvailableException()

        return Message(
            text = chatMessage.content,
            isFromUser = chatMessage.role == ChatRole.User,
            messageStatus = MessageStatus.Sent,
        )
    }

    private fun Conversation.toChatMessages(): List<ChatMessage> = this.list
        .filterNot { it.messageStatus == MessageStatus.Error }
        .map {
            ChatMessage(
                content = it.text,
                role = if (it.isFromUser) {
                    ChatRole.User
                } else {
                    ChatRole.Assistant
                },
            )
        }
}

class NoChoiceAvailableException : Exception()
