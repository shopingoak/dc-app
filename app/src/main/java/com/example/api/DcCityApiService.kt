package com.example.api

import com.example.model.AdminCommandRequest
import com.example.model.GenericApiResponse
import com.example.model.RegisterRequest
import com.example.model.RegisterResponse
import com.example.model.SessionStatusResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

interface DcCityApiService {

    @POST("api/register-session")
    suspend fun registerSession(
        @Body request: RegisterRequest
    ): RegisterResponse

    @GET("api/session/{sessionId}/status")
    suspend fun getSessionStatus(
        @Path("sessionId") sessionId: String
    ): SessionStatusResponse

    @POST("api/admin/command")
    suspend fun sendAdminCommand(
        @Body request: AdminCommandRequest
    ): GenericApiResponse

    @GET("api/health")
    suspend fun checkHealth(): GenericApiResponse

    companion object {
        // Default local address for Android emulator to reach host PC: 10.0.2.2:8000
        const val DEFAULT_BASE_URL = "http://10.0.2.2:8000/"

        fun create(baseUrl: String): DcCityApiService {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build()

            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            val normalizedUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"

            return Retrofit.Builder()
                .baseUrl(normalizedUrl)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(DcCityApiService::class.java)
        }
    }
}
