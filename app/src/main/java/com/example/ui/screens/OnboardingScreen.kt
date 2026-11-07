package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.ui.components.DcCityHeader
import com.example.ui.components.LanguageSegmentedControl
import com.example.ui.components.SlideIllustration1
import com.example.ui.components.SlideIllustration2
import com.example.ui.components.SlideIllustration3
import com.example.ui.theme.DcOrange
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MainUiState
import com.example.ui.viewmodel.MainViewModel

@Composable
fun OnboardingScreen(
    viewModel: MainViewModel,
    uiState: MainUiState,
    modifier: Modifier = Modifier
) {
    val lang = uiState.currentLanguage
    val slide = uiState.onboardingIndex

    var dragOffset by remember { mutableFloatStateOf(0f) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(slide) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (dragOffset < -50f) {
                            viewModel.nextOnboarding()
                        } else if (dragOffset > 50f && slide > 0) {
                            viewModel.setOnboardingIndex(slide - 1)
                        }
                        dragOffset = 0f
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        dragOffset += dragAmount
                    }
                )
            },
        containerColor = Color.White,
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

                Spacer(modifier = Modifier.height(10.dp))

                LanguageSegmentedControl(
                    selectedLanguage = lang,
                    onLanguageSelected = { viewModel.setLanguage(it) },
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Animated Illustration & Copy Content
            AnimatedContent(
                targetState = slide,
                transitionSpec = {
                    fadeIn(animationSpec = tween(240)) togetherWith fadeOut(animationSpec = tween(200))
                },
                modifier = Modifier.weight(1f),
                label = "onboarding_content"
            ) { targetSlide ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Illustration area
                    Box(
                        modifier = Modifier
                            .size(260.dp)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        when (targetSlide) {
                            0 -> SlideIllustration1(modifier = Modifier.fillMaxSize())
                            1 -> SlideIllustration2(modifier = Modifier.fillMaxSize())
                            else -> SlideIllustration3(modifier = Modifier.fillMaxSize())
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Title
                    Text(
                        text = getSlideTitle(targetSlide, lang),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        textAlign = TextAlign.Center,
                        lineHeight = 30.sp,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Subtitle
                    Text(
                        text = getSlideSubtitle(targetSlide, lang),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 21.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            // Bottom Section: Dots indicator & Primary Action Button
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 36.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 3 Pager Dots
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(3) { index ->
                        val isCurrent = index == slide
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .height(6.dp)
                                .width(if (isCurrent) 22.dp else 6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (isCurrent) DcOrange else Color(0xFFD1D5DB))
                                .clickable { viewModel.setOnboardingIndex(index) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Primary Next / Continue Button
                Button(
                    onClick = { viewModel.nextOnboarding() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("onboarding_next_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DcOrange,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = getSlideButtonText(slide, lang),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

private fun getSlideTitle(slide: Int, lang: AppLanguage): String {
    return when (slide) {
        0 -> when (lang) {
            AppLanguage.EN -> "A full bank in your phone"
            AppLanguage.RU -> "Целый банк в вашем телефоне"
            AppLanguage.TJ -> "Як бонки комил дар телефони шумо"
        }
        1 -> when (lang) {
            AppLanguage.EN -> "Access your funds 24/7"
            AppLanguage.RU -> "Получите доступ к средствам 24/7"
            AppLanguage.TJ -> "Ба маблағҳои худ 24/7 дастрасӣ"
        }
        else -> when (lang) {
            AppLanguage.EN -> "Money transfers from Russia"
            AppLanguage.RU -> "Денежные переводы из России"
            AppLanguage.TJ -> "Интиқоли маблағ аз Русия"
        }
    }
}

private fun getSlideSubtitle(slide: Int, lang: AppLanguage): String {
    return when (slide) {
        0 -> when (lang) {
            AppLanguage.EN -> "Over 1000 services in one app"
            AppLanguage.RU -> "Более 1000 услуг в одном приложении"
            AppLanguage.TJ -> "Зиёда аз 1000 хизматрасонӣ дар як барнома"
        }
        1 -> when (lang) {
            AppLanguage.EN -> "Modern encryption fully protects your funds and all transactions"
            AppLanguage.RU -> "Современная система шифрования полностью обеспечивает защиту ваших средств и совершаемых операций"
            AppLanguage.TJ -> "Системаи муосири рамзгузорӣ маблағҳо ва тамоми амалиётҳои шуморо пурра муҳофизат мекунад"
        }
        else -> when (lang) {
            AppLanguage.EN -> "Link Russian VISA, Mastercard and MIR cards. Send by card number or phone number easily"
            AppLanguage.RU -> "Привязывайте российские карты «VISA», «Mastercard» и «МИР». Переводите по номеру карты или по номеру телефона, что очень удобно"
            AppLanguage.TJ -> "Кортҳои русии «VISA», «Mastercard» ва «МИР»-ро пайваст кунед. Бо рақами корт ё рақами телефон ба осонӣ интиқол диҳед"
        }
    }
}

private fun getSlideButtonText(slide: Int, lang: AppLanguage): String {
    return when (slide) {
        0 -> when (lang) {
            AppLanguage.EN -> "Next"
            AppLanguage.RU -> "Далее"
            AppLanguage.TJ -> "Давом додан"
        }
        1 -> when (lang) {
            AppLanguage.EN -> "Continue"
            AppLanguage.RU -> "Продолжить"
            AppLanguage.TJ -> "Давом додан"
        }
        else -> when (lang) {
            AppLanguage.EN -> "Next"
            AppLanguage.RU -> "Далее"
            AppLanguage.TJ -> "Давом додан"
        }
    }
}
