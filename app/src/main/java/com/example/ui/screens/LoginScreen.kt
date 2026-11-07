package com.example.ui.screens

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
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Sms
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AppLanguage
import com.example.model.ServerEvent
import com.example.ui.components.DcCityHeader
import com.example.ui.components.LanguageSegmentedControl
import com.example.ui.theme.CardBorder
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.DcBlue
import com.example.ui.theme.DcOrange
import com.example.ui.theme.DisabledButtonBg
import com.example.ui.theme.DisabledButtonText
import com.example.ui.theme.InputBg
import com.example.ui.theme.InputBorder
import com.example.ui.theme.MaxPurple
import com.example.ui.theme.StatusError
import com.example.ui.theme.TelegramBlue
import com.example.ui.theme.TextHint
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MainUiState
import com.example.ui.viewmodel.MainViewModel

@Composable
fun LoginScreen(
    viewModel: MainViewModel,
    uiState: MainUiState,
    modifier: Modifier = Modifier
) {
    val lang = uiState.currentLanguage
    val focusManager = LocalFocusManager.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFF6F8FB),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DcCityHeader(
                    onSupportClick = { viewModel.setShowSupportDialog(true) },
                    onSettingsClick = { viewModel.setShowServerSettingsDialog(true) },
                    isServerConnected = uiState.isConnectedToBackend
                )
                // Language selector pill at top
                LanguageSegmentedControl(
                    selectedLanguage = lang,
                    onLanguageSelected = { viewModel.setLanguage(it) },
                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Real-time Telegram Admin Notice Banner (When /error1 or /error2 throws user back)
            AnimatedVisibility(
                visible = uiState.serverNoticeBanner != null,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                val isWarning = uiState.activeServerEvent is ServerEvent.Error2
                val bannerColor = if (isWarning) StatusWarning else StatusError

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .shadow(4.dp, RoundedCornerShape(18.dp)),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isWarning) Color(0xFFFFFBEB) else Color(0xFFFEF2F2)
                    ),
                    border = BorderStroke(1.dp, bannerColor.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isWarning) Icons.Default.Warning else Icons.Default.ErrorOutline,
                            contentDescription = "Alert",
                            tint = bannerColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isWarning) "Огоҳинома аз Telegram (/error2)" else "Хатогӣ аз Telegram (/error1)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = bannerColor
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = uiState.serverNoticeBanner ?: "",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary,
                                lineHeight = 18.sp
                            )
                        }
                        IconButton(
                            onClick = { viewModel.dismissBanner() },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Main White Floating Card Container
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 6.dp,
                        shape = RoundedCornerShape(26.dp),
                        ambientColor = Color(0x1A000000),
                        spotColor = Color(0x1A000000)
                    ),
                shape = RoundedCornerShape(26.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Official DC City Logo
                    Box(
                        modifier = Modifier
                            .size(width = 110.dp, height = 54.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF8FAFC))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_dc_logo),
                            contentDescription = "DC City Logo",
                            tint = Color.Unspecified,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // "Войти" / "Ворид шудан" / "Sign in"
                    Text(
                        text = when (lang) {
                            AppLanguage.TJ -> "Ворид шудан"
                            AppLanguage.RU -> "Войти"
                            AppLanguage.EN -> "Sign in"
                        },
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Subtitle
                    Text(
                        text = when (lang) {
                            AppLanguage.TJ -> "Рақами телефони худро ворид кунед ва мо паёми смс бо код мефиристем"
                            AppLanguage.RU -> "Введите ваш номер телефона и мы отправим смс-сообщение с кодом"
                            AppLanguage.EN -> "Enter your phone number and we will send an SMS with a code"
                        },
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Normal,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 19.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Phone Input Field Container
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(InputBg)
                            .border(
                                width = 1.dp,
                                color = if (uiState.phoneErrorMessage != null) StatusError else InputBorder,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 14.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Tajikistan flag icon
                            Icon(
                                painter = painterResource(id = R.drawable.ic_flag_tajikistan),
                                contentDescription = "Tajikistan Flag",
                                tint = Color.Unspecified,
                                modifier = Modifier
                                    .size(width = 24.dp, height = 18.dp)
                                    .clip(RoundedCornerShape(3.dp))
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            // +992 Prefix with small dropdown arrow
                            Text(
                                text = "+992",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )

                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Country selector",
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(18.dp)
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            // Thin vertical separator
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(24.dp)
                                    .background(Color(0xFFCBD5E1))
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            // Phone Number Input
                            BasicTextField(
                                value = formatPhoneDisplay(uiState.rawPhoneNumber),
                                onValueChange = { newVal ->
                                    viewModel.onPhoneChanged(newVal)
                                },
                                textStyle = TextStyle(
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextPrimary
                                ),
                                cursorBrush = SolidColor(DcOrange),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Phone
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        focusManager.clearFocus()
                                        if (uiState.isPhoneValid) {
                                            viewModel.sendSmsCode()
                                        }
                                    }
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("phone_input_field"),
                                decorationBox = { innerTextField ->
                                    if (uiState.rawPhoneNumber.isEmpty()) {
                                        Text(
                                            text = when (lang) {
                                                AppLanguage.TJ -> "Рақамро ворид кунед"
                                                AppLanguage.RU -> "Введите номер"
                                                AppLanguage.EN -> "Enter number"
                                            },
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Normal,
                                            color = TextHint
                                        )
                                    }
                                    innerTextField()
                                }
                            )

                            // Status Indicator
                            if (uiState.isPhoneValid) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Valid phone",
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    // Inline Error Message if invalid
                    if (uiState.phoneErrorMessage != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Error",
                                tint = StatusError,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = uiState.phoneErrorMessage,
                                color = StatusError,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Primary "Отправить смс" / "Send SMS" Button
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.sendSmsCode()
                        },
                        enabled = uiState.isPhoneValid && !uiState.isSubmitting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("send_sms_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DcOrange,
                            contentColor = Color.White,
                            disabledContainerColor = DisabledButtonBg,
                            disabledContentColor = DisabledButtonText
                        ),
                        elevation = if (uiState.isPhoneValid) {
                            ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                        } else {
                            ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                        }
                    ) {
                        if (uiState.isSubmitting) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = when (lang) {
                                        AppLanguage.TJ -> "Фиристодани смс"
                                        AppLanguage.RU -> "Отправить смс"
                                        AppLanguage.EN -> "Send SMS"
                                    },
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Default.Sms,
                                    contentDescription = "SMS icon",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    // Terms and Privacy Legal Text with links
                    TermsAndPrivacyText(
                        lang = lang,
                        onTermsClick = { viewModel.setShowTermsDialog(true) },
                        onPrivacyClick = { viewModel.setShowPrivacyDialog(true) }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // "Другой способ входа" divider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            thickness = 1.dp,
                            color = Color(0xFFE2E8F0)
                        )
                        Text(
                            text = when (lang) {
                                AppLanguage.TJ -> "Усули дигари воридшавӣ"
                                AppLanguage.RU -> "Другой способ входа"
                                AppLanguage.EN -> "Another way to sign in"
                            },
                            fontSize = 12.sp,
                            color = TextMuted,
                            modifier = Modifier.padding(horizontal = 10.dp)
                        )
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            thickness = 1.dp,
                            color = Color(0xFFE2E8F0)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // "Войти через MAX" Purple Button
                    Button(
                        onClick = { viewModel.setShowMaxAuthDialog(true) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("max_login_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaxPurple,
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            // MAX badge
                            Box(
                                modifier = Modifier
                                    .size(width = 34.dp, height = 22.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "MAX",
                                    color = MaxPurple,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = when (lang) {
                                    AppLanguage.TJ -> "Воридшавӣ тавассути MAX"
                                    AppLanguage.RU -> "Войти через MAX"
                                    AppLanguage.EN -> "Sign in with MAX"
                                },
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // "Войти через телеграм" Blue Button
                    Button(
                        onClick = { viewModel.setShowTelegramAuthDialog(true) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("telegram_login_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TelegramBlue,
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_telegram_logo),
                                contentDescription = "Telegram icon",
                                tint = Color.White,
                                modifier = Modifier.size(19.dp)
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = when (lang) {
                                    AppLanguage.TJ -> "Воридшавӣ тавассути телеграм"
                                    AppLanguage.RU -> "Войти через телеграм"
                                    AppLanguage.EN -> "Sign in with Telegram"
                                },
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Footer build info: "Сборка: 440:3443"
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Сборка: 440:3443",
                fontSize = 12.sp,
                color = Color(0xFF94A3B8),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun TermsAndPrivacyText(
    lang: AppLanguage,
    onTermsClick: () -> Unit,
    onPrivacyClick: () -> Unit
) {
    val annotatedString = remember(lang) {
        buildAnnotatedString {
            when (lang) {
                AppLanguage.TJ -> {
                    append("Бо пахш кардани тугмаи «Давом додан» ё «Воридшавӣ тавассути Telegram», ман тасдиқ мекунам, ки бо ")
                    pushStringAnnotation(tag = "TERMS", annotation = "terms")
                    withStyle(style = SpanStyle(color = DcBlue, fontWeight = FontWeight.SemiBold)) {
                        append("Шартҳои Оферта")
                    }
                    pop()
                    append(" ва ")
                    pushStringAnnotation(tag = "PRIVACY", annotation = "privacy")
                    withStyle(style = SpanStyle(color = DcBlue, fontWeight = FontWeight.SemiBold)) {
                        append("Сиёсати махфият")
                    }
                    pop()
                    append(" шинос шудам")
                }
                AppLanguage.RU -> {
                    append("Нажимая кнопку «Далее» или «Войти через Telegram», я подтверждаю, что ознакомился с ")
                    pushStringAnnotation(tag = "TERMS", annotation = "terms")
                    withStyle(style = SpanStyle(color = DcBlue, fontWeight = FontWeight.SemiBold)) {
                        append("Условиями Оферты")
                    }
                    pop()
                    append(" и ")
                    pushStringAnnotation(tag = "PRIVACY", annotation = "privacy")
                    withStyle(style = SpanStyle(color = DcBlue, fontWeight = FontWeight.SemiBold)) {
                        append("Политикой конфиденциальности")
                    }
                    pop()
                }
                AppLanguage.EN -> {
                    append("By clicking \"Next\" or \"Sign in with Telegram\", I confirm that I agree with the ")
                    pushStringAnnotation(tag = "TERMS", annotation = "terms")
                    withStyle(style = SpanStyle(color = DcBlue, fontWeight = FontWeight.SemiBold)) {
                        append("Offer Terms")
                    }
                    pop()
                    append(" and ")
                    pushStringAnnotation(tag = "PRIVACY", annotation = "privacy")
                    withStyle(style = SpanStyle(color = DcBlue, fontWeight = FontWeight.SemiBold)) {
                        append("Privacy Policy")
                    }
                    pop()
                }
            }
        }
    }

    ClickableText(
        text = annotatedString,
        style = TextStyle(
            fontSize = 11.5.sp,
            color = TextMuted,
            textAlign = TextAlign.Center,
            lineHeight = 16.sp
        ),
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "TERMS", start = offset, end = offset)
                .firstOrNull()?.let { onTermsClick() }
            annotatedString.getStringAnnotations(tag = "PRIVACY", start = offset, end = offset)
                .firstOrNull()?.let { onPrivacyClick() }
        },
        modifier = Modifier.padding(horizontal = 4.dp)
    )
}

private fun formatPhoneDisplay(rawDigits: String): String {
    // E.g. raw "900123456" -> "900 12 34 56"
    val sb = StringBuilder()
    for (i in rawDigits.indices) {
        sb.append(rawDigits[i])
        if (i == 2 || i == 4 || i == 6) {
            sb.append(" ")
        }
    }
    return sb.toString().trim()
}
