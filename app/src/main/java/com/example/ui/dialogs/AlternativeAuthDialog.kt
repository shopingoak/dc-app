package com.example.ui.dialogs

import androidx.compose.foundation.background
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AppLanguage
import com.example.ui.theme.MaxPurple
import com.example.ui.theme.TelegramBlue
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AlternativeAuthDialog(
    isTelegram: Boolean,
    lang: AppLanguage,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val brandColor = if (isTelegram) TelegramBlue else MaxPurple

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(brandColor),
                    contentAlignment = Alignment.Center
                ) {
                    if (isTelegram) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_telegram_logo),
                            contentDescription = "Telegram",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(
                            text = "MAX",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if (isTelegram) "Telegram Auth" else "MAX ID Auth",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (isTelegram) {
                        when (lang) {
                            AppLanguage.TJ -> "Барои воридшавӣ тавассути Telegram, шумо метавонед бо як пахш ба боти расмии DC City пайваст шуда, иҷозат диҳед."
                            AppLanguage.RU -> "Для входа через Telegram вы можете в один клик подтвердить авторизацию через официального бота DC City."
                            AppLanguage.EN -> "To sign in via Telegram, you can authorize with one click through official DC City bot."
                        }
                    } else {
                        when (lang) {
                            AppLanguage.TJ -> "Барои воридшавӣ тавассути MAX ID, профили тасдиқшудаи MAX-и шумо истифода мешавад."
                            AppLanguage.RU -> "Для входа через MAX ID будет использован ваш подтвержденный профиль в экосистеме MAX."
                            AppLanguage.EN -> "To sign in with MAX ID, your verified MAX ecosystem profile will be utilized."
                        }
                    },
                    fontSize = 13.5.sp,
                    color = TextSecondary,
                    lineHeight = 19.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = brandColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = when (lang) {
                        AppLanguage.TJ -> "Идома додан"
                        AppLanguage.RU -> "Продолжить"
                        AppLanguage.EN -> "Continue"
                    },
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = when (lang) {
                        AppLanguage.TJ -> "Бекор кардан"
                        AppLanguage.RU -> "Отмена"
                        AppLanguage.EN -> "Cancel"
                    },
                    color = TextSecondary
                )
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White
    )
}
