package com.example.ui.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.ui.theme.DcOrange
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun TermsDialog(
    isPrivacyPolicy: Boolean,
    lang: AppLanguage,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isPrivacyPolicy) {
                    when (lang) {
                        AppLanguage.TJ -> "Сиёсати махфият"
                        AppLanguage.RU -> "Политика конфиденциальности"
                        AppLanguage.EN -> "Privacy Policy"
                    }
                } else {
                    when (lang) {
                        AppLanguage.TJ -> "Шартҳои Оферта"
                        AppLanguage.RU -> "Условия Оферты"
                        AppLanguage.EN -> "Terms of Offer"
                    }
                },
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 340.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = if (isPrivacyPolicy) {
                        when (lang) {
                            AppLanguage.TJ -> "1. Мо махфияти маълумоти шахсии шуморо ҳифз мекунем.\n\n" +
                                    "2. Рақами телефони шумо танҳо барои аутентификатсия ва фиристодани паёмҳои смс истифода мешавад.\n\n" +
                                    "3. Ҳеҷ гуна паролҳо ё рамзҳои махфӣ дар тарафи сеюм сабт намешаванд.\n\n" +
                                    "4. Тамоми амалиётҳо бо протоколҳои муосири бехатарии шифркунӣ муҳофизат мегарданд."
                            AppLanguage.RU -> "1. Мы обеспечиваем безопасность и конфиденциальность ваших персональных данных.\n\n" +
                                    "2. Ваш номер телефона используется исключительно для аутентификации и отправки проверочных смс.\n\n" +
                                    "3. Секретные коды и пароли не передаются и не сохраняются третьим лицам.\n\n" +
                                    "4. Все транзакции защищены современными стандартами банковского шифрования."
                            AppLanguage.EN -> "1. We protect the privacy and security of your personal data.\n\n" +
                                    "2. Your phone number is solely used for authentication and sending verification messages.\n\n" +
                                    "3. Secret codes and passwords are never exposed or saved to third parties.\n\n" +
                                    "4. All transactions are protected by modern end-to-end encryption standards."
                        }
                    } else {
                        when (lang) {
                            AppLanguage.TJ -> "1. Бо истифода аз замимаи DC City шумо ба шартҳои хизматрасонии бонкӣ розӣ мешавед.\n\n" +
                                    "2. Интиқоли маблағҳо мутобиқи тарифҳо ва қоидаҳои Бонки миллии Тоҷикистон амалӣ карда мешавад.\n\n" +
                                    "3. Корбар барои дурустии рақами телефони воридшуда масъул аст."
                            AppLanguage.RU -> "1. Используя приложение DC City, вы соглашаетесь с условиями публичной оферты и банковского обслуживания.\n\n" +
                                    "2. Денежные переводы и платежи осуществляются в соответствии с тарифами и законодательством РТ.\n\n" +
                                    "3. Пользователь несет ответственность за корректность введенного номера телефона."
                            AppLanguage.EN -> "1. By using DC City app, you agree to the terms of public banking service offer.\n\n" +
                                    "2. Money transfers and operations are processed according to bank terms.\n\n" +
                                    "3. The user is responsible for entering a valid phone number."
                        }
                    },
                    fontSize = 13.5.sp,
                    color = TextSecondary,
                    lineHeight = 20.sp
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
                        AppLanguage.TJ -> "Қабул дорам"
                        AppLanguage.RU -> "Принимаю"
                        AppLanguage.EN -> "Accept"
                    },
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        shape = RoundedCornerShape(24.dp),
        containerColor = Color.White
    )
}
