package com.safirisalama.bot

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform