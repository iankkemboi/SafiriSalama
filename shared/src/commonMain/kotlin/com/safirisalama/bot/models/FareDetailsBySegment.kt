package com.safirisalama.bot.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FareDetailsBySegment(

    @SerialName("segmentId") var segmentId: String? = null,
    @SerialName("cabin") var cabin: String? = null,
    @SerialName("fareBasis") var fareBasis: String? = null,
    @SerialName("brandedFare") var brandedFare: String? = null,
    @SerialName("brandedFareLabel") var brandedFareLabel: String? = null,
    @SerialName("includedCheckedBags") var includedCheckedBags: com.safirisalama.bot.models.IncludedCheckedBags? = com.safirisalama.bot.models.IncludedCheckedBags(),
    @SerialName("amenities") var amenities: ArrayList<com.safirisalama.bot.models.Amenities> = arrayListOf(),

)
