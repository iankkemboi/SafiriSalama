package com.safirisalama.bot.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Arrival(

    @SerialName("iataCode") var iataCode: String? = null,
    @SerialName("at") var at: String? = null,

)
