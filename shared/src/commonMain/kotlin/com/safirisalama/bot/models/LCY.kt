package com.safirisalama.bot.models

import kotlinx.serialization.SerialName

data class LCY(

    @SerialName("cityCode") var cityCode: String? = null,
    @SerialName("countryCode") var countryCode: String? = null,

)
