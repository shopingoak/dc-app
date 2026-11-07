package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AppLanguage
import com.example.ui.theme.DcBlue
import com.example.ui.theme.DcOrange
import com.example.ui.theme.DisabledButtonBg
import com.example.ui.theme.DisabledButtonText
import com.example.ui.theme.InputBg
import com.example.ui.theme.InputBorder
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.TextHint
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MainUiState
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.delay

@Composable
fun VerificationWaitScreen(
    viewModel: MainViewModel,
    uiState: MainUiState,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.backToLogin()
    }

    val lang = uiState.currentLanguage
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val clipboardManager = LocalClipboardManager.current
    var copiedToast by remember { mutableStateOf(false) }

    // Resend countdown timer
    var countdown by remember { mutableIntStateOf(58) }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        while (countdown > 0) {
            delay(1000L)
            countdown--
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFF7F9FC),
        topBar = {
            // Header: < Back Arrow | "Тасдиқи шахсият" | Support icon
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                IconButton(
                    onClick = { viewModel.backToLogin() },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .testTag("back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }

                Text(
                    text = when (lang) {
                        AppLanguage.TJ -> "Тасдиқи шахсият"
                        AppLanguage.RU -> "Подтверждение личности"
                        AppLanguage.EN -> "Identity Verification"
                    },
                    fontSize = 17.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.align(Alignment.Center)
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable { viewModel.setShowSupportDialog(true) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_support_headset),
                        contentDescription = "Support",
                        tint = Color(0xFF475569),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(14.dp))

            // Main Card matching user's photo WIN_20261107_17_14_51_Pro_93c262e0.jpg
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 6.dp, shape = RoundedCornerShape(26.dp)),
                shape = RoundedCornerShape(26.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Soft circular badge with orange message/SMS icon
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFEDE0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sms,
                            contentDescription = "SMS icon",
                            tint = DcOrange,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Title: "Рамзи SMS-ро ворид кунед"
                    Text(
                        text = when (lang) {
                            AppLanguage.TJ -> "Рамзи SMS-ро ворид кунед"
                            AppLanguage.RU -> "Введите код из SMS"
                            AppLanguage.EN -> "Enter SMS Code"
                        },
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Subtext: "Рамз ба рақам фиристода шуд"
                    Text(
                        text = when (lang) {
                            AppLanguage.TJ -> "Рамз ба рақам фиристода шуд"
                            AppLanguage.RU -> "Код отправлен на номер"
                            AppLanguage.EN -> "Code sent to number"
                        },
                        fontSize = 13.5.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Bold phone number (e.g. +992074372299 or +992 900123456)
                    Text(
                        text = "+992" + uiState.rawPhoneNumber,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // 4-Digit OTP Code Input Cells
                    OtpInputRow(
                        code = uiState.smsCode,
                        onCodeChanged = { viewModel.onSmsCodeChanged(it) },
                        focusRequester = focusRequester,
                        onDone = {
                            focusManager.clearFocus()
                            viewModel.submitSmsCode()
                        }
                    )

                    Spacer(modifier = Modifier.height(26.dp))

                    // Orange Primary Button: "Давом додан ✔"
                    val isCodeReady = uiState.smsCode.length >= 4
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.submitSmsCode()
                        },
                        enabled = isCodeReady,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("confirm_sms_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DcOrange,
                            contentColor = Color.White,
                            disabledContainerColor = DisabledButtonBg,
                            disabledContentColor = DisabledButtonText
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = if (isCodeReady) 2.dp else 0.dp
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = when (lang) {
                                    AppLanguage.TJ -> "Давом додан"
                                    AppLanguage.RU -> "Продолжить"
                                    AppLanguage.EN -> "Continue"
                                },
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Check",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Resend countdown timer text
                    if (countdown > 0) {
                        val formattedSeconds = if (countdown < 10) "0$countdown" else "$countdown"
                        Text(
                            text = when (lang) {
                                AppLanguage.TJ -> "Рамзро нагирифтед? Аз нав фиристодан (0:$formattedSeconds)"
                                AppLanguage.RU -> "Не получили код? Отправить снова (0:$formattedSeconds)"
                                AppLanguage.EN -> "Didn't receive code? Resend (0:$formattedSeconds)"
                            },
                            fontSize = 12.5.sp,
                            color = TextMuted,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Text(
                            text = when (lang) {
                                AppLanguage.TJ -> "Аз нав фиристодани рамз"
                                AppLanguage.RU -> "Отправить код повторно"
                                AppLanguage.EN -> "Resend code"
                            },
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DcBlue,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.clickable {
                                countdown = 59
                                viewModel.sendSmsCode()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun OtpInputRow(
    code: String,
    onCodeChanged: (String) -> Unit,
    focusRequester: FocusRequester,
    onDone: () -> Unit
) {
    // Hidden basic text field that captures keyboard inputs
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = code,
            onValueChange = { newVal ->
                if (newVal.length <= 4) {
                    onCodeChanged(newVal)
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            keyboardActions = KeyboardActions(onDone = { onDone() }),
            cursorBrush = SolidColor(Color.Transparent),
            modifier = Modifier
                .focusRequester(focusRequester)
                .size(1.dp)
                .testTag("otp_hidden_input")
        )

        // Visual 4-cell boxes
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(4) { index ->
                val digit = if (index < code.length) code[index].toString() else ""
                val isFocused = index == code.length

                Box(
                    modifier = Modifier
                        .size(width = 54.dp, height = 58.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isFocused) Color(0xFFFFF7F2) else InputBg)
                        .border(
                            width = if (isFocused) 2.dp else 1.dp,
                            color = if (isFocused) DcOrange else InputBorder,
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable { focusRequester.requestFocus() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = digit,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }
        }
    }
}
