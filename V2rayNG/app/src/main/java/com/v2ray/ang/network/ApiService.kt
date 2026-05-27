package com.v2ray.ang.network

import retrofit2.Response
import retrofit2.http.*

data class LoginRequest(
    val phone: String,
    val password: String
)

data class RegisterRequest(
    val phone: String,
    val password: String,
    val telegram_username: String,
    val full_name: String,
    val city: String,
    val birth_date: String,
    val device_fingerprint: String
)

data class LoginResponse(
    val token: String?,
    val message: String?
)

data class RegisterResponse(
    val message: String?
)

data class MeResponse(
    val id: Int?,
    val phone: String?,
    val full_name: String?,
    val status: String?
)

interface ApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @GET("auth/me")
    suspend fun getMe(@Header("Authorization") token: String): Response<MeResponse>
}