package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MoodItem
import com.example.ui.WorkspaceViewModel
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassTextField
import com.example.ui.components.GlowButton
import com.example.ui.theme.*
import kotlin.math.roundToInt

@Composable
fun MoodBoardScreen(viewModel: WorkspaceViewModel) {
    val items by viewModel.moodItems.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var boardMode by remember { mutableStateOf("Canvas") } // Canvas, Grid

    var title by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("ColorPalette") } // ColorPalette, Typography
    var metadataContent by remember { mutableStateOf("") }

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
                        text = "INSPIRATION",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberCyan,
                        letterSpacing = 3.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Mood Board",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = DarkTextPrimary
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Quick view selector
                    IconButton(
                        onClick = { boardMode = if (boardMode == "Canvas") "Grid" else "Canvas" },
                        modifier = Modifier.background(Color(0x0EFFFFFF), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Toggle Mode",
                            tint = CyberCyan
                        )
                    }

                    GlowButton(
                        text = "New Plate",
                        onClick = { showCreateDialog = true },
                        glowColor = CyberCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Subtitle instructions
            Text(
                text = if (boardMode == "Canvas") "Active Interactive Canvas: Drag plates with fingers to position" else "Pinterest-Style Visual Grid",
                style = MaterialTheme.typography.bodySmall,
                color = DarkTextSecondary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (items.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "Empty MoodBoard",
                            tint = DarkTextMuted,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Mood board is blank. Instantiate design concepts.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkTextSecondary
                        )
                    }
                }
            } else {
                if (boardMode == "Canvas") {
                    // Infinite interactive draggable background area
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0x03FFFFFF))
                            .border(1.dp, Color(0x08FFFFFF), RoundedCornerShape(16.dp))
                    ) {
                        // Display items at designated coordinates
                        items.forEach { item ->
                            DraggableItemPlate(item = item, viewModel = viewModel)
                        }
                    }
                } else {
                    // Pinterest grid layout
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(items) { item ->
                            GlassCard(
                                modifier = Modifier.fillMaxWidth(),
                                borderColor = CyberCyan.copy(alpha = 0.3f)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = DarkTextPrimary
                                    )

                                    IconButton(
                                        onClick = { viewModel.deleteMoodItem(item) },
                                        modifier = Modifier.size(20.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = Color.White.copy(alpha = 0.3f),
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                MoodContentPlate(item = item)
                            }
                        }
                    }
                }
            }
        }

        // Animated Dialog: Add Concept Board element
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
                        text = "NEW CONCEPT PLATE",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = CyberCyan,
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text("PALETTE / REFERENCE TITLE", style = MaterialTheme.typography.labelSmall, color = CyberCyan)
                        Spacer(modifier = Modifier.height(8.dp))
                        GlassTextField(
                            value = title,
                            onValueChange = { title = it },
                            placeholder = { Text("E.g. Brutalist Brand Typography...", color = DarkTextMuted) }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("CONCEPT CLASSIFICATION", style = MaterialTheme.typography.labelSmall, color = CyberCyan)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf("ColorPalette", "Typography").forEach { t ->
                                val active = selectedType == t
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (active) CyberCyan else Color(0x10FFFFFF))
                                        .clickable { selectedType = t },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (t == "ColorPalette") "Color Palette" else "Typography Guide",
                                        color = if (active) DeepBlackBg else DarkTextSecondary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = if (selectedType == "ColorPalette") "INPUT COLOR HEX VALUES (CSV)" else "TYPOGRAPHIC PAIRING DETAILS",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberCyan
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        GlassTextField(
                            value = metadataContent,
                            onValueChange = { metadataContent = it },
                            placeholder = {
                                Text(
                                    if (selectedType == "ColorPalette") "E.g. #030303,#FF7D29,#00E5FF,#FFFFFF..."
                                    else "Font: Space Grotesk\nSpacing: 2sp\nStyle: Brutalist",
                                    color = DarkTextMuted
                                )
                            },
                            singleLine = false,
                            modifier = Modifier.height(115.dp)
                        )
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
                            text = "Assemble Concept",
                            onClick = {
                                if (title.isNotEmpty() && metadataContent.isNotEmpty()) {
                                    viewModel.addMoodItem(title, selectedType, metadataContent)
                                    showCreateDialog = false
                                    title = ""
                                    metadataContent = ""
                                }
                            },
                            modifier = Modifier.weight(1f),
                            glowColor = CyberCyan
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DraggableItemPlate(item: MoodItem, viewModel: WorkspaceViewModel) {
    var offsetX by remember { mutableStateOf(item.xOffset) }
    var offsetY by remember { mutableStateOf(item.yOffset) }

    Box(
        modifier = Modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .width(item.width.dp)
            .pointerInput(item) {
                detectDragGestures(
                    onDragEnd = {
                        viewModel.updateMoodItemPosition(item, offsetX, offsetY)
                    }
                ) { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                }
            }
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF16161F))
            .border(1.dp, Color(0x20FFFFFF), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = DarkTextPrimary
                )

                IconButton(
                    onClick = { viewModel.deleteMoodItem(item) },
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.White.copy(alpha = 0.3f),
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            MoodContentPlate(item = item)
        }
    }
}

@Composable
fun MoodContentPlate(item: MoodItem) {
    val context = LocalContext.current

    when (item.type) {
        "ColorPalette" -> {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(8.dp)),
                horizontalArrangement = Arrangement.Start
            ) {
                val hexList = item.content?.split(",") ?: emptyList()
                hexList.forEach { hex ->
                    val color = remember(hex) {
                        try {
                            Color(android.graphics.Color.parseColor(hex.trim()))
                        } catch (e: Exception) {
                            ElectricBlue
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(color)
                            .clickable {
                                Toast
                                    .makeText(context, "Hex copied: $hex", Toast.LENGTH_SHORT)
                                    .show()
                            }
                    )
                }
            }
        }
        else -> {
            // Typography representation
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(CyberCyan.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FormatSize,
                        contentDescription = "Typo",
                        tint = CyberCyan,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = item.content ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkTextSecondary,
                    lineHeight = 14.sp
                )
            }
        }
    }
}
