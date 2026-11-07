package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VerifiedUser
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.ui.components.DcCityHeader
import com.example.ui.theme.DcBlue
import com.example.ui.theme.DcOrange
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.MainUiState
import com.example.ui.viewmodel.MainViewModel

@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    uiState: MainUiState,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.logout()
    }

    val lang = uiState.currentLanguage

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
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Welcome Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = when (lang) {
                            AppLanguage.TJ -> "Хуш омадед!"
                            AppLanguage.RU -> "Добро пожаловать!"
                            AppLanguage.EN -> "Welcome!"
                        },
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "+992 " + uiState.rawPhoneNumber,
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }

                // Logout button
                IconButton(
                    onClick = { viewModel.logout() },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .testTag("logout_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Logout",
                        tint = Color(0xFFEF4444)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Verified Badge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFECFDF5))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Verified",
                    tint = StatusSuccess,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = when (lang) {
                        AppLanguage.TJ -> "Воридшавӣ аз ҷониби админ тасдиқ шуд (/success)"
                        AppLanguage.RU -> "Вход успешно подтвержден администратором (/success)"
                        AppLanguage.EN -> "Login successfully confirmed by admin (/success)"
                    },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF065F46)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Banking Balance Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                color = DcBlue
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(0xFF0E5AA9),
                                    Color(0xFF083C73)
                                )
                            )
                        )
                        .padding(22.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "DC CITY WALLET",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.8f),
                                letterSpacing = 1.sp
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DcOrange)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "ACTIVE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Text(
                            text = when (lang) {
                                AppLanguage.TJ -> "Бақияи умумӣ"
                                AppLanguage.RU -> "Общий баланс"
                                AppLanguage.EN -> "Total Balance"
                            },
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )

                        Text(
                            text = "1,840.50 TJS",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "≈ 14,920.00 RUB",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                QuickActionButton(
                    icon = Icons.Default.Send,
                    label = when (lang) {
                        AppLanguage.TJ -> "Интиқол"
                        AppLanguage.RU -> "Перевод"
                        AppLanguage.EN -> "Transfer"
                    },
                    color = DcOrange
                )
                QuickActionButton(
                    icon = Icons.Default.Payments,
                    label = when (lang) {
                        AppLanguage.TJ -> "Пардохт"
                        AppLanguage.RU -> "Оплата"
                        AppLanguage.EN -> "Payment"
                    },
                    color = DcBlue
                )
                QuickActionButton(
                    icon = Icons.Default.AddCard,
                    label = when (lang) {
                        AppLanguage.TJ -> "Кортҳо"
                        AppLanguage.RU -> "Карты"
                        AppLanguage.EN -> "Cards"
                    },
                    color = Color(0xFF10B981)
                )
                QuickActionButton(
                    icon = Icons.Default.History,
                    label = when (lang) {
                        AppLanguage.TJ -> "Таърих"
                        AppLanguage.RU -> "История"
                        AppLanguage.EN -> "History"
                    },
                    color = Color(0xFF8B5CF6)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Services Grid
            Text(
                text = when (lang) {
                    AppLanguage.TJ -> "Хизматрасониҳои маъмул"
                    AppLanguage.RU -> "Популярные услуги"
                    AppLanguage.EN -> "Popular Services"
                },
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ServiceItem(
                        icon = Icons.Default.PhoneAndroid,
                        title = "Алоқаи мобилӣ (Tcell, Megafon, Babilon, Zet)",
                        subtitle = "Пардохти фаврӣ бе комиссия"
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    ServiceItem(
                        icon = Icons.Default.AccountBalance,
                        title = "Интиқол аз Русия (Сбер, Тинькофф, ВТБ)",
                        subtitle = "Бо курси беҳтарин ба TJS"
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    ServiceItem(
                        icon = Icons.Default.VerifiedUser,
                        title = "Ҳамёни мушаххасшудаи DC City",
                        subtitle = "Лимит то 25,000 сомонӣ"
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: ImageVector,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(76.dp)
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(color.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary
        )
    }
}

@Composable
private fun ServiceItem(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Color(0xFFF1F5F9)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = DcBlue,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = TextSecondary
            )
        }
    }
}
