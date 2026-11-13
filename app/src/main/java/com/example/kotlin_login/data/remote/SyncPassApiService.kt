package com.example.kotlin_login.data.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Contrato de cliente HTTP (Retrofit) hacia el Backend / API Gateway de SyncPass
 * conectado a PostgreSQL (Supabase).
 */
interface SyncPassApiService {

    @GET("api/talleres")
    suspend fun getTalleres(): TalleresResponseDto

    @POST("api/talleres")
    suspend fun createTaller(@Body request: CreateTallerRequestDto): TalleresResponseDto

    @DELETE("api/talleres")
    suspend fun deleteTaller(@Query("id") id: String): TalleresResponseDto

    @GET("api/asistencias")
    suspend fun getAsistencias(): AsistenciasResponseDto

    @POST("api/asistencias")
    suspend fun syncAsistencias(@Body request: SyncAsistenciasRequestDto): AsistenciasResponseDto

    @POST("api/encuestas")
    suspend fun submitEncuesta(@Body request: EncuestaRequestDto): EncuestaResponseDto

    @GET("api/usuarios")
    suspend fun getUsuarios(): UsuariosResponseDto
}
