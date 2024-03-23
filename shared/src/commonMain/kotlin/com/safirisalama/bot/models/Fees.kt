package com.safirisalama.bot.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Fees(

    @SerialName("amount") var amount: String? = null,
    @SerialName("type") var type: String? = null,

)
