package com.example.repository

import com.example.api.DcCityApiService
import com.example.model.AdminCommandRequest
import com.example.model.AppLanguage
import com.example.model.RegisterRequest
import com.example.model.ServerEvent
import com.example.model.SessionStatusResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import java.util.UUID

class DcCityRepository {

    private var currentBaseUrl = DcCityApiService.DEFAULT_BASE_URL
    private var apiService: DcCityApiService = DcCityApiService.create(currentBaseUrl)

    private val _sessionId = MutableStateFlow(generateSessionId())
    val sessionId = _sessionId.asStateFlow()

    private val _registeredPhone = MutableStateFlow("")
    val registeredPhone = _registeredPhone.asStateFlow()

    private val _isConnected = MutableStateFlow(false)
    val isConnected = _isConnected.asStateFlow()

    fun updateBaseUrl(newUrl: String) {
        val trimmed = newUrl.trim()
        if (trimmed.isNotEmpty() && trimmed != currentBaseUrl) {
            currentBaseUrl = trimmed
            apiService = DcCityApiService.create(currentBaseUrl)
        }
    }

    fun getBaseUrl(): String = currentBaseUrl

    fun renewSession(): String {
        val newId = generateSessionId()
        _sessionId.value = newId
        return newId
    }

    private fun generateSessionId(): String {
        return "dc-" + UUID.randomUUID().toString().substring(0, 8)
    }

    suspend fun registerSession(phone: String, language: AppLanguage): Result<String> {
        return try {
            _registeredPhone.value = phone
            val response = apiService.registerSession(
                RegisterRequest(
                    sessionId = _sessionId.value,
                    phone = phone,
                    language = language.code
                )
            )
            _isConnected.value = true
            Result.success(response.message)
        } catch (e: Exception) {
            // Note: If backend is offline during testing in emulator, allow offline simulated flow
            _isConnected.value = false
            Result.failure(e)
        }
    }

    fun pollSessionEvents(pollIntervalMs: Long = 1500L): Flow<ServerEvent> = flow {
        var lastStatus = "pending"
        while (true) {
            try {
                val currentId = _sessionId.value
                val statusResp: SessionStatusResponse = apiService.getSessionStatus(currentId)
                _isConnected.value = true

                if (statusResp.status != lastStatus || statusResp.command != null) {
                    lastStatus = statusResp.status
                    when (statusResp.status) {
                        "error1" -> {
                            val msg = statusResp.message ?: "Рақами телефон нодуруст аст. Лутфан рақамро санҷед"
                            emit(ServerEvent.Error1(msg))
                        }
                        "error2" -> {
                            val msg = statusResp.message ?: "Шумо ҳоло аз аккаунти худ баромад накардаед"
                            emit(ServerEvent.Error2(msg))
                        }
                        "success" -> {
                            val msg = statusResp.message ?: "Воридшавӣ тасдиқ шуд"
                            emit(ServerEvent.Success(msg))
                        }
                        "custom" -> {
                            val msg = statusResp.message ?: "Паём аз админ"
                            emit(ServerEvent.CustomMessage(msg))
                        }
                    }
                }
            } catch (e: Exception) {
                _isConnected.value = false
            }
            delay(pollIntervalMs)
        }
    }

    suspend fun sendSimulatedCommand(command: String, message: String? = null): Boolean {
        return try {
            apiService.sendAdminCommand(
                AdminCommandRequest(
                    sessionId = _sessionId.value,
                    command = command,
                    message = message
                )
            )
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun checkHealth(): Boolean {
        return try {
            apiService.checkHealth()
            _isConnected.value = true
            true
        } catch (e: Exception) {
            _isConnected.value = false
            false
        }
    }
}
