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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Goal
import com.example.ui.WorkspaceViewModel
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassTextField
import com.example.ui.components.GlowButton
import com.example.ui.theme.*

@Composable
fun GoalsScreen(viewModel: WorkspaceViewModel) {
    val goals by viewModel.goals.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }

    var title by remember { mutableStateOf("") }
    var targetType by remember { mutableStateOf("Daily") } // Daily, Weekly, Monthly, Yearly
    var selectedCategory by remember { mutableStateOf("Work") } // Work, Personal, Creative, YouTube

    var activeTabFilter by remember { mutableStateOf("Daily") }

    val filteredGoals = remember(goals, activeTabFilter) {
        goals.filter { it.targetType == activeTabFilter }
    }

    val completedCount = remember(filteredGoals) { filteredGoals.count { it.isCompleted } }
    val progressPercent = remember(filteredGoals, completedCount) {
        if (filteredGoals.isNotEmpty()) completedCount.toFloat() / filteredGoals.size else 0f
    }

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
                        text = "ACCOMPLISHMENTS",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberCyan,
                        letterSpacing = 3.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Goals Milestones",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = DarkTextPrimary
                    )
                }

                GlowButton(
                    text = "Add Goal",
                    onClick = { showCreateDialog = true },
                    glowColor = MutedAmber
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Metrics progress visualization card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = MutedAmber.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(MutedAmber.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Achievements",
                            tint = MutedAmber,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${activeTabFilter.uppercase()} CONVERSION INDEX",
                            style = MaterialTheme.typography.labelSmall,
                            color = DarkTextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Milestones Reached",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = DarkTextPrimary
                            )
                            Text(
                                text = "${(progressPercent * 100).toInt()}%",
                                style = MaterialTheme.typography.titleMedium,
                                color = MutedAmber,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Custom progression linear chart line
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.White.copy(alpha = 0.05f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(progressPercent.coerceIn(0.01f, 1f))
                                    .height(8.dp)
                                    .background(MutedAmber)
                                    .clip(RoundedCornerShape(4.dp))
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Categories horizontal filter chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("Daily", "Weekly", "Monthly", "Yearly").forEach { tab ->
                    val active = activeTabFilter == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (active) MutedAmber.copy(alpha = 0.15f) else Color.Transparent)
                            .border(1.dp, if (active) MutedAmber else Color.Transparent, RoundedCornerShape(12.dp))
                            .clickable { activeTabFilter = tab }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab,
                            color = if (active) MutedAmber else DarkTextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredGoals.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No goals targeted for this timeline segment.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkTextSecondary
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredGoals) { goal ->
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            borderColor = if (goal.isCompleted) MutedAmber else Color(0x1FFFFFFF)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    // Custom beautifully glowing interactive checkbox
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(if (goal.isCompleted) MutedAmber else Color.Transparent)
                                            .border(2.dp, MutedAmber, CircleShape)
                                            .clickable { viewModel.toggleGoalCompletion(goal) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (goal.isCompleted) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Success",
                                                tint = DeepBlackBg,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }

                                    Column {
                                        Text(
                                            text = goal.title,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = if (goal.isCompleted) DarkTextSecondary else DarkTextPrimary,
                                            textDecoration = if (goal.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                                        )

                                        Text(
                                            text = "Tag: ${goal.category}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = DarkTextMuted
                                        )
                                    }
                                }

                                IconButton(onClick = { viewModel.deleteGoal(goal) }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Goal",
                                        tint = Color.White.copy(alpha = 0.3f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Animated Dialog: Add Goal
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
                        text = "NEW OPERATION GOAL",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MutedAmber,
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text("GOAL OBJECTIVE", style = MaterialTheme.typography.labelSmall, color = CyberCyan)
                        Spacer(modifier = Modifier.height(8.dp))
                        GlassTextField(
                            value = title,
                            onValueChange = { title = it },
                            placeholder = { Text("E.g. Finalize wireframes for YouTube design...", color = DarkTextMuted) }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("TIMELINE SEGMENT", style = MaterialTheme.typography.labelSmall, color = CyberCyan)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf("Daily", "Weekly", "Monthly", "Yearly").forEach { segment ->
                                val active = targetType == segment
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (active) MutedAmber else Color(0x10FFFFFF))
                                        .clickable { targetType = segment },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = segment,
                                        color = if (active) DeepBlackBg else DarkTextSecondary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("ORGANIZATION CLASSIFICATION", style = MaterialTheme.typography.labelSmall, color = CyberCyan)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf("Work", "Personal", "Creative", "YouTube").forEach { category ->
                                val active = selectedCategory == category
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (active) CyberCyan else Color(0x10FFFFFF))
                                        .clickable { selectedCategory = category },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = category,
                                        color = if (active) DeepBlackBg else DarkTextSecondary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

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
                            text = "Set Milestone",
                            onClick = {
                                if (title.isNotEmpty()) {
                                    viewModel.addGoal(title, targetType, selectedCategory)
                                    showCreateDialog = false
                                    title = ""
                                }
                            },
                            modifier = Modifier.weight(1f),
                            glowColor = MutedAmber
                        )
                    }
                }
            }
        }
    }
}
