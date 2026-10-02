package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CompatibilityLevel
import com.example.ui.theme.FabricLoaderColor
import com.example.ui.theme.ForgeLoaderColor
import com.example.ui.theme.VanillaColor

@Composable
fun LoaderBadge(
    loader: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = when (loader.lowercase()) {
        "fabric" -> Triple(FabricLoaderColor.copy(alpha = 0.2f), FabricLoaderColor, "Fabric")
        "forge" -> Triple(ForgeLoaderColor.copy(alpha = 0.2f), ForgeLoaderColor, "Forge")
        else -> Triple(VanillaColor.copy(alpha = 0.2f), VanillaColor, "Vanilla")
    }

    Box(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(6.dp))
            .border(1.dp, textColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun VersionChip(
    version: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Color(0xFF21262D), RoundedCornerShape(6.dp))
            .border(1.dp, Color(0xFF30363D), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = version,
            color = Color(0xFFE6EDF3),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun CompatibilityBadge(
    level: CompatibilityLevel,
    modifier: Modifier = Modifier
) {
    val (color, icon) = when (level) {
        CompatibilityLevel.VERIFIED_COMPATIBLE -> Color(0xFF10B981) to Icons.Default.CheckCircle
        CompatibilityLevel.COMPATIBLE_WITH_CAVEATS -> Color(0xFFF59E0B) to Icons.Default.Warning
        CompatibilityLevel.TERRAIN_INCOMPATIBLE -> Color(0xFFEF4444) to Icons.Default.Warning
        CompatibilityLevel.UNVERIFIED_UNKNOWN -> Color(0xFF9CA3AF) to Icons.Default.Info
    }

    Row(
        modifier = modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
            .border(1.dp, color.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = level.label,
            tint = color,
            modifier = Modifier.size(13.dp)
        )
        Text(
            text = " " + level.label,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
