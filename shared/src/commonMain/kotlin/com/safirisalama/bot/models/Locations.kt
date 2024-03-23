package com.safirisalama.bot.models

import kotlinx.serialization.SerialName

data class Locations(

    @SerialName("LOS") var LOS: com.safirisalama.bot.models.LOS? = com.safirisalama.bot.models.LOS(),
    @SerialName("ZRH") var ZRH: com.safirisalama.bot.models.ZRH? = com.safirisalama.bot.models.ZRH(),
    @SerialName("EWR") var EWR: com.safirisalama.bot.models.EWR? = com.safirisalama.bot.models.EWR(),
    @SerialName("LCY") var LCY: com.safirisalama.bot.models.LCY? = com.safirisalama.bot.models.LCY(),
    @SerialName("DCA") var DCA: com.safirisalama.bot.models.DCA? = com.safirisalama.bot.models.DCA(),
    @SerialName("FRA") var FRA: com.safirisalama.bot.models.FRA? = com.safirisalama.bot.models.FRA(),
    @SerialName("LHR") var LHR: com.safirisalama.bot.models.LHR? = com.safirisalama.bot.models.LHR(),
    @SerialName("CDG") var CDG: com.safirisalama.bot.models.CDG? = com.safirisalama.bot.models.CDG(),

    @SerialName("IAD") var IAD: com.safirisalama.bot.models.IAD? = com.safirisalama.bot.models.IAD(),

    )
