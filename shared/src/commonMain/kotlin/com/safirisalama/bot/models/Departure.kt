package com.safirisalama.bot.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Departure(

    @SerialName("iataCode") var iataCode: String? = null,
    @SerialName("terminal") var terminal: String? = null,
    @SerialName("at") var at: String? = null,

)
