package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.WorkspaceViewModel
import com.example.ui.screens.*
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: WorkspaceViewModel = viewModel()
                val isLoggedIn by viewModel.isLoggedIn.collectAsState()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DeepBlackBg
                ) {
                    if (!isLoggedIn) {
                        LoginScreen(viewModel = viewModel)
                    } else {
                        WorkspaceShell(viewModel = viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun WorkspaceShell(viewModel: WorkspaceViewModel) {
    val activeScreen by viewModel.activeScreen.collectAsState()
    var showNavigatorDrawer by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlackBg)
    ) {
        // High-fidelity background glowing atmospheric canvas mimicking the blur blobs of the Frosted Glass HTML theme
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw a soft glowing cyan/blue sphere at the top right
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x282F7BFF), // Semi-transparent brand blue
                        Color(0x002F7BFF)  // Fading away to transparency
                    ),
                    center = androidx.compose.ui.geometry.Offset(size.width * 0.85f, size.height * 0.15f),
                    radius = size.width * 0.65f
                ),
                radius = size.width * 0.65f,
                center = androidx.compose.ui.geometry.Offset(size.width * 0.85f, size.height * 0.15f)
            )

            // Draw a beautiful soft orange blooming flare at the bottom left to create an extremely rich contrast
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x19FF7D29), // Semi-transparent warm orange
                        Color(0x00FF7D29)  // Fading away
                    ),
                    center = androidx.compose.ui.geometry.Offset(size.width * 0.15f, size.height * 0.85f),
                    radius = size.width * 0.7f
                ),
                radius = size.width * 0.7f,
                center = androidx.compose.ui.geometry.Offset(size.width * 0.15f, size.height * 0.85f)
            )
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent, // Let the atmospheric glow shine through
            bottomBar = {
                // High-legibility custom glassmorphic navigation shortcuts bar - bg-white/10 with high-contrast borders
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .padding(bottom = 12.dp, start = 16.dp, end = 16.dp)
                        .height(64.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color(0x1BFFFFFF)) // Absolute 10% frosted white translucency
                        .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(32.dp)), // Clear frosted edge highlight
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Quick Primary modules icons
                        NavigationShortcut(
                            icon = Icons.Default.Dashboard,
                            label = "Hub",
                            active = activeScreen == "Dashboard",
                            onClick = { viewModel.selectScreen("Dashboard") }
                        )

                        NavigationShortcut(
                            icon = Icons.Default.Notes,
                            label = "Notes",
                            active = activeScreen == "Notes",
                            onClick = { viewModel.selectScreen("Notes") }
                        )

                        // Big center Workspace Drawer launcher button styled as a floating glass orb with outline
                        Box(
                            modifier = Modifier
                                .offset(y = (-14).dp)
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(ElectricBlue, CyberCyan)
                                    )
                                )
                                .border(4.dp, DeepBlackBg, CircleShape)
                                .clickable { showNavigatorDrawer = !showNavigatorDrawer },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (showNavigatorDrawer) Icons.Default.Close else Icons.Default.Apps,
                                contentDescription = "Search modules",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        NavigationShortcut(
                            icon = Icons.Default.AutoAwesome,
                            label = "AI Bot",
                            active = activeScreen == "Assistant",
                            onClick = { viewModel.selectScreen("Assistant") }
                        )

                        NavigationShortcut(
                            icon = Icons.Default.CalendarToday,
                            label = "Timeline",
                            active = activeScreen == "Calendar",
                            onClick = { viewModel.selectScreen("Calendar") }
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
            // Smooth horizontal cross-fade switching between all 11 screens
            AnimatedContent(
                targetState = activeScreen,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "ScreenTransition"
            ) { targetScreen ->
                when (targetScreen) {
                    "Dashboard" -> DashboardScreen(viewModel = viewModel)
                    "Journal" -> JournalScreen(viewModel = viewModel)
                    "Notes" -> NotesScreen(viewModel = viewModel)
                    "Projects" -> ProjectsScreen(viewModel = viewModel)
                    "Kanban" -> KanbanScreen(viewModel = viewModel)
                    "MoodBoard" -> MoodBoardScreen(viewModel = viewModel)
                    "SketchBoard" -> SketchScreen(viewModel = viewModel)
                    "Goals" -> GoalsScreen(viewModel = viewModel)
                    "Vault" -> VaultScreen(viewModel = viewModel)
                    "Assistant" -> AssistantScreen(viewModel = viewModel)
                    "Calendar" -> CalendarScreen(viewModel = viewModel)
                    else -> DashboardScreen(viewModel = viewModel)
                }
            }

            // High-fidelity full workspace sliding panel navigation drawer
            AnimatedVisibility(
                visible = showNavigatorDrawer,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DeepBlackBg.copy(alpha = 0.96f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "MK PERSONAL OPERATING SYSTEM",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberCyan,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                        Text(
                            text = "Central Workspace",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Complete grid containing all 11 modules!
                        val workspacesList = listOf(
                            WorkspaceMeta("Productivity Hub", "Dashboard", "Core system logs", Icons.Default.Dashboard, ElectricBlue),
                            WorkspaceMeta("Daily Journal", "Journal", "Diary reflections", Icons.Default.Book, SoftOrange),
                            WorkspaceMeta("Smart Notes", "Notes", "Rich index logs", Icons.Default.Notes, CyberCyan),
                            WorkspaceMeta("Projects Space", "Projects", "Unlimited project items", Icons.Default.Assignment, ElectricBlue),
                            WorkspaceMeta("Kanban Board", "Kanban", "Tasks status", Icons.Default.ListAlt, SoftOrange),
                            WorkspaceMeta("Inspiration Mood", "MoodBoard", "Pinterest design reference", Icons.Default.Palette, CyberCyan),
                            WorkspaceMeta("Infinite Drawing", "SketchBoard", "Sketch ideas & boards", Icons.Default.Gesture, ElectricBlue),
                            WorkspaceMeta("Goals Tracking", "Goals", "Completion metrics", Icons.Default.EmojiEvents, MutedAmber),
                            WorkspaceMeta("Secure Storage", "Vault", "Filing & assets index", Icons.Default.FolderSpecial, CyberCyan),
                            WorkspaceMeta("AI Digital Assistant", "Assistant", "Gemini planning", Icons.Default.AutoAwesome, SoftOrange),
                            WorkspaceMeta("Workspace Calendar", "Calendar", "Chronology deadlines", Icons.Default.CalendarToday, ElectricBlue)
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            workspacesList.forEach { meta ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(
                                            if (activeScreen == meta.screenKey) Color(0x1F2F7BFF) else Color(0x0EFFFFFF)
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = if (activeScreen == meta.screenKey) ElectricBlue else Color(0x0FFFFFFF),
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .clickable {
                                            viewModel.selectScreen(meta.screenKey)
                                            showNavigatorDrawer = false
                                        }
                                        .padding(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .background(meta.color.copy(alpha = 0.15f), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = meta.icon,
                                                contentDescription = meta.title,
                                                tint = meta.color,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(16.dp))

                                        Column {
                                            Text(
                                                text = meta.title,
                                                style = MaterialTheme.typography.bodyLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                            Text(
                                                text = meta.tagline,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = DarkTextSecondary
                                            )
                                        }

                                        Spacer(modifier = Modifier.weight(1f))

                                        if (activeScreen == meta.screenKey) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Active",
                                                tint = CyberCyan,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(40.dp))
                    }
                }
            }
        }
    }
}
}

@Composable
fun NavigationShortcut(
    icon: ImageVector,
    label: String,
    active: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (active) CyberCyan else DarkTextSecondary,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (active) CyberCyan else DarkTextMuted,
            fontSize = 9.sp,
            fontWeight = if (active) FontWeight.Bold else FontWeight.Normal
        )
    }
}

data class WorkspaceMeta(
    val title: String,
    val screenKey: String,
    val tagline: String,
    val icon: ImageVector,
    val color: Color
)
