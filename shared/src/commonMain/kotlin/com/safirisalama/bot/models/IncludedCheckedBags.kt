package com.safirisalama.bot.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IncludedCheckedBags(

    @SerialName("quantity") var quantity: Int? = null,

)
