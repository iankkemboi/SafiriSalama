package com.safirisalama.bot.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Aircraft(

    @SerialName("code") var code: String? = null,

)
