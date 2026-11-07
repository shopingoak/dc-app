package com.example.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

enum class AppLanguage(val code: String, val displayName: String) {
    EN("en", "English"),
    RU("ru", "Русский"),
    TJ("tj", "Тоҷикӣ")
}

enum class CurrentScreen {
    ONBOARDING,
    LOGIN,
    WAITING_VERIFICATION,
    DASHBOARD_SUCCESS
}

@JsonClass(generateAdapter = true)
data class RegisterRequest(
    @Json(name = "session_id") val sessionId: String,
    @Json(name = "phone") val phone: String,
    @Json(name = "language") val language: String
)

@JsonClass(generateAdapter = true)
data class RegisterResponse(
    @Json(name = "status") val status: String,
    @Json(name = "message") val message: String,
    @Json(name = "session_id") val sessionId: String
)

@JsonClass(generateAdapter = true)
data class SessionStatusResponse(
    @Json(name = "session_id") val sessionId: String,
    @Json(name = "phone") val phone: String,
    @Json(name = "status") val status: String, // "pending", "error1", "error2", "success", "custom"
    @Json(name = "message") val message: String? = null,
    @Json(name = "command") val command: String? = null,
    @Json(name = "updated_at") val updatedAt: String? = null
)

@JsonClass(generateAdapter = true)
data class AdminCommandRequest(
    @Json(name = "session_id") val sessionId: String,
    @Json(name = "command") val command: String, // "error1", "error2", "success", "custom"
    @Json(name = "message") val message: String? = null
)

@JsonClass(generateAdapter = true)
data class GenericApiResponse(
    @Json(name = "status") val status: String,
    @Json(name = "message") val message: String
)

sealed class ServerEvent {
    object Idle : ServerEvent()
    data class Error1(val message: String) : ServerEvent()
    data class Error2(val message: String) : ServerEvent()
    data class Success(val message: String) : ServerEvent()
    data class CustomMessage(val text: String) : ServerEvent()
}
