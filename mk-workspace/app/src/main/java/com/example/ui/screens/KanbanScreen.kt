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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Task
import com.example.ui.WorkspaceViewModel
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassTextField
import com.example.ui.components.GlowButton
import com.example.ui.theme.*

@Composable
fun KanbanScreen(viewModel: WorkspaceViewModel) {
    val tasks by viewModel.tasks.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf("To Do") } // To Do, In Progress, Review, Done
    var priority by remember { mutableStateOf("Medium") } // Low, Medium, High

    val columns = listOf("To Do", "In Progress", "Review", "Done")

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
                        text = "EFFICIENCY",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberCyan,
                        letterSpacing = 3.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Kanban Board",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = DarkTextPrimary
                    )
                }

                GlowButton(
                    text = "Add Card",
                    onClick = { showCreateDialog = true },
                    glowColor = SoftOrange
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Horizontally scrollable Columns wrapper
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                columns.forEach { colName ->
                    val columnTasks = tasks.filter { it.status.equals(colName, ignoreCase = true) }

                    // Column container card
                    Box(
                        modifier = Modifier
                            .width(280.dp)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0x04FFFFFF))
                            .border(1.dp, Color(0x0EFFFFFF), RoundedCornerShape(16.dp))
                            .padding(12.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            // Column Header
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(
                                                when (colName) {
                                                    "To Do" -> DarkTextSecondary
                                                    "In Progress" -> ElectricBlue
                                                    "Review" -> SoftOrange
                                                    else -> ForrestGreen
                                                },
                                                CircleShape
                                            )
                                    )
                                    Text(
                                        text = colName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = DarkTextPrimary
                                    )
                                }

                                Badge(
                                    containerColor = Color.White.copy(alpha = 0.1f),
                                    contentColor = Color.White
                                ) {
                                    Text(text = "${columnTasks.size}")
                                }
                            }

                            Divider(color = Color(0x10FFFFFF), thickness = 1.dp)
                            Spacer(modifier = Modifier.height(12.dp))

                            if (columnTasks.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .border(2.dp, Color(0x04FFFFFF), RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Empty column\nReady for allocations",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = DarkTextMuted,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(columnTasks) { task ->
                                        KanbanTaskCard(task, viewModel)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Animated Dialog: Add Task card
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
                        text = "NEW KANBAN TASK",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = SoftOrange,
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text("TASK CARD TITLE", style = MaterialTheme.typography.labelSmall, color = CyberCyan)
                        Spacer(modifier = Modifier.height(8.dp))
                        GlassTextField(
                            value = title,
                            onValueChange = { title = it },
                            placeholder = { Text("E.g. Refine Infinite Drawing Canvas...", color = DarkTextMuted) }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("BRIEF TASK OUTLINE", style = MaterialTheme.typography.labelSmall, color = CyberCyan)
                        Spacer(modifier = Modifier.height(8.dp))
                        GlassTextField(
                            value = description,
                            onValueChange = { description = it },
                            placeholder = { Text("List actionable steps or parameters...", color = DarkTextMuted) },
                            singleLine = false,
                            modifier = Modifier.height(80.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("TARGET COLUMN STATE", style = MaterialTheme.typography.labelSmall, color = CyberCyan)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf("To Do", "In Progress", "Review", "Done").forEach { col ->
                                val active = selectedStatus == col
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(34.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (active) SoftOrange else Color(0x10FFFFFF))
                                        .clickable { selectedStatus = col },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = col,
                                        color = if (active) Color.White else DarkTextSecondary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("TASK SEVERITY/PRIORITY", style = MaterialTheme.typography.labelSmall, color = CyberCyan)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf("Low", "Medium", "High").forEach { pri ->
                                val active = priority == pri
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(34.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            if (active) {
                                                when (pri) {
                                                    "High" -> Color(0xFFE53935)
                                                    "Medium" -> SoftOrange
                                                    else -> ForrestGreen
                                                }
                                            } else Color(0x10FFFFFF)
                                        )
                                        .clickable { priority = pri },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = pri,
                                        color = Color.White,
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
                            text = "Spawn Card",
                            onClick = {
                                if (title.isNotEmpty() && description.isNotEmpty()) {
                                    viewModel.addTask(
                                        title = title,
                                        description = description,
                                        status = selectedStatus,
                                        deadline = System.currentTimeMillis() + 86400000L,
                                        priority = priority
                                    )
                                    showCreateDialog = false
                                    title = ""
                                    description = ""
                                }
                            },
                            modifier = Modifier.weight(1f),
                            glowColor = SoftOrange
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun KanbanTaskCard(task: Task, viewModel: WorkspaceViewModel) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = when (task.priority) {
            "High" -> Color(0x80FF1744)
            "Medium" -> SoftOrange.copy(alpha = 0.5f)
            else -> ForrestGreen.copy(alpha = 0.5f)
        }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            when (task.priority) {
                                "High" -> Color(0x20FF1744)
                                "Medium" -> SoftOrange.copy(alpha = 0.2f)
                                else -> ForrestGreen.copy(alpha = 0.2f)
                            }
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = task.priority.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = when (task.priority) {
                            "High" -> Color(0xFFFFF1744)
                            "Medium" -> SoftOrange
                            else -> ForrestGreen
                        },
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = { viewModel.deleteTask(task.id) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete card",
                        tint = Color.White.copy(alpha = 0.3f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = task.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = DarkTextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = task.description,
                style = MaterialTheme.typography.bodySmall,
                color = DarkTextSecondary,
                lineHeight = 14.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Arrow controllers to transition card columns
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (task.status != "To Do") {
                    IconButton(
                        onClick = {
                            val nextCol = when (task.status) {
                                "In Progress" -> "To Do"
                                "Review" -> "In Progress"
                                "Done" -> "Review"
                                else -> "To Do"
                            }
                            viewModel.updateTaskStatus(task, nextCol)
                        },
                        modifier = Modifier
                            .size(28.dp)
                            .background(Color.White.copy(alpha = 0.04f), CircleShape)
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Move Back", tint = DarkTextSecondary, modifier = Modifier.size(14.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                if (task.status != "Done") {
                    IconButton(
                        onClick = {
                            val nextCol = when (task.status) {
                                "To Do" -> "In Progress"
                                "In Progress" -> "Review"
                                "Review" -> "Done"
                                else -> "Done"
                            }
                            viewModel.updateTaskStatus(task, nextCol)
                        },
                        modifier = Modifier
                            .size(28.dp)
                            .background(Color.White.copy(alpha = 0.04f), CircleShape)
                    ) {
                        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Move Forward", tint = CyberCyan, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
    }
}

val ForrestGreen = Color(0xFF4CAF50)
