package com.safirisalama.bot.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AmenityProvider(

    @SerialName("name") var name: String? = null,

)
