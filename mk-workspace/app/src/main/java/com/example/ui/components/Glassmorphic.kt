package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    borderWidth: Dp = 1.dp,
    borderColor: Color = GlassBorderColor,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0x1BFFFFFF), // 10% white start
                        Color(0x0AFFFFFF)  // 4% white end
                    )
                )
            )
            .border(
                width = borderWidth,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        borderColor.copy(alpha = 0.16f), // White frost highlight
                        borderColor.copy(alpha = 0.04f)  // Dark bleed shadow edge
                    )
                ),
                shape = RoundedCornerShape(cornerRadius)
            )
            .padding(18.dp)
    ) {
        Column {
            content()
        }
    }
}

@Composable
fun GlowButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    glowColor: Color = ElectricBlue,
    textColor: Color = Color.White
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(50.dp)
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(
                    colors = listOf(glowColor, CyberCyan)
                ),
                shape = RoundedCornerShape(24.dp)
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0x15000000),
            contentColor = textColor
        ),
        shape = RoundedCornerShape(24.dp),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
    ) {
        androidx.compose.material3.Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = textColor,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            letterSpacing = androidx.compose.ui.unit.TextUnit.Unspecified
        )
    }
}

@Composable
fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: @Composable (() -> Unit)?,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x0EFFFFFF))
            .border(1.dp, Color(0x15FFFFFF), RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f)) {
                if (value.isEmpty() && placeholder != null) {
                    placeholder()
                }
                androidx.compose.foundation.text.BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = singleLine,
                    textStyle = TextStyle(
                        color = Color.White,
                        fontSize = androidx.compose.ui.unit.TextUnit.Unspecified
                    ).copy(color = Color.White),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (trailingIcon != null) {
                trailingIcon()
            }
        }
    }
}

// Custom simple text style for glass text fields
private val TextStyle = androidx.compose.ui.text.TextStyle(
    color = Color.White,
    fontSize = androidx.compose.ui.unit.TextUnit.Unspecified
)
