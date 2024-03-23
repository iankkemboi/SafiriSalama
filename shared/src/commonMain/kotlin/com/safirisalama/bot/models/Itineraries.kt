package com.safirisalama.bot.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Itineraries(

    @SerialName("duration") var duration: String? = null,
    @SerialName("segments") var segments: ArrayList<Segments> = arrayListOf(),

)
