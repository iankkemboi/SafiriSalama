package com.safirisalama.bot.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Segments(

    @SerialName("departure") var departure: com.safirisalama.bot.models.Departure? = com.safirisalama.bot.models.Departure(),
    @SerialName("arrival") var arrival: Arrival? = Arrival(),
    @SerialName("carrierCode") var carrierCode: String? = null,
    @SerialName("number") var number: String? = null,
    @SerialName("aircraft") var aircraft: com.safirisalama.bot.models.Aircraft? = com.safirisalama.bot.models.Aircraft(),
    @SerialName("operating") var operating: com.safirisalama.bot.models.Operating? = com.safirisalama.bot.models.Operating(),
    @SerialName("duration") var duration: String? = null,
    @SerialName("id") var id: String? = null,
    @SerialName("numberOfStops") var numberOfStops: Int? = null,
    @SerialName("blacklistedInEU") var blacklistedInEU: Boolean? = null,

)
