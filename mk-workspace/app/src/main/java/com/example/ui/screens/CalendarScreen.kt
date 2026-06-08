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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NotificationsActive
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
import com.example.data.CalendarEvent
import com.example.ui.WorkspaceViewModel
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassTextField
import com.example.ui.components.GlowButton
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CalendarScreen(viewModel: WorkspaceViewModel) {
    val events by viewModel.calendarEvents.collectAsState()
    val selectedTimestamp by viewModel.calendarSelectedDate.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var calendarViewMode by remember { mutableStateOf("Month") } // Month, Week, Day

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isReminder by remember { mutableStateOf(false) }
    var eventCategory by remember { mutableStateOf("Work") } // Work, Personal, Deadline

    val calendar = remember(selectedTimestamp) {
        Calendar.getInstance().apply { timeInMillis = selectedTimestamp }
    }

    val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    val dayFormat = SimpleDateFormat("EEEE, MMM dd", Locale.getDefault())

    // Month Grid Calculation: Lets represent a standard Month view of 35 elements
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOffset = remember(selectedTimestamp) {
        val tempCal = Calendar.getInstance().apply {
            timeInMillis = selectedTimestamp
            set(Calendar.DAY_OF_MONTH, 1)
        }
        tempCal.get(Calendar.DAY_OF_WEEK) - 1 // 0-indexed sunday offset
    }

    val currentSelectedDay = calendar.get(Calendar.DAY_OF_MONTH)

    val currentDayEvents = remember(events, selectedTimestamp) {
        val sCal = Calendar.getInstance().apply { timeInMillis = selectedTimestamp }
        events.filter { event ->
            val eCal = Calendar.getInstance().apply { timeInMillis = event.date }
            sCal.get(Calendar.YEAR) == eCal.get(Calendar.YEAR) &&
            sCal.get(Calendar.DAY_OF_YEAR) == eCal.get(Calendar.DAY_OF_YEAR)
        }
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
                        text = "CHRONOLOGY",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberCyan,
                        letterSpacing = 3.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Calendar View",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = DarkTextPrimary
                    )
                }

                GlowButton(
                    text = "New Event",
                    onClick = { showCreateDialog = true },
                    glowColor = ElectricBlue
                )
            }

            // Month / Week / Day views segment triggers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("Month", "Week", "Day").forEach { mode ->
                    val active = calendarViewMode == mode
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (active) ElectricBlue.copy(alpha = 0.15f) else Color.Transparent)
                            .border(1.dp, if (active) ElectricBlue else Color.Transparent, RoundedCornerShape(12.dp))
                            .clickable { calendarViewMode = mode }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$mode View",
                            color = if (active) ElectricBlue else DarkTextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Navigation bar month/year
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        calendar.add(Calendar.MONTH, -1)
                        viewModel.selectCalendarDate(calendar.timeInMillis)
                    },
                    modifier = Modifier.background(Color(0x0AFFFFFF), CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = "Prev", tint = Color.White)
                }

                Text(
                    text = monthYearFormat.format(calendar.time).uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DarkTextPrimary,
                    letterSpacing = 1.sp
                )

                IconButton(
                    onClick = {
                        calendar.add(Calendar.MONTH, 1)
                        viewModel.selectCalendarDate(calendar.timeInMillis)
                    },
                    modifier = Modifier.background(Color(0x0AFFFFFF), CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Next", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Draw Month Grid if Month mode is active (the primary mode)
            if (calendarViewMode == "Month") {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = GlassBorderColor
                ) {
                    // Week headers
                    Row(modifier = Modifier.fillMaxWidth()) {
                        listOf("S", "M", "T", "W", "T", "F", "S").forEach { d ->
                            Text(
                                text = d,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.labelSmall,
                                color = CyberCyan,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Days grid
                    val totalSlots = firstDayOffset + daysInMonth
                    val weeksNeeded = if (totalSlots % 7 == 0) totalSlots / 7 else (totalSlots / 7) + 1

                    for (w in 0 until weeksNeeded) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            for (d in 0 until 7) {
                                val currentSlotIndex = w * 7 + d
                                val dayNum = currentSlotIndex - firstDayOffset + 1
                                val isValidDayNum = dayNum in 1..daysInMonth

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .clip(CircleShape)
                                        .background(
                                            if (isValidDayNum && dayNum == currentSelectedDay) ElectricBlue else Color.Transparent
                                        )
                                        .clickable(enabled = isValidDayNum) {
                                            calendar.set(Calendar.DAY_OF_MONTH, dayNum)
                                            viewModel.selectCalendarDate(calendar.timeInMillis)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isValidDayNum) {
                                        Text(
                                            text = "$dayNum",
                                            fontWeight = if (dayNum == currentSelectedDay) FontWeight.Black else FontWeight.Normal,
                                            color = if (dayNum == currentSelectedDay) Color.White else DarkTextPrimary,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Selected Day agenda list
            Text(
                text = "${dayFormat.format(calendar.time).uppercase()} AGENDA KEYS",
                style = MaterialTheme.typography.labelSmall,
                color = MutedAmber,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (currentDayEvents.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .border(1.dp, Color(0x06FFFFFF), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = "None", tint = DarkTextMuted, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No operational events slotted for this key.",
                            style = MaterialTheme.typography.bodySmall,
                            color = DarkTextMuted
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(currentDayEvents) { event ->
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            borderColor = if (event.isReminder) SoftOrange else ElectricBlue
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(
                                                if (event.isReminder) SoftOrange.copy(alpha = 0.1f) else ElectricBlue.copy(alpha = 0.1f),
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.NotificationsActive,
                                            contentDescription = "Alert",
                                            tint = if (event.isReminder) SoftOrange else ElectricBlue,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = event.title,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = DarkTextPrimary
                                        )
                                        Text(
                                            text = event.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = DarkTextSecondary
                                        )
                                    }
                                }

                                IconButton(onClick = { viewModel.deleteCalendarEvent(event) }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete event",
                                        tint = Color.White.copy(alpha = 0.3f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Animated Dialog: Add CalendarEvent
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
                        text = "NEW TRACKING EVENT",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = ElectricBlue,
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text("EVENT DESIGNATION", style = MaterialTheme.typography.labelSmall, color = CyberCyan)
                        Spacer(modifier = Modifier.height(8.dp))
                        GlassTextField(
                            value = title,
                            onValueChange = { title = it },
                            placeholder = { Text("E.g. Workspace release review...", color = DarkTextMuted) }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("DESCRIPTION / OBJECTIVE", style = MaterialTheme.typography.labelSmall, color = CyberCyan)
                        Spacer(modifier = Modifier.height(8.dp))
                        GlassTextField(
                            value = description,
                            onValueChange = { description = it },
                            placeholder = { Text("Enter detail requirements...", color = DarkTextMuted) },
                            singleLine = false,
                            modifier = Modifier.height(70.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("NOTIFICATIONS ALERTS", style = MaterialTheme.typography.labelSmall, color = CyberCyan)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable { isReminder = !isReminder }
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Checkbox(
                                checked = isReminder,
                                onCheckedChange = { isReminder = it },
                                colors = CheckboxDefaults.colors(checkedColor = SoftOrange)
                            )
                            Text(
                                text = "Inject active reminder notify trigger alert",
                                style = MaterialTheme.typography.bodyMedium,
                                color = DarkTextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("EVENT SEGMENT", style = MaterialTheme.typography.labelSmall, color = CyberCyan)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf("Work", "Personal", "Deadline").forEach { category ->
                                val active = eventCategory == category
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (active) ElectricBlue else Color(0x10FFFFFF))
                                        .clickable { eventCategory = category },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = category,
                                        color = if (active) Color.White else DarkTextSecondary,
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
                            text = "Spawn Event",
                            onClick = {
                                if (title.isNotEmpty()) {
                                    viewModel.addCalendarEvent(
                                        title = title,
                                        description = description,
                                        date = selectedTimestamp,
                                        category = eventCategory,
                                        isReminder = isReminder
                                    )
                                    showCreateDialog = false
                                    title = ""
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
