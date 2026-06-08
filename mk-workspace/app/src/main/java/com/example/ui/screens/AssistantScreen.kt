package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.WorkspaceViewModel
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassTextField
import com.example.ui.components.GlowButton
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AssistantScreen(viewModel: WorkspaceViewModel) {
    val chatHistory by viewModel.aiChatHistory.collectAsState()
    val isAiLoading by viewModel.isAiLoading.collectAsState()

    var textInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Scroll to the latest message whenever it loads
    LaunchedEffect(chatHistory.size) {
        if (chatHistory.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(chatHistory.size - 1)
            }
        }
    }

    val quickCommands = listOf(
        "Plan my day",
        "Generate a YouTube roadmap",
        "Create a horror content strategy",
        "Organize my projects"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Welcome and Header area
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "AI", tint = CyberCyan, modifier = Modifier.size(16.dp))
                    Text(
                        text = "GEMINI 3.5 AI ENGINE",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberCyan,
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "MK Digital AI",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = DarkTextPrimary
                )
            }

            IconButton(
                onClick = { viewModel.clearAiChat() },
                modifier = Modifier
                    .background(Color(0x0EFFFFFF), CircleShape)
                    .border(1.dp, Color(0x1BFFFFFF), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.CleaningServices,
                    contentDescription = "Flush Chat",
                    tint = SoftOrange
                )
            }
        }

        // Quick commands scrolling shortcuts row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            quickCommands.forEach { cmd ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0x0F2F7BFF))
                        .border(1.dp, ElectricBlue.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                        .clickable(enabled = !isAiLoading) {
                            viewModel.askAiAssistant(cmd)
                        }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = cmd,
                        color = ElectricBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Chat conversation timeline
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(chatHistory) { messagePair ->
                val isUser = messagePair.second
                val text = messagePair.first

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .clip(
                                RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp,
                                    bottomStart = if (isUser) 16.dp else 2.dp,
                                    bottomEnd = if (isUser) 2.dp else 16.dp
                                )
                            )
                            .background(
                                if (isUser) Color(0x1D00E5FF) else Color(0x0EFFFFFF)
                            )
                            .border(
                                width = 1.dp,
                                color = if (isUser) CyberCyan.copy(alpha = 0.25f) else Color(0x0EFFFFFF),
                                shape = RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp,
                                    bottomStart = if (isUser) 16.dp else 2.dp,
                                    bottomEnd = if (isUser) 2.dp else 16.dp
                                )
                            )
                            .padding(14.dp)
                    ) {
                        Column {
                            Text(
                                text = if (isUser) "MOHAMED KHALED" else "MK WORKSPACE CENTRAL MIND",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isUser) CyberCyan else ElectricBlue,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )

                            // Quick markdown parsing visual simulator in response blocks
                            text.split("\n").forEach { line ->
                                when {
                                    line.startsWith("**") -> {
                                        Text(
                                            text = line.replace("**", ""),
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(vertical = 2.dp)
                                        )
                                    }
                                    line.trim().startsWith("- ") || line.trim().startsWith("* ") -> {
                                        Text(
                                            text = "• ${line.trim().drop(2)}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = DarkTextSecondary,
                                            modifier = Modifier.padding(start = 12.dp, top = 2.dp, bottom = 2.dp)
                                        )
                                    }
                                    else -> {
                                        if (line.isNotEmpty()) {
                                            Text(
                                                text = line,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = DarkTextSecondary,
                                                lineHeight = 20.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Real-time AI generative thinking orbits
            if (isAiLoading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0x06FFFFFF))
                                .padding(12.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = CyberCyan,
                                strokeWidth = 2.dp
                            )
                            Text(
                                text = "Gemini model calculations spinning...",
                                style = MaterialTheme.typography.bodySmall,
                                color = DarkTextMuted
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // TextInput layout
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 60.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                GlassTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = {
                        Text(
                            "Consult digital workspace logic...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkTextMuted
                        )
                    }
                )
            }

            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isAiLoading) Color(0x10FFFFFF) else CyberCyan)
                    .clickable(enabled = !isAiLoading && textInput.isNotEmpty()) {
                        viewModel.askAiAssistant(textInput)
                        textInput = ""
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send prompt",
                    tint = if (isAiLoading) DarkTextMuted else DeepBlackBg
                )
            }
        }
    }
}
