package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Note
import com.example.ui.WorkspaceViewModel
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassTextField
import com.example.ui.components.GlowButton
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NotesScreen(viewModel: WorkspaceViewModel) {
    val notes by viewModel.notes.collectAsState()
    val noteSearchQuery by viewModel.noteSearchQuery.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedNoteForView by remember { mutableStateOf<Note?>(null) }

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Ideas") } // Ideas, Business, Design, YouTube, Learning
    var tags by remember { mutableStateOf("") }

    var selectedFilterCategory by remember { mutableStateOf("All") }

    val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())

    val filteredNotes = remember(notes, selectedFilterCategory) {
        if (selectedFilterCategory == "All") {
            notes
        } else {
            notes.filter { it.category.equals(selectedFilterCategory, ignoreCase = true) }
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
                        text = "CREATIVITY",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberCyan,
                        letterSpacing = 3.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Smart Notes",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = DarkTextPrimary
                    )
                }

                GlowButton(
                    text = "Add Note",
                    onClick = { showCreateDialog = true },
                    glowColor = CyberCyan
                )
            }

            // Search Bar
            GlassTextField(
                value = noteSearchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Search notes by title or keyword...", color = DarkTextMuted) },
                trailingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = CyberCyan)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Category filter chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filterChips = listOf("All", "Ideas", "Business", "Design", "YouTube", "Learning")
                filterChips.forEach { cat ->
                    val active = selectedFilterCategory == cat
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (active) CyberCyan.copy(alpha = 0.2f) else Color(0x06FFFFFF))
                            .border(1.dp, if (active) CyberCyan else Color(0x10FFFFFF), RoundedCornerShape(20.dp))
                            .clickable { selectedFilterCategory = cat }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = cat,
                            color = if (active) CyberCyan else DarkTextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredNotes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.HistoryEdu,
                            contentDescription = "Empty Notes",
                            tint = DarkTextMuted,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No notes found in this workspace.",
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
                    items(filteredNotes) { note ->
                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedNoteForView = note },
                            borderColor = GlassBorderColor
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = note.category.uppercase(),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = SoftOrange,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "• ${dateFormat.format(Date(note.date))}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = DarkTextMuted
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = note.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = DarkTextPrimary,
                                        maxLines = 1
                                    )

                                    if (note.tags.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            note.tags.split(",").forEach { tag ->
                                                Text(
                                                    text = "#${tag.trim()}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = CyberCyan,
                                                    fontSize = 11.sp
                                                )
                                            }
                                        }
                                    }
                                }

                                Row {
                                    IconButton(onClick = { selectedNoteForView = note }) {
                                        Icon(imageVector = Icons.Default.Visibility, contentDescription = "View note", tint = CyberCyan)
                                    }
                                    IconButton(onClick = { viewModel.deleteNote(note.id) }) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete note", tint = Color.White.copy(alpha = 0.4f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Animated Dialog: View details (Markdown simulated Preview)
        AnimatedVisibility(
            visible = selectedNoteForView != null,
            enter = fadeIn() + slideInHorizontally(),
            exit = fadeOut() + slideOutHorizontally()
        ) {
            val viewNote = selectedNoteForView
            if (viewNote != null) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DeepBlackBg
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = viewNote.category.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                color = SoftOrange,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )

                            Button(
                                onClick = { selectedNoteForView = null },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.08f)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Back", color = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = viewNote.title,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = DarkTextPrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            viewNote.tags.split(",").forEach { tag ->
                                Text(
                                    text = "#${tag.trim()}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = CyberCyan
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Obsidian Style Markdown visual rendering container
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0x0EFFFFFF)),
                            border = BorderStroke(1.dp, Color(0x10FFFFFF)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "OBSIDIAN PREVIEW ENGINE",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = DarkTextMuted,
                                    letterSpacing = 2.sp
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                // Simple parsing visual simulation
                                viewNote.content.split("\n").forEach { line ->
                                    when {
                                        line.startsWith("# ") -> {
                                            Text(
                                                text = line.removePrefix("# "),
                                                style = MaterialTheme.typography.headlineSmall,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color.White,
                                                modifier = Modifier.padding(vertical = 6.dp)
                                            )
                                        }
                                        line.startsWith("## ") -> {
                                            Text(
                                                text = line.removePrefix("## "),
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = ElectricBlue,
                                                modifier = Modifier.padding(vertical = 4.dp)
                                            )
                                        }
                                        line.startsWith("1. ") || line.startsWith("- ") -> {
                                            Text(
                                                text = line,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = CyberCyan,
                                                fontFamily = FontFamily.Monospace,
                                                modifier = Modifier.padding(start = 12.dp, top = 2.dp, bottom = 2.dp)
                                            )
                                        }
                                        else -> {
                                            if (line.isNotEmpty()) {
                                                Text(
                                                    text = line,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = DarkTextSecondary,
                                                    lineHeight = 22.sp,
                                                    modifier = Modifier.padding(vertical = 4.dp)
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
        }

        // Animated Dialog: Add/Create Note
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
                        text = "NEW SMART NOTE",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = CyberCyan,
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text("NOTE TITLE", style = MaterialTheme.typography.labelSmall, color = CyberCyan)
                        Spacer(modifier = Modifier.height(8.dp))
                        GlassTextField(
                            value = title,
                            onValueChange = { title = it },
                            placeholder = { Text("E.g. Content Pillars for Business...", color = DarkTextMuted) }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("CATEGORY INDEX", style = MaterialTheme.typography.labelSmall, color = CyberCyan)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf("Ideas", "Business", "Design", "YouTube", "Learning").forEach { cat ->
                                val active = selectedCategory == cat
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(34.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (active) CyberCyan else Color(0x10FFFFFF))
                                        .clickable { selectedCategory = cat },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = cat,
                                        color = if (active) DeepBlackBg else DarkTextSecondary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("TAGS (COMMA SEPARATED)", style = MaterialTheme.typography.labelSmall, color = CyberCyan)
                        Spacer(modifier = Modifier.height(8.dp))
                        GlassTextField(
                            value = tags,
                            onValueChange = { tags = it },
                            placeholder = { Text("UI, Roadmap, Ideas...", color = DarkTextMuted) }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("CONTENT markdown support", style = MaterialTheme.typography.labelSmall, color = CyberCyan)
                        Spacer(modifier = Modifier.height(8.dp))
                        GlassTextField(
                            value = content,
                            onValueChange = { content = it },
                            placeholder = { Text("Write content here...\nUse # for Header\nUse - for Bullet list", color = DarkTextMuted) },
                            singleLine = false,
                            modifier = Modifier.height(180.dp)
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
                            text = "Save Note",
                            onClick = {
                                if (title.isNotEmpty() && content.isNotEmpty()) {
                                    viewModel.addNote(title, content, selectedCategory, tags)
                                    showCreateDialog = false
                                    title = ""
                                    content = ""
                                    tags = ""
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
