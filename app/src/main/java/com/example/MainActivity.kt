package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.model.CurrentScreen
import com.example.ui.dialogs.AlternativeAuthDialog
import com.example.ui.dialogs.ServerSettingsDialog
import com.example.ui.dialogs.SupportDialog
import com.example.ui.dialogs.TermsDialog
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.VerificationWaitScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding()
                ) {
                    val uiState by viewModel.uiState.collectAsState()

                    // Main App Navigation between screens
                    Crossfade(
                        targetState = uiState.currentScreen,
                        animationSpec = tween(220),
                        label = "screen_crossfade"
                    ) { screen ->
                        when (screen) {
                            CurrentScreen.ONBOARDING -> {
                                OnboardingScreen(viewModel = viewModel, uiState = uiState)
                            }
                            CurrentScreen.LOGIN -> {
                                LoginScreen(viewModel = viewModel, uiState = uiState)
                            }
                            CurrentScreen.WAITING_VERIFICATION -> {
                                VerificationWaitScreen(viewModel = viewModel, uiState = uiState)
                            }
                            CurrentScreen.DASHBOARD_SUCCESS -> {
                                DashboardScreen(viewModel = viewModel, uiState = uiState)
                            }
                        }
                    }

                    // Dialogs
                    if (uiState.showSupportDialog) {
                        SupportDialog(
                            lang = uiState.currentLanguage,
                            onDismiss = { viewModel.setShowSupportDialog(false) }
                        )
                    }

                    if (uiState.showTermsDialog) {
                        TermsDialog(
                            isPrivacyPolicy = false,
                            lang = uiState.currentLanguage,
                            onDismiss = { viewModel.setShowTermsDialog(false) }
                        )
                    }

                    if (uiState.showPrivacyDialog) {
                        TermsDialog(
                            isPrivacyPolicy = true,
                            lang = uiState.currentLanguage,
                            onDismiss = { viewModel.setShowPrivacyDialog(false) }
                        )
                    }

                    if (uiState.showServerSettingsDialog) {
                        ServerSettingsDialog(
                            currentUrl = uiState.serverUrl,
                            isConnected = uiState.isConnectedToBackend,
                            sessionId = uiState.sessionId,
                            lang = uiState.currentLanguage,
                            onSaveUrl = { viewModel.updateServerUrl(it) },
                            onTestConnection = { viewModel.testServerConnection() },
                            onDismiss = { viewModel.setShowServerSettingsDialog(false) }
                        )
                    }

                    if (uiState.showMaxAuthDialog) {
                        AlternativeAuthDialog(
                            isTelegram = false,
                            lang = uiState.currentLanguage,
                            onConfirm = {
                                viewModel.setShowMaxAuthDialog(false)
                                // Pre-fill sample verified MAX number and transition
                                viewModel.onPhoneChanged("900123456")
                            },
                            onDismiss = { viewModel.setShowMaxAuthDialog(false) }
                        )
                    }

                    if (uiState.showTelegramAuthDialog) {
                        AlternativeAuthDialog(
                            isTelegram = true,
                            lang = uiState.currentLanguage,
                            onConfirm = {
                                viewModel.setShowTelegramAuthDialog(false)
                                // Pre-fill sample verified Telegram number and transition
                                viewModel.onPhoneChanged("927112233")
                            },
                            onDismiss = { viewModel.setShowTelegramAuthDialog(false) }
                        )
                    }
                }
            }
        }
    }
}
