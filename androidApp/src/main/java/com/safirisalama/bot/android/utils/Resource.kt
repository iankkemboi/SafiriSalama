package com.safirisalama.bot.android.utils

sealed class Resource<T> {
    data class Success<T>(val data: T) : Resource<T>()

    class Error<T>(val exception: Exception) : Resource<T>()
}
