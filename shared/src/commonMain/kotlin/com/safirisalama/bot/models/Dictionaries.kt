package com.safirisalama.bot.models

import kotlinx.serialization.SerialName

data class Dictionaries(

    @SerialName("locations") var locations: com.safirisalama.bot.models.Locations? = com.safirisalama.bot.models.Locations(),
    @SerialName("aircraft") var aircraft: com.safirisalama.bot.models.Aircraft? = com.safirisalama.bot.models.Aircraft(),
    @SerialName("currencies") var currencies: com.safirisalama.bot.models.Currencies? = com.safirisalama.bot.models.Currencies(),
    @SerialName("carriers") var carriers: com.safirisalama.bot.models.Carriers? = com.safirisalama.bot.models.Carriers(),

    )
