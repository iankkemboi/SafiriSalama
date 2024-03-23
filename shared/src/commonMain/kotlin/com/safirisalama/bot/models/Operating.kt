package com.safirisalama.bot.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Operating(

    @SerialName("carrierCode") var carrierCode: String? = null,

)
