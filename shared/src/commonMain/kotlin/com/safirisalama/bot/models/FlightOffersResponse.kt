package com.safirisalama.bot.models

import kotlinx.serialization.SerialName

data class FlightOffersResponse(

    @SerialName("meta") var meta: com.safirisalama.bot.models.Meta? = com.safirisalama.bot.models.Meta(),
    @SerialName("data") var data: ArrayList<com.safirisalama.bot.models.Data> = arrayListOf(),
    @SerialName("dictionaries") var dictionaries: com.safirisalama.bot.models.Dictionaries? = com.safirisalama.bot.models.Dictionaries(),

)
