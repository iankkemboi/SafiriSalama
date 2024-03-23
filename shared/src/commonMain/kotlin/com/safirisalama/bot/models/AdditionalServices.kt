package com.safirisalama.bot.models

import kotlinx.serialization.SerialName

data class AdditionalServices(

    @SerialName("amount") var amount: String? = null,
    @SerialName("type") var type: String? = null,

)
