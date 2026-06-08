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
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Project
import com.example.ui.WorkspaceViewModel
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassTextField
import com.example.ui.components.GlowButton
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProjectsScreen(viewModel: WorkspaceViewModel) {
    val projects by viewModel.projects.collectAsState()
    val tasks by viewModel.tasks.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Active") } // Planning, Active, On Hold, Completed
    var colorHex by remember { mutableStateOf("#1E88E5") }

    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    val colorsMap = listOf(
        "#1E88E5" to "Electric Blue",
        "#E64A19" to "Soft Orange",
        "#00B0FF" to "Cyber Cyan",
        "#43A047" to "Forest Green",
        "#E91E63" to "Laser Pink"
    )

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
                        text = "PLANNING",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberCyan,
                        letterSpacing = 3.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Projects Workspace",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = DarkTextPrimary
                    )
                }

                GlowButton(
                    text = "New Project",
                    onClick = { showCreateDialog = true },
                    glowColor = ElectricBlue
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (projects.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Assignment,
                            contentDescription = "Empty Projects",
                            tint = DarkTextMuted,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No projects inside this workflow.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkTextSecondary
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(projects) { project ->
                        val projectTasks = tasks.filter { it.projectId == project.id }
                        val completedCount = projectTasks.count { it.status == "Done" }
                        val progressValue = if (projectTasks.isNotEmpty()) {
                            completedCount.toFloat() / projectTasks.size
                        } else 0f

                        val projectColor = remember(project.colorHex) {
                            try {
                                Color(android.graphics.Color.parseColor(project.colorHex))
                            } catch (e: Exception) {
                                ElectricBlue
                            }
                        }

                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            borderColor = projectColor
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .background(projectColor, CircleShape)
                                        )

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(projectColor.copy(alpha = 0.15f))
                                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = project.status.uppercase(),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = projectColor,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp
                                            )
                                        }

                                        Spacer(modifier = Modifier.weight(1f))

                                        IconButton(onClick = { viewModel.deleteProject(project.id) }) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete project",
                                                tint = Color.White.copy(alpha = 0.3f),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = project.name,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = DarkTextPrimary
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = project.description,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = DarkTextSecondary,
                                        lineHeight = 18.sp
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CalendarToday,
                                            contentDescription = "Deadline",
                                            tint = DarkTextMuted,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = "Deadline: ${dateFormat.format(Date(project.deadline))}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = DarkTextMuted
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Simple custom progression bar
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Project Milestones",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = DarkTextSecondary
                                            )
                                            Text(
                                                text = "${(progressValue * 100).toInt()}% completed",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = projectColor,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(6.dp)
                                                .clip(RoundedCornerShape(3.dp))
                                                .background(Color.White.copy(alpha = 0.05f))
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth(progressValue.coerceIn(0.01f, 1f))
                                                    .height(6.dp)
                                                    .background(
                                                        brush = Brush.horizontalGradient(
                                                            colors = listOf(projectColor, projectColor.copy(alpha = 0.5f))
                                                        )
                                                    )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Custom animation dialogue for project insertion
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
                        text = "NEW OPERATIONAL PROJECT",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = ElectricBlue,
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text("PROJECT NAME", style = MaterialTheme.typography.labelSmall, color = CyberCyan)
                        Spacer(modifier = Modifier.height(8.dp))
                        GlassTextField(
                            value = name,
                            onValueChange = { name = it },
                            placeholder = { Text("Enter project name...", color = DarkTextMuted) }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("BRIEF OBJECTIVE", style = MaterialTheme.typography.labelSmall, color = CyberCyan)
                        Spacer(modifier = Modifier.height(8.dp))
                        GlassTextField(
                            value = description,
                            onValueChange = { description = it },
                            placeholder = { Text("What is search or deliverable criteria?... ", color = DarkTextMuted) },
                            singleLine = false,
                            modifier = Modifier.height(80.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("INITIAL WORKFLOW STATUS", style = MaterialTheme.typography.labelSmall, color = CyberCyan)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf("Planning", "Active", "On Hold", "Completed").forEach { s ->
                                val active = status == s
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(34.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (active) ElectricBlue else Color(0x10FFFFFF))
                                        .clickable { status = s },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = s,
                                        color = if (active) Color.White else DarkTextSecondary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("PROJECT BRAND COLOR", style = MaterialTheme.typography.labelSmall, color = CyberCyan)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            colorsMap.forEach { pair ->
                                val active = colorHex == pair.first
                                val targetColor = remember { Color(android.graphics.Color.parseColor(pair.first)) }
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(targetColor)
                                        .border(
                                            width = 2.dp,
                                            color = if (active) Color.White else Color.Transparent,
                                            shape = CircleShape
                                        )
                                        .clickable { colorHex = pair.first }
                                )
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
                            text = "Save ProjectSignal",
                            onClick = {
                                if (name.isNotEmpty() && description.isNotEmpty()) {
                                    viewModel.addProject(
                                        name = name,
                                        description = description,
                                        status = status,
                                        deadline = System.currentTimeMillis() + 86400000L * 10,
                                        colorHex = colorHex
                                    )
                                    showCreateDialog = false
                                    name = ""
                                    description = ""
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
