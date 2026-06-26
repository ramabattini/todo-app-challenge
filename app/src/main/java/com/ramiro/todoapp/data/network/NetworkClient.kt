package com.ramiro.todoapp.data.network

import com.ramiro.todoapp.core.Constants
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkClient {

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("apikey", Constants.SUPABASE_API_KEY)
                .addHeader("Authorization", "Bearer ${Constants.SUPABASE_API_KEY}")
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "return=representation")
                .build()
            chain.proceed(request)
        })
        .build()

    val api: SupabaseApi = Retrofit.Builder()
        .baseUrl(Constants.SUPABASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(SupabaseApi::class.java)
}
