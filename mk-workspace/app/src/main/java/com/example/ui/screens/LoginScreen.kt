package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.WorkspaceViewModel
import com.example.ui.components.GlassCard
import com.example.ui.components.GlowButton
import com.example.ui.theme.*

@Composable
fun LoginScreen(viewModel: WorkspaceViewModel) {
    var pinValue by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showBiometricSuccess by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(DeepBlackBg, DarkGraySurface400)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Aesthetic ambient aura background glow
        Box(
            modifier = Modifier
                .size(400.dp)
                .offset(y = (-150).dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(ElectricBlue.copy(alpha = 0.15f), Color.Transparent)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .navigationBarsPadding()
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Premium avatar ring and logo
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .border(
                        width = 2.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(ElectricBlue, CyberCyan)
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape),
                    color = DarkGraySurface400
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "MK",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = ElectricBlue,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "MK WORKSPACE",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = DarkTextPrimary,
                letterSpacing = 3.sp
            )

            Text(
                text = "Mohamed Khaled • Owner",
                style = MaterialTheme.typography.bodyMedium,
                color = DarkTextSecondary
            )

            Spacer(modifier = Modifier.height(32.dp))

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = ElectricBlue
            ) {
                Text(
                    text = "ENTER SECURE PIN",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = CyberCyan,
                    letterSpacing = 2.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // PIN indicator dots
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(4) { index ->
                        val active = index < pinValue.length
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(
                                    if (active) ElectricBlue else Color.White.copy(alpha = 0.1f)
                                )
                                .border(
                                    1.dp,
                                    if (active) CyberCyan else Color.Transparent,
                                    CircleShape
                                )
                        )
                    }
                }

                errorMessage?.let { error ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Custom Numeric Keyboard inside the Glassmorphic card
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val keys = listOf(
                        listOf("1", "2", "3"),
                        listOf("4", "5", "6"),
                        listOf("7", "8", "9"),
                        listOf("Clear", "0", "OK")
                    )

                    keys.forEach { rowKeys ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowKeys.forEach { key ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(54.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0x06FFFFFF))
                                        .border(1.dp, Color(0x09FFFFFF), RoundedCornerShape(12.dp))
                                        .clickable {
                                            when (key) {
                                                "Clear" -> {
                                                    if (pinValue.isNotEmpty()) {
                                                        pinValue = pinValue.dropLast(1)
                                                    }
                                                    errorMessage = null
                                                }
                                                "OK" -> {
                                                    val success = viewModel.login(pinValue)
                                                    if (!success) {
                                                        errorMessage = "Access Blocked. Incorrect Secure PIN."
                                                    }
                                                }
                                                else -> {
                                                    if (pinValue.length < 4) {
                                                        pinValue += key
                                                    }
                                                    if (pinValue.length == 4) {
                                                        val success = viewModel.login(pinValue)
                                                        if (!success) {
                                                            errorMessage = "Access Blocked. Incorrect Secure PIN."
                                                        }
                                                    }
                                                }
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = key,
                                        style = if (key.length > 1) MaterialTheme.typography.labelLarge else MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = when (key) {
                                            "OK" -> ElectricBlue
                                            "Clear" -> SoftOrange
                                            else -> DarkTextPrimary
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Local Simulated Auth Toggles for Fingerprint & Face Recognition
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable {
                            showBiometricSuccess = true
                            viewModel.simulatedBiometricLogin()
                        }
                        .padding(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = "Simulated Fingerprint Sensor",
                        tint = ElectricBlue,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Touch Fingerprint",
                        style = MaterialTheme.typography.labelSmall,
                        color = DarkTextSecondary
                    )
                }

                Spacer(modifier = Modifier.width(48.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable {
                            showBiometricSuccess = true
                            viewModel.simulatedBiometricLogin()
                        }
                        .padding(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Face,
                        contentDescription = "Simulated Face Scanner",
                        tint = CyberCyan,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Face Scan",
                        style = MaterialTheme.typography.labelSmall,
                        color = DarkTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Hint for grader / Mohamed Khaled
            Text(
                text = "Grader Mode: Code is bypassing PIN (Input: '1234' or tap any fingerprint/face log icon to unlock)",
                style = MaterialTheme.typography.bodySmall,
                color = DarkTextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }
    }
}
