package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.JournalEntry
import com.example.ui.WorkspaceViewModel
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassTextField
import com.example.ui.components.GlowButton
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun JournalScreen(viewModel: WorkspaceViewModel) {
    val entries by viewModel.journalEntries.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedTag by remember { mutableStateOf("Personal") } // Personal, Work, Creative
    var selectedMood by remember { mutableStateOf("Excellent") } // Excellent, Good, Neutral, Low
    var reflection by remember { mutableStateOf("") }

    val dateFormat = SimpleDateFormat("MMM dd, yyyy - HH:mm", Locale.getDefault())

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ORGANIZATION",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberCyan,
                        letterSpacing = 3.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Daily Journal",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = DarkTextPrimary
                    )
                }

                GlowButton(
                    text = "New Entry",
                    onClick = { showCreateDialog = true },
                    glowColor = ElectricBlue
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (entries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Book,
                            contentDescription = "Empty Journal",
                            tint = DarkTextMuted,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No journal entries captured yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkTextSecondary
                        )
                        Text(
                            text = "Write your first daily reflection to begin.",
                            style = MaterialTheme.typography.bodySmall,
                            color = DarkTextMuted
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(entries) { entry ->
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            borderColor = when (entry.tag) {
                                "Work" -> ElectricBlue
                                "Creative" -> SoftOrange
                                else -> CyberCyan
                            }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(
                                                    when (entry.tag) {
                                                        "Work" -> ElectricBlue.copy(alpha = 0.2f)
                                                        "Creative" -> SoftOrange.copy(alpha = 0.2f)
                                                        else -> CyberCyan.copy(alpha = 0.2f)
                                                    }
                                                )
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = entry.tag.uppercase(),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = when (entry.tag) {
                                                    "Work" -> ElectricBlue
                                                    "Creative" -> SoftOrange
                                                    else -> CyberCyan
                                                },
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Text(
                                            text = "Mood: ${entry.mood}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MutedAmber,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = entry.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = DarkTextPrimary
                                    )

                                    Text(
                                        text = dateFormat.format(Date(entry.date)),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = DarkTextMuted
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Text(
                                        text = entry.content,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = DarkTextSecondary,
                                        lineHeight = 20.sp
                                    )

                                    if (entry.reflection.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .border(1.dp, Color(0x10FFFFFF), RoundedCornerShape(8.dp))
                                                .background(Color(0x04FFFFFF))
                                                .padding(8.dp)
                                        ) {
                                            Text(
                                                text = "DAILY REFLECTION",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = CyberCyan,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = entry.reflection,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = DarkTextSecondary
                                            )
                                        }
                                    }
                                }

                                IconButton(
                                    onClick = { viewModel.deleteJournal(entry.id) },
                                    modifier = Modifier.padding(start = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete entry",
                                        tint = Color.White.copy(alpha = 0.4f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Custom Animated Dialogue overlay to input journal
        AnimatedVisibility(
            visible = showCreateDialog,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = DeepBlackBg.copy(alpha = 0.95f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "NEW JOURNAL INSIGHT",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = ElectricBlue,
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text("JOURNAL TITLE", style = MaterialTheme.typography.labelSmall, color = CyberCyan)
                        Spacer(modifier = Modifier.height(8.dp))
                        GlassTextField(
                            value = title,
                            onValueChange = { title = it },
                            placeholder = { Text("Enter title...", color = DarkTextMuted) }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("ENTRY CATEGORY", style = MaterialTheme.typography.labelSmall, color = CyberCyan)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf("Personal", "Work", "Creative").forEach { tag ->
                                val active = selectedTag == tag
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (active) ElectricBlue else Color(0x10FFFFFF))
                                        .border(1.dp, if (active) CyberCyan else Color.Transparent, RoundedCornerShape(8.dp))
                                        .clickable { selectedTag = tag },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = tag,
                                        color = if (active) Color.White else DarkTextSecondary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("SELECT CURRENT MOOD", style = MaterialTheme.typography.labelSmall, color = CyberCyan)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf("Excellent", "Good", "Neutral", "Low").forEach { mood ->
                                val active = selectedMood == mood
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (active) SoftOrange else Color(0x10FFFFFF))
                                        .border(1.dp, if (active) MutedAmber else Color.Transparent, RoundedCornerShape(8.dp))
                                        .clickable { selectedMood = mood },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = mood,
                                        color = if (active) Color.White else DarkTextSecondary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("CONTENT LOG", style = MaterialTheme.typography.labelSmall, color = CyberCyan)
                        Spacer(modifier = Modifier.height(8.dp))
                        GlassTextField(
                            value = content,
                            onValueChange = { content = it },
                            placeholder = { Text("How did your business/creative actions flow today?...", color = DarkTextMuted) },
                            singleLine = false,
                            modifier = Modifier.height(110.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("DAILY REFLECTION & TAKEAWAYS", style = MaterialTheme.typography.labelSmall, color = CyberCyan)
                        Spacer(modifier = Modifier.height(8.dp))
                        GlassTextField(
                            value = reflection,
                            onValueChange = { reflection = it },
                            placeholder = { Text("What did you learn? Lessons or adjustments?...", color = DarkTextMuted) },
                            singleLine = false,
                            modifier = Modifier.height(80.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = { showCreateDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.05f)),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Cancel", color = Color.White)
                        }

                        GlowButton(
                            text = "Save Entry",
                            onClick = {
                                if (title.isNotEmpty() && content.isNotEmpty()) {
                                    viewModel.addJournalEntry(title, content, selectedMood, selectedTag, reflection)
                                    showCreateDialog = false
                                    title = ""
                                    content = ""
                                    reflection = ""
                                }
                            },
                            modifier = Modifier.weight(1f),
                            glowColor = ElectricBlue
                        )
                    }
                }
            }
        }
    }
}
