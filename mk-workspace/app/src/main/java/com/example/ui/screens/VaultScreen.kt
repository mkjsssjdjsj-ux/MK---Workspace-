package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.VaultFile
import com.example.ui.WorkspaceViewModel
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassTextField
import com.example.ui.components.GlowButton
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun VaultScreen(viewModel: WorkspaceViewModel) {
    val files by viewModel.vaultFiles.collectAsState()
    val activeFolder by viewModel.selectedVaultFolder.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    var fileName by remember { mutableStateOf("") }
    var selectedFolder by remember { mutableStateOf("Assets") } // Assets, PDFs, Designs, Documents
    var selectedType by remember { mutableStateOf("Image") } // Image, PDF, Document, Design Asset

    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    val filteredFiles = remember(files, activeFolder, searchQuery) {
        files.filter { file ->
            val matchFolder = (activeFolder == "All" || file.folder == activeFolder)
            val matchSearch = (searchQuery.isEmpty() || file.name.contains(searchQuery, ignoreCase = true))
            matchFolder && matchSearch
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
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(imageVector = Icons.Default.Shield, contentDescription = "Secure", tint = CyberCyan, modifier = Modifier.size(16.dp))
                        Text(
                            text = "METADATA VAULT",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberCyan,
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "File Storage",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = DarkTextPrimary
                    )
                }

                GlowButton(
                    text = "Vault In",
                    onClick = { showCreateDialog = true },
                    glowColor = CyberCyan
                )
            }

            // Search Files
            GlassTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search assets by file name...", color = DarkTextMuted) },
                trailingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = CyberCyan)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Folders selection row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val foldersList = listOf("All", "Assets", "PDFs", "Designs", "Documents")
                foldersList.forEach { dir ->
                    val active = activeFolder == dir
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (active) CyberCyan.copy(alpha = 0.15f) else Color(0x06FFFFFF))
                            .border(1.dp, if (active) CyberCyan else Color(0x0AFFFFFF), RoundedCornerShape(12.dp))
                            .clickable { viewModel.setVaultFolder(dir) }
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = dir,
                                tint = if (active) CyberCyan else DarkTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = dir,
                                color = if (active) CyberCyan else DarkTextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredFiles.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.FolderSpecial,
                            contentDescription = "Empty",
                            tint = DarkTextMuted,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "This storage shelf is currently unallocated.",
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
                    items(filteredFiles) { file ->
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            borderColor = GlassBorderColor
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    // Custom visual asset type icons
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .background(Color.White.copy(alpha = 0.04f), RoundedCornerShape(10.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = when (file.type) {
                                                "Image" -> Icons.Default.Image
                                                "PDF" -> Icons.Default.PictureAsPdf
                                                else -> Icons.Default.InsertDriveFile
                                            },
                                            contentDescription = file.type,
                                            tint = when (file.type) {
                                                "Image" -> CyberCyan
                                                "PDF" -> SoftOrange
                                                else -> ElectricBlue
                                            },
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = file.name,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = DarkTextPrimary
                                        )

                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Text(
                                                text = file.folder.uppercase(),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = CyberCyan,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "• ${file.size}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = DarkTextSecondary,
                                                fontSize = 11.sp
                                            )
                                            Text(
                                                text = "• ${dateFormat.format(Date(file.date))}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = DarkTextMuted,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                }

                                IconButton(onClick = { viewModel.deleteVaultFile(file) }) {
                                    Icon(
                                        imageVector = Icons.Default.Add, // Rotate custom adding triggers as deleting
                                        contentDescription = "Delete file",
                                        tint = SoftOrange,
                                        modifier = Modifier.clip(RoundedCornerShape(2.dp))
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Animated Dialog: Upload/Index File
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
                        text = "INDEX SECURE FILE",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = CyberCyan,
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text("ASSET NAME", style = MaterialTheme.typography.labelSmall, color = CyberCyan)
                        Spacer(modifier = Modifier.height(8.dp))
                        GlassTextField(
                            value = fileName,
                            onValueChange = { fileName = it },
                            placeholder = { Text("E.g. branding_mockups_ver2.zip...", color = DarkTextMuted) }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("FILE CLASSIFICATION", style = MaterialTheme.typography.labelSmall, color = CyberCyan)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf("Image", "PDF", "Document").forEach { type ->
                                val active = selectedType == type
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (active) CyberCyan else Color(0x10FFFFFF))
                                        .clickable { selectedType = type },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = type,
                                        color = if (active) DeepBlackBg else DarkTextSecondary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("CHOOSE VAULT FOLDER", style = MaterialTheme.typography.labelSmall, color = CyberCyan)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf("Assets", "PDFs", "Designs", "Documents").forEach { folder ->
                                val active = selectedFolder == folder
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(40.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (active) ElectricBlue else Color(0x10FFFFFF))
                                        .clickable { selectedFolder = folder },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = folder,
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
                            text = "Commit Storage",
                            onClick = {
                                if (fileName.isNotEmpty()) {
                                    viewModel.addVaultFile(
                                        name = fileName,
                                        folder = selectedFolder,
                                        size = "${(2..24).random()}.${(0..9).random()} MB",
                                        type = selectedType
                                    )
                                    showCreateDialog = false
                                    fileName = ""
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
