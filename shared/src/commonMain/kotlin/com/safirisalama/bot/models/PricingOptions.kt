package com.safirisalama.bot.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PricingOptions(

    @SerialName("fareType") var fareType: ArrayList<String> = arrayListOf(),
    @SerialName("includedCheckedBagsOnly") var includedCheckedBagsOnly: Boolean? = null,

)
