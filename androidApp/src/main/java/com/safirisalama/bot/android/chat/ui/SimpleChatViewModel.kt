package com.safirisalama.bot.android.chat.ui


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.compose.runtime.mutableStateOf
import com.safirisalama.bot.android.chat.data.api.ApiService
import com.safirisalama.bot.android.chat.data.api.ChatRequest
import com.safirisalama.bot.android.chat.data.api.ChatResponse
import com.safirisalama.bot.android.chat.data.api.RetrofitClient

data class Message(val text: String, val isUser: Boolean, val suggestions: List<String> = emptyList())


class SimpleChatViewModel : ViewModel() {

    var chatHistory = mutableStateOf(listOf<Message>())
    var suggestedPrompts = mutableStateOf(listOf<String>())

    private val apiService: ApiService = RetrofitClient.apiService

    fun sendMessage(message: String) {
        // Add user message to chat history
        val userMessage = Message(message, true)
        chatHistory.value = chatHistory.value + userMessage

        val request = ChatRequest(message)
        viewModelScope.launch {
            apiService.sendMessage(request).enqueue(object : Callback<ChatResponse> {
                override fun onResponse(call: Call<ChatResponse>, response: Response<ChatResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            val botMessage = Message(it.chatgpt_response, false, it.suggested_prompts)
                            chatHistory.value = chatHistory.value + botMessage
                        }
                    }
                }

                override fun onFailure(call: Call<ChatResponse>, t: Throwable) {
                    // Handle error
                }
            })
        }
    }
}
