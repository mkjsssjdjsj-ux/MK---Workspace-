package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SketchElement
import com.example.ui.WorkspaceViewModel
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassTextField
import com.example.ui.components.GlowButton
import com.example.ui.theme.*

// local helper representations for real-time strokes
data class StrokeLine(
    val points: List<Offset>,
    val color: Color,
    val thickness: Float,
    val isHighlighter: Boolean = false
)

data class ShapeStamp(
    val position: Offset,
    val type: String, // Rectangle, Circle, Line
    val color: Color,
    val size: Float = 80f
)

data class MindMapNode(
    val position: Offset,
    val text: String,
    val color: Color
)

@Composable
fun SketchScreen(viewModel: WorkspaceViewModel) {
    // In-memory lists for lightning-fast responsive updates
    val strokes = remember { mutableStateListOf<StrokeLine>() }
    val shapes = remember { mutableStateListOf<ShapeStamp>() }
    val textNodes = remember { mutableStateListOf<MindMapNode>() }

    var currentTool by remember { mutableStateOf("Pen") } // Pen, Pencil, Highlighter, Shapes, Text
    var selectedColorHex by remember { mutableStateOf("#00E5FF") } // Cyan primary
    var currentShapeType by remember { mutableStateOf("Rectangle") } // Rectangle, Circle, Connector
    var textNodeValue by remember { mutableStateOf("") }

    val currentBrushColor = remember(selectedColorHex) { Color(android.graphics.Color.parseColor(selectedColorHex)) }
    val currentBrushThickness = remember(currentTool) {
        when (currentTool) {
            "Pencil" -> 2.5f
            "Highlighter" -> 35f
            else -> 8f // Pen
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
                        text = "BRAINSTORMING",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberCyan,
                        letterSpacing = 3.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Infinite Canvas",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = DarkTextPrimary
                    )
                }

                IconButton(
                    onClick = {
                        strokes.clear()
                        shapes.clear()
                        textNodes.clear()
                    },
                    modifier = Modifier
                        .background(Color(0x0EFFFFFF), CircleShape)
                        .border(1.dp, Color(0x20FFFFFF), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Clear Canvas",
                        tint = SoftOrange
                    )
                }
            }

            // Toolbar block
            GlassCard(modifier = Modifier.fillMaxWidth(), borderWidth = 1.dp) {
                // First Row: Tools Selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf("Pen", "Pencil", "Highlighter", "Shapes", "Text").forEach { tool ->
                        val active = currentTool == tool
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (active) Color(0x1F00E5FF) else Color.Transparent)
                                .clickable { currentTool = tool }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = when (tool) {
                                    "Pen" -> Icons.Default.Edit
                                    "Pencil" -> Icons.Default.Create
                                    "Highlighter" -> Icons.Default.FormatPaint
                                    "Shapes" -> Icons.Default.Category
                                    else -> Icons.Default.Title
                                },
                                contentDescription = tool,
                                tint = if (active) CyberCyan else DarkTextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = tool,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (active) CyberCyan else DarkTextSecondary,
                                fontSize = 9.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Second Row: Palette Colors Selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Quick Colors
                    val colors = listOf("#FFFFFF", "#2F7BFF", "#00E5FF", "#FFFF7D29", "#E91E63")
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        colors.forEach { hex ->
                            val active = selectedColorHex == hex
                            val colorValue = remember { Color(android.graphics.Color.parseColor(hex)) }
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(colorValue)
                                    .border(
                                        width = 1.5.dp,
                                        color = if (active) Color.White else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { selectedColorHex = hex }
                            )
                        }
                    }

                    // Contextual tools (Shapes types selector or Text Node input)
                    if (currentTool == "Shapes") {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("Rectangle", "Circle", "Connector").forEach { shape ->
                                val active = currentShapeType == shape
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (active) ElectricBlue else Color(0x10FFFFFF))
                                        .clickable { currentShapeType = shape }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = shape,
                                        color = if (active) Color.White else DarkTextSecondary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    } else if (currentTool == "Text") {
                        Box(modifier = Modifier.width(150.dp).height(36.dp)) {
                            GlassTextField(
                                value = textNodeValue,
                                onValueChange = { textNodeValue = it },
                                placeholder = { Text("Anchor node...", fontSize = 11.sp, color = DarkTextMuted) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Canvas drawing pane
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF070709))
                    .border(2.dp, Color(0x0FFFFFFF), RoundedCornerShape(16.dp))
                    .pointerInput(currentTool, selectedColorHex, currentShapeType, textNodeValue) {
                        detectDragGestures(
                            onDragStart = { startOffset ->
                                if (currentTool == "Pen" || currentTool == "Pencil" || currentTool == "Highlighter") {
                                    strokes.add(
                                        StrokeLine(
                                            points = listOf(startOffset),
                                            color = currentBrushColor,
                                            thickness = currentBrushThickness,
                                            isHighlighter = currentTool == "Highlighter"
                                        )
                                    )
                                }
                            }
                        ) { change, dragAmount ->
                            change.consume()
                            if (currentTool == "Pen" || currentTool == "Pencil" || currentTool == "Highlighter") {
                                val lastStroke = strokes.lastOrNull()
                                if (lastStroke != null) {
                                    val updatedPoints = lastStroke.points + change.position
                                    strokes[strokes.size - 1] = lastStroke.copy(points = updatedPoints)
                                }
                            }
                        }
                    }
                    .pointerInput(currentTool, selectedColorHex, currentShapeType, textNodeValue) {
                        detectTapGestures { tapPosition ->
                            when (currentTool) {
                                "Shapes" -> {
                                    shapes.add(
                                        ShapeStamp(
                                            position = tapPosition,
                                            type = currentShapeType,
                                            color = currentBrushColor
                                        )
                                    )
                                }
                                "Text" -> {
                                    if (textNodeValue.isNotEmpty()) {
                                        textNodes.add(
                                            MindMapNode(
                                                position = tapPosition,
                                                text = textNodeValue,
                                                color = currentBrushColor
                                            )
                                        )
                                        textNodeValue = ""
                                    }
                                }
                            }
                        }
                    }
            ) {
                // Interactive Compose Canvas Drawing Block
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // 1. Draw Highlighters first (so they go underneath thin pen strokes)
                    strokes.filter { it.isHighlighter }.forEach { stroke ->
                        if (stroke.points.size > 1) {
                            val path = Path().apply {
                                val first = stroke.points.first()
                                moveTo(first.x, first.y)
                                stroke.points.drop(1).forEach { pt ->
                                    lineTo(pt.x, pt.y)
                                }
                            }
                            drawPath(
                                path = path,
                                color = stroke.color.copy(alpha = 0.35f),
                                style = Stroke(
                                    width = stroke.thickness,
                                    cap = StrokeCap.Round
                                )
                            )
                        }
                    }

                    // 2. Draw regular pens / pencil lines
                    strokes.filter { !it.isHighlighter }.forEach { stroke ->
                        if (stroke.points.size > 1) {
                            val path = Path().apply {
                                val first = stroke.points.first()
                                moveTo(first.x, first.y)
                                stroke.points.drop(1).forEach { pt ->
                                    lineTo(pt.x, pt.y)
                                }
                            }
                            drawPath(
                                path = path,
                                color = stroke.color,
                                style = Stroke(
                                    width = stroke.thickness,
                                    cap = StrokeCap.Round
                                )
                            )
                        }
                    }

                    // 3. Draw visual shape markers
                    shapes.forEach { stamp ->
                        when (stamp.type) {
                            "Rectangle" -> {
                                drawRect(
                                    color = stamp.color,
                                    topLeft = Offset(stamp.position.x - stamp.size / 2, stamp.position.y - stamp.size / 2),
                                    size = Size(stamp.size, stamp.size),
                                    style = Stroke(width = 3f)
                                )
                            }
                            "Circle" -> {
                                drawCircle(
                                    color = stamp.color,
                                    center = stamp.position,
                                    radius = stamp.size / 2,
                                    style = Stroke(width = 3f)
                                )
                            }
                            else -> {
                                // Connector lines
                                drawLine(
                                    color = stamp.color,
                                    start = Offset(stamp.position.x - 40f, stamp.position.y),
                                    end = Offset(stamp.position.x + 40f, stamp.position.y),
                                    strokeWidth = 3f
                                )
                            }
                        }
                    }
                }

                // 4. Overlap Mind-map labels using standard text node boxes
                textNodes.forEach { node ->
                    Box(
                        modifier = Modifier
                            .offset(node.position.x.dp / 2.7f, node.position.y.dp / 2.7f) // approximate offset projection
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1F1F24))
                            .border(1.dp, node.color, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = node.text,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Empty Hint overlays
                if (strokes.isEmpty() && shapes.isEmpty() && textNodes.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Tap tool to select, draw directly with finger on canvas.\nTap anywhere to drop custom Text Nodes.",
                            style = MaterialTheme.typography.bodySmall,
                            color = DarkTextMuted,
                            textAlign = TextAlign.Center,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Help info row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 60.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Live Canvas drawing is operational",
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkTextMuted
                )
            }
        }
    }
}
