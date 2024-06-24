package com.safirisalama.bot.android.chat.data.api

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class ChatRequest(val message: String)
data class ChatResponse(val chatgpt_response: String, val suggested_prompts: List<String>)

interface ApiService {
    @POST("api/python")
    fun sendMessage(@Body request: ChatRequest): Call<ChatResponse>
}

object RetrofitClient {
    private const val BASE_URL = "https://nextjs-fastapi-starter-alpha-virid.vercel.app/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
