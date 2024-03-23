package com.safirisalama.bot

import kotlinx.serialization.SerialName

data class AMS(

    @SerialName("cityCode") var cityCode: String? = null,
    @SerialName("countryCode") var countryCode: String? = null,

)
