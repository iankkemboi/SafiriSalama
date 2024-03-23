package com.safirisalama.bot.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Price(

    @SerialName("currency") var currency: String? = null,
    @SerialName("total") var total: String? = null,
    @SerialName("base") var base: String? = null,

)
