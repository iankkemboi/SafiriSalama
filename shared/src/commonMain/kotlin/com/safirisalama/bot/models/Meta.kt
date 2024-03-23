package com.safirisalama.bot.models

import kotlinx.serialization.SerialName

data class Meta(

    @SerialName("count") var count: Int? = null,
    @SerialName("links") var links: com.safirisalama.bot.models.Links? = com.safirisalama.bot.models.Links(),

    )
