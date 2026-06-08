package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.WorkspaceViewModel
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassTextField
import com.example.ui.components.GlowButton
import com.example.ui.theme.*

@Composable
fun DashboardScreen(viewModel: WorkspaceViewModel) {
    val tasks by viewModel.tasks.collectAsState()
    val projects by viewModel.projects.collectAsState()
    val goals by viewModel.goals.collectAsState()
    val notes by viewModel.notes.collectAsState()
    val moodItems by viewModel.moodItems.collectAsState()
    val calendarEvents by viewModel.calendarEvents.collectAsState()

    var quickNoteText by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Welcome and Header Area
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "WELCOME BACK,",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyberCyan,
                    letterSpacing = 3.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Mohamed Khaled",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = DarkTextPrimary
                )
            }

            // Power off button / Logout
            IconButton(
                onClick = { viewModel.logout() },
                modifier = Modifier
                    .background(Color(0x0EFFFFFF), CircleShape)
                    .border(1.dp, Color(0x1AFFFFFF), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = "Log Out",
                    tint = SoftOrange
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // First Row: Core Widgets (Productivity Score & Daily Motivation Widget)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Productivity Widget
            GlassCard(
                modifier = Modifier
                    .weight(1.1f)
                    .height(160.dp),
                borderColor = ElectricBlue
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "PRODUCTIVITY",
                            style = MaterialTheme.typography.labelSmall,
                            color = DarkTextSecondary,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "94%",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Black,
                            color = ElectricBlue
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Excellent Space",
                            style = MaterialTheme.typography.bodySmall,
                            color = CyberCyan
                        )
                    }

                    // Simple Visual circular feedback
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(72.dp)
                    ) {
                        CircularProgressIndicator(
                            progress = { 0.94f },
                            modifier = Modifier.fillMaxSize(),
                            color = ElectricBlue,
                            strokeWidth = 6.dp,
                            trackColor = Color.White.copy(alpha = 0.05f),
                        )
                        Text(
                            text = "OPS",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = DarkTextPrimary
                        )
                    }
                }
            }

            // Daily Motivation Widget
            GlassCard(
                modifier = Modifier
                    .weight(1f)
                    .height(160.dp),
                borderColor = SoftOrange
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = "Idea",
                        tint = SoftOrange,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "DAILY MOTIVATION",
                        style = MaterialTheme.typography.labelSmall,
                        color = DarkTextSecondary,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "\"The best way to predict your creative future is to build it right now.\"",
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkTextPrimary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Second Row: Weekly Tracker Progress Bar Widget & Quick Note Input
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = Color(0x20FFFFFF)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "QUICK SPACE ENGINE",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyberCyan,
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        GlassTextField(
                            value = quickNoteText,
                            onValueChange = { quickNoteText = it },
                            placeholder = {
                                Text(
                                    "Type quick idea to instantly save...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = DarkTextMuted
                                )
                            }
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(ElectricBlue)
                            .clickable {
                                if (quickNoteText.isNotEmpty()) {
                                    viewModel.addNote(
                                        title = "Quick Note: ${quickNoteText.take(20)}...",
                                        content = quickNoteText,
                                        category = "Ideas",
                                        tags = "Quick"
                                    )
                                    quickNoteText = ""
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Quick Note",
                            tint = Color.White
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "WORKSPACE OVERVIEW",
            style = MaterialTheme.typography.labelSmall,
            color = DarkTextSecondary,
            letterSpacing = 2.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Module summary cards grids (Clickable routing targets)
        val overviewItems = listOf(
            OverviewInfo("Today's Tasks", "${tasks.filter { it.status != "Done" }.size} Pending", Icons.Default.CheckCircle, "Kanban", ElectricBlue),
            OverviewInfo("Active Projects", "${projects.filter { it.status == "Active" }.size} Running", Icons.Default.Dashboard, "Projects", CyberCyan),
            OverviewInfo("Goals Status", "${goals.filter { it.isCompleted }.size}/${goals.size} Completed", Icons.Default.EmojiEvents, "Goals", MutedAmber),
            OverviewInfo("Recent Notes", "${notes.size} Total", Icons.Default.Notes, "Notes", SoftOrange),
            OverviewInfo("Mood Board", "${moodItems.size} Anchors", Icons.Default.Palette, "MoodBoard", CyberCyan),
            OverviewInfo("Workspace Calendar", "${calendarEvents.size} Indicators", Icons.Default.CalendarMonth, "Calendar", ElectricBlue)
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            overviewItems.chunked(2).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowItems.forEach { item ->
                        GlassCard(
                            modifier = Modifier
                                .weight(1f)
                                .height(100.dp)
                                .clickable { viewModel.selectScreen(item.screenKey) },
                            borderColor = item.color.copy(alpha = 0.4f)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(item.color.copy(alpha = 0.15f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.title,
                                        tint = item.color,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = DarkTextPrimary
                                    )
                                    Text(
                                        text = item.status,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = DarkTextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Bottom Motivation statement
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0x06FFFFFF))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.MilitaryTech,
                contentDescription = "Operating System Integrity",
                tint = MutedAmber,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "MK Operating System. Locally encrypted database integration active.",
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkTextSecondary,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

data class OverviewInfo(
    val title: String,
    val status: String,
    val icon: ImageVector,
    val screenKey: String,
    val color: Color
)
