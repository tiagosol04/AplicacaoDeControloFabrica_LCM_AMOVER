package com.example.aplicacaodecontrolofabrica.core.network

import com.example.aplicacaodecontrolofabrica.BuildConfig
import com.example.aplicacaodecontrolofabrica.core.auth.AuthDataStore
import com.example.aplicacaodecontrolofabrica.core.auth.AuthFailureInterceptor
import com.example.aplicacaodecontrolofabrica.core.auth.AuthHeaderInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiModule {

    fun createApiService(authDataStore: AuthDataStore): ApiService {
        return createRetrofit(authDataStore).create(ApiService::class.java)
    }

    private fun createRetrofit(authDataStore: AuthDataStore): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .client(createOkHttpClient(authDataStore))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun createOkHttpClient(authDataStore: AuthDataStore): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(AuthHeaderInterceptor(authDataStore))
            .addInterceptor(AuthFailureInterceptor(authDataStore))

        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            builder.addInterceptor(logging)
        }

        return builder.build()
    }
}