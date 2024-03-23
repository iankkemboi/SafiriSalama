package com.safirisalama.bot.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Amenities(

    @SerialName("description") var description: String? = null,
    @SerialName("isChargeable") var isChargeable: Boolean? = null,
    @SerialName("amenityType") var amenityType: String? = null,
    @SerialName("amenityProvider") var amenityProvider: com.safirisalama.bot.models.AmenityProvider? = com.safirisalama.bot.models.AmenityProvider(),

    )
