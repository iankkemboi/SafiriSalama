package com.safirisalama.bot.android.chat.data.api

import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit


data class ChatRequest(val message: String)
data class ChatResponse(val chatgpt_response: String, val suggested_prompts: List<String>)

interface ApiService {
    @POST("api/python")
    fun sendMessage(@Body request: ChatRequest): Call<ChatResponse>
}

var client: OkHttpClient = OkHttpClient.Builder()
    .connectTimeout(25, TimeUnit.SECONDS)
    .writeTimeout(25, TimeUnit.SECONDS)
    .readTimeout(25, TimeUnit.SECONDS)
    .build()
object RetrofitClient {
    private const val BASE_URL = "https://nextjs-fastapi-starter-alpha-virid.vercel.app/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ApiService::class.java)
    }
}
