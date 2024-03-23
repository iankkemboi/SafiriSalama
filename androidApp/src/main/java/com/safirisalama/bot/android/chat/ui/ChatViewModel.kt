package com.safirisalama.bot.android.chat.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.safirisalama.bot.Amadeus
import com.safirisalama.bot.android.chat.data.Conversation
import com.safirisalama.bot.android.chat.data.Message
import com.safirisalama.bot.android.chat.data.MessageStatus
import com.safirisalama.bot.android.chat.domain.usecase.ObserveMessagesUseCase
import com.safirisalama.bot.android.chat.domain.usecase.ResendMessageUseCase
import com.safirisalama.bot.android.chat.domain.usecase.SendChatRequestUseCase
import com.safirisalama.bot.models.Data
import kotlinx.coroutines.launch

class ChatViewModel(
    private val sendChatRequestUseCase: SendChatRequestUseCase,
    private val resendChatRequestUseCase: ResendMessageUseCase,
    private val observeMessagesUseCase: ObserveMessagesUseCase,
) : ViewModel() {

    private val _conversation = MutableLiveData<Conversation>()
    val conversation: LiveData<Conversation> = _conversation

    private val _isSendingMessage = MutableLiveData<Boolean>()
    val isSendingMessage: LiveData<Boolean> = _isSendingMessage

    private val _destination = MutableLiveData<String>()
    val destination: LiveData<String> = _destination

    val _activitiesList = MutableLiveData<List<String>>()
    private val activitiesList = ArrayList<String>()

    private val _flightList = MutableLiveData<List<Data>?>()
    var flightList: MutableLiveData<List<Data>?> = _flightList

    init {
        observeMessageList()
    }

    private fun observeMessageList() {
        viewModelScope.launch {
            observeMessagesUseCase.invoke().collect { conversation ->
                _conversation.postValue(conversation)
                if (conversation.list.any { it.text.contains("Sure,let me look below for flight prices") }) {
                    getFlights()
                }

                _isSendingMessage.postValue(
                    conversation.list.any { it.messageStatus == MessageStatus.Sending },
                )
            }
        }
    }

    fun sendMessage(prompt: String) {
        viewModelScope.launch {
            sendChatRequestUseCase(
                prompt,
            )
        }
    }

    fun setDestination(prompt: String) {
        viewModelScope.launch {
            _destination.postValue(prompt)
        }
    }

    fun setActivities(prompt: String) {
        viewModelScope.launch {
            activitiesList.add(prompt)
            _activitiesList.postValue(activitiesList)
        }
    }

    private fun getFlights() {
        viewModelScope.launch {
            val res = Amadeus().getFlightPrices(getDestinationCode())

            _flightList.postValue(res.first)
        }
    }

    fun generateDestinationsString(): String {
        if (activitiesList.isEmpty()) return ""

        // Join all activities with a comma and prepend the description
        val activities = activitiesList.joinToString(separator = ",")
        return ",with the following activities: ${activities.lowercase()}"
    }

    fun showDestinations(): List<String> {
        val destinations =
            mutableListOf("North Africa", "West Africa", "East Africa", "Southern Africa")
        setDestination(destinations[0])
        return destinations
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

    private fun getDestinationName(): String {
        return when (destination.value) {
            "North Africa" -> "Cairo"
            "West Africa" -> "Lagos"
            "East Africa" -> "Nairobi"
            "Southern Africa" -> "Johannesburg"
            else -> "Nairobi"
        }
    }

    fun resendMessage(message: Message) {
        viewModelScope.launch {
            resendChatRequestUseCase(
                message,
            )
        }
    }
}
