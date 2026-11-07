package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AppLanguage
import com.example.ui.theme.CardBorder
import com.example.ui.theme.DcBlue
import com.example.ui.theme.DcOrange
import com.example.ui.theme.InputBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

@Composable
fun DcCityHeader(
    onSupportClick: () -> Unit,
    modifier: Modifier = Modifier,
    onSettingsClick: (() -> Unit)? = null,
    isServerConnected: Boolean = false
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        // Optional Server status / Config button on top left
        if (onSettingsClick != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(InputBg)
                    .clickable { onSettingsClick() }
                    .testTag("server_status_button"),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(if (isServerConnected) Color(0xFF10B981) else Color(0xFF94A3B8))
                )
            }
        }

        // Official DC City Dushanbe Logo centered
        Icon(
            painter = painterResource(id = R.drawable.ic_dc_logo),
            contentDescription = "DC City Dushanbe Logo",
            tint = Color.Unspecified,
            modifier = Modifier
                .align(Alignment.Center)
                .height(34.dp)
                .width(82.dp)
        )

        // Headset Support Button top right
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(42.dp)
                .clip(CircleShape)
                .background(Color(0xFFF1F5F9))
                .clickable { onSupportClick() }
                .testTag("support_button"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_support_headset),
                contentDescription = "Customer Support",
                tint = Color(0xFF334155),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun LanguageSegmentedControl(
    selectedLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF1F3F7))
            .padding(3.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AppLanguage.values().forEach { lang ->
                val isSelected = lang == selectedLanguage
                val bgColor by animateColorAsState(
                    targetValue = if (isSelected) Color.White else Color.Transparent,
                    animationSpec = tween(180),
                    label = "lang_bg"
                )
                val textColor by animateColorAsState(
                    targetValue = if (isSelected) TextPrimary else TextMuted,
                    animationSpec = tween(180),
                    label = "lang_text"
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(9.dp))
                        .then(
                            if (isSelected) {
                                Modifier
                                    .shadow(elevation = 2.dp, shape = RoundedCornerShape(9.dp))
                                    .background(bgColor)
                                    .border(0.5.dp, Color(0xFFE2E8F0), RoundedCornerShape(9.dp))
                            } else {
                                Modifier.background(bgColor)
                            }
                        )
                        .clickable { onLanguageSelected(lang) }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                        .testTag("lang_${lang.code}"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = lang.displayName,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                        color = textColor
                    )
                }
            }
        }
    }
}
