package com.safirisalama.bot.android.chat.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.safirisalama.bot.Amadeus
import com.safirisalama.bot.android.chat.data.api.ApiService
import com.safirisalama.bot.android.chat.data.api.ChatRequest
import com.safirisalama.bot.android.chat.data.api.ChatResponse
import com.safirisalama.bot.android.chat.data.api.RetrofitClient
import com.safirisalama.bot.models.Data

data class Message(
    val text: String,
    val isUser: Boolean,
    val suggestions: List<String> = emptyList()
)


class SimpleChatViewModel : ViewModel() {

    private val _chatHistory = mutableStateOf<List<SimpleChatItem>>(emptyList())
    val chatHistory: State<List<SimpleChatItem>> = _chatHistory

    var errorMessage = mutableStateOf<String?>(null)
    var isLoading = mutableStateOf(false)

    private val apiService: ApiService = RetrofitClient.apiService

    private val _destination = MutableLiveData<String>()
    val destination: LiveData<String> = _destination

    val _activitiesList = MutableLiveData<List<String>>()
    private val activitiesList = ArrayList<String>()
    private val _flightList = MutableLiveData<List<Data>?>()
    var flightList: MutableLiveData<List<Data>?> = _flightList

    init {
        val destinations = showDestinations()
        setDestination(destinations[0])
    }

    fun sendMessage(message: String) {
        val flightPrices = message.contains("flight prices")

        val userMessage = Message(message, true)
        isLoading.value = true
        _chatHistory.value += SimpleChatItem.MessageItem(userMessage)

        if (flightPrices) {
            _chatHistory.value += SimpleChatItem.MessageItem(Message("Sure, let me look below for flight prices from Berlin to  $message", false))
            getFlights()
        } else {
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
                                val botMessage = Message(it.chatgpt_response, false, it.suggested_prompts)
                                Log.v("MessageItem", botMessage.text)
                                _chatHistory.value += SimpleChatItem.MessageItem(botMessage)
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
    }

    private fun getDestinationCode(): String {
        return when (destination.value) {
            "North Africa" -> "CAI"
            "West Africa" -> "LOS"
            "East Africa" -> "NBO"
            "Southern Africa" -> "JNB"
            else -> "NBO"
        }
    }

    private fun getFlights() {
        viewModelScope.launch {
            val res = Amadeus().getFlightPrices(getDestinationCode())
            _flightList.postValue(res.first)
            // Add flight search results to chat history
            res.first.let { flights ->
                _chatHistory.value += SimpleChatItem.FlightSearch(flights)
            }
            isLoading.value = false
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
        return mutableListOf("North Africa", "West Africa", "East Africa", "Southern Africa")
    }

    fun generateDestinationsString(): String {
        if (activitiesList.isEmpty()) return ""
        val activities = activitiesList.joinToString(separator = ",")
        return ",with the following activities: ${activities.lowercase()}"
    }
}