package com.example.kotlin_login.di

import com.example.kotlin_login.data.datasource.SyncPassRemoteDataSource
import com.example.kotlin_login.data.datasource.SyncPassRemoteDataSourceImpl
import com.example.kotlin_login.data.remote.SyncPassApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Módulo de Inversión e Inyección de Dependencias de Red (NetworkModule).
 * Provee instancias únicas (@Singleton) de OkHttpClient, Retrofit, SyncPassApiService y RemoteDataSource
 * para optimizar memoria RAM y batería en el dispositivo móvil.
 */
object NetworkModule {

    const val PRODUCTION_BASE_URL = "https://sync-pass-web-rho.vercel.app/"

    private val okHttpClientInstance: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/json")
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(loggingInterceptor)
            .build()
    }

    private val retrofitInstance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(PRODUCTION_BASE_URL)
            .client(provideOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val apiServiceInstance: SyncPassApiService by lazy {
        provideRetrofit().create(SyncPassApiService::class.java)
    }

    private val remoteDataSourceInstance: SyncPassRemoteDataSource by lazy {
        SyncPassRemoteDataSourceImpl(provideSyncPassApiService())
    }

    fun provideOkHttpClient(): OkHttpClient = okHttpClientInstance

    fun provideRetrofit(): Retrofit = retrofitInstance

    fun provideSyncPassApiService(): SyncPassApiService = apiServiceInstance

    fun provideRemoteDataSource(): SyncPassRemoteDataSource = remoteDataSourceInstance
}
