package com.safirisalama.bot.android.chat.ui


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.safirisalama.bot.android.chat.data.api.ApiService
import com.safirisalama.bot.android.chat.data.api.ChatRequest
import com.safirisalama.bot.android.chat.data.api.ChatResponse
import com.safirisalama.bot.android.chat.data.api.RetrofitClient

data class Message(
    val text: String,
    val isUser: Boolean,
    val suggestions: List<String> = emptyList()
)


class SimpleChatViewModel : ViewModel() {

    var chatHistory = mutableStateOf(listOf<Message>())
    var errorMessage = mutableStateOf<String?>(null)

    private val apiService: ApiService = RetrofitClient.apiService

    private val _destination = MutableLiveData<String>()
    val destination: LiveData<String> = _destination

    val _activitiesList = MutableLiveData<List<String>>()
    private val activitiesList = ArrayList<String>()
    var isLoading = mutableStateOf(false)
    init{
        val destinations = showDestinations()
        setDestination(destinations[0])
    }
    fun sendMessage(message: String) {
        // Add user message to chat history
        val userMessage = Message(message, true)
        isLoading.value = true
        chatHistory.value += userMessage

        val request = ChatRequest(message)
        viewModelScope.launch {
            apiService.sendMessage(request).enqueue(object : Callback<ChatResponse> {
                override fun onResponse(
                    call: Call<ChatResponse>,
                    response: Response<ChatResponse>
                ) {
                    if (response.isSuccessful) {
                        isLoading.value = false
                        response.body()?.let {
                            val botMessage =
                                Message(it.chatgpt_response, false, it.suggested_prompts)
                            Log.v("Message", botMessage.text)
                            chatHistory.value += botMessage
                        }
                    }
                }

                override fun onFailure(call: Call<ChatResponse>, t: Throwable) {
                    isLoading.value = false
                    Log.v("Failure", "Failure: ${t.message}")
                    errorMessage.value = "There was an error in the request"
                }
            })
        }
    }

    fun setDestination(prompt: String) {
        viewModelScope.launch {
            Log.v("prompt", prompt)
            _destination.postValue(prompt)
        }
    }

    fun setActivities(prompt: String) {
        viewModelScope.launch {
            activitiesList.add(prompt)
            _activitiesList.postValue(activitiesList)
        }
    }

    fun showDestinations(): List<String> {
        val destinations =
            mutableListOf("North Africa", "West Africa", "East Africa", "Southern Africa")
        return destinations
    }

    fun generateDestinationsString(): String {
        if (activitiesList.isEmpty()) return ""

        // Join all activities with a comma and prepend the description
        val activities = activitiesList.joinToString(separator = ",")
        return ",with the following activities: ${activities.lowercase()}"
    }
}
