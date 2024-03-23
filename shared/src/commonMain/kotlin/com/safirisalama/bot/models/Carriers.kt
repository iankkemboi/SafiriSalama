package com.safirisalama.bot.models

import kotlinx.serialization.SerialName

data class Carriers(

    @SerialName("KL") var KL: String? = null,
    @SerialName("AF") var AF: String? = null,
    @SerialName("CL") var CL: String? = null,
    @SerialName("LH") var LH: String? = null,
    @SerialName("LX") var LX: String? = null,
    @SerialName("UA") var UA: String? = null,
    @SerialName("VS") var VS: String? = null,

)
