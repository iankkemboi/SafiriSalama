package com.safirisalama.bot.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Data(

    @SerialName("type") var type: String? = null,
    @SerialName("id") var id: String? = null,
    @SerialName("source") var source: String? = null,
    @SerialName("instantTicketingRequired") var instantTicketingRequired: Boolean? = null,
    @SerialName("nonHomogeneous") var nonHomogeneous: Boolean? = null,
    @SerialName("oneWay") var oneWay: Boolean? = null,
    @SerialName("lastTicketingDate") var lastTicketingDate: String? = null,
    @SerialName("lastTicketingDateTime") var lastTicketingDateTime: String? = null,
    @SerialName("numberOfBookableSeats") var numberOfBookableSeats: Int? = null,
    @SerialName("itineraries") var itineraries: ArrayList<Itineraries> = arrayListOf(),
    @SerialName("price") var price: Price? = Price(),
    @SerialName("pricingOptions") var pricingOptions: PricingOptions? = PricingOptions(),
    @SerialName("validatingAirlineCodes") var validatingAirlineCodes: ArrayList<String> = arrayListOf(),
    @SerialName("travelerPricings") var travelerPricings: ArrayList<TravelerPricings> = arrayListOf(),

)
