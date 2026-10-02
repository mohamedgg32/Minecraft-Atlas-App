package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldGreen,
    onPrimary = Color(0xFF003822),
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = OnPrimaryContainerDark,
    secondary = FabricLoaderColor,
    onSecondary = Color(0xFF00363F),
    secondaryContainer = SecondaryContainerDark,
    onSecondaryContainer = Color(0xFFBAF0F8),
    tertiary = CopperOrange,
    onTertiary = Color(0xFF451900),
    tertiaryContainer = Color(0xFF6B2900),
    onTertiaryContainer = Color(0xFFFFDBCF),
    background = ForgeDarkBackground,
    onBackground = TextPrimaryDark,
    surface = ForgeDarkSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = ForgeDarkSurfaceVariant,
    onSurfaceVariant = TextSecondaryDark,
    outline = ForgeDarkBorder,
    outlineVariant = Color(0xFF262C36),
    error = RedstoneRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF00875A),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFA3F1D1),
    onPrimaryContainer = Color(0xFF002114),
    secondary = Color(0xFF007A8D),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFB4EBF4),
    onSecondaryContainer = Color(0xFF002026),
    tertiary = Color(0xFFB45309),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFDDB8),
    onTertiaryContainer = Color(0xFF381400),
    background = Color(0xFFF6F8FA),
    onBackground = Color(0xFF1F2328),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1F2328),
    surfaceVariant = Color(0xFFEAEEF2),
    onSurfaceVariant = Color(0xFF57606A),
    outline = Color(0xFFD0D7DE),
    error = Color(0xFFCF222E),
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to sleek Minecraft Java dark mode
    dynamicColor: Boolean = false, // Keep distinct branding
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
