package com.example.ui.dialogs

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AppLanguage
import com.example.ui.theme.DcBlue
import com.example.ui.theme.DcOrange
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SupportDialog(
    lang: AppLanguage,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(DcBlue.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_support_headset),
                        contentDescription = "Support",
                        tint = DcBlue,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = when (lang) {
                        AppLanguage.TJ -> "Хадамоти дастгирӣ"
                        AppLanguage.RU -> "Служба поддержки"
                        AppLanguage.EN -> "Customer Support"
                    },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = when (lang) {
                        AppLanguage.TJ -> "Барои кӯмак ё саволҳо ба маркази тамос муроҷиат кунед (24/7):"
                        AppLanguage.RU -> "Для помощи или вопросов свяжитесь с колл-центром (24/7):"
                        AppLanguage.EN -> "For assistance or inquiries contact our call center (24/7):"
                    },
                    fontSize = 13.sp,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                SupportContactRow(
                    icon = Icons.Default.Call,
                    title = "7711",
                    subtitle = when (lang) {
                        AppLanguage.TJ -> "Занг аз тамоми операторони Тоҷикистон ройгон"
                        AppLanguage.RU -> "Бесплатный звонок по Таджикистану"
                        AppLanguage.EN -> "Toll-free in Tajikistan"
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                SupportContactRow(
                    icon = Icons.Default.QuestionAnswer,
                    title = "@dccity_support_bot",
                    subtitle = when (lang) {
                        AppLanguage.TJ -> "Дастгирии фаврӣ дар Telegram"
                        AppLanguage.RU -> "Быстрая поддержка в Telegram"
                        AppLanguage.EN -> "Fast Telegram Support"
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                SupportContactRow(
                    icon = Icons.Default.Language,
                    title = "dc.tj",
                    subtitle = "Dushanbe City Official Website"
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = DcOrange),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = when (lang) {
                        AppLanguage.TJ -> "Фаҳмо"
                        AppLanguage.RU -> "Понятно"
                        AppLanguage.EN -> "Close"
                    },
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White
    )
}

@Composable
private fun SupportContactRow(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFFF1F5F9)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = DcBlue,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 11.5.sp,
                color = TextSecondary
            )
        }
    }
}
