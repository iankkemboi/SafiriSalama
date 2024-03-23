package com.safirisalama.bot.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TravelerPricings(

    @SerialName("travelerId") var travelerId: String? = null,
    @SerialName("fareOption") var fareOption: String? = null,
    @SerialName("travelerType") var travelerType: String? = null,
    @SerialName("price") var price: com.safirisalama.bot.models.Price? = com.safirisalama.bot.models.Price(),
    @SerialName("fareDetailsBySegment") var fareDetailsBySegment: ArrayList<com.safirisalama.bot.models.FareDetailsBySegment> = arrayListOf(),

)
