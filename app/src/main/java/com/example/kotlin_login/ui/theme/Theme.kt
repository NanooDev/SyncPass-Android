package com.example.kotlin_login.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

/**
 * Gestor global de estado reactivo para Modo Claro y Modo Oscuro en SyncPass Android.
 * Permite alternar la interfaz de forma dinámica y sincronizada entre todas las pantallas.
 */
object ThemeState {
    var isDarkTheme by mutableStateOf(false)

    fun toggle() {
        isDarkTheme = !isDarkTheme
    }
}

// Paleta Modo Claro (Clean SaaS & Corporate Tech)
private val LightColorScheme = lightColorScheme(
    primary = SyncPassIndigo,
    onPrimary = Color.White,
    primaryContainer = SyncPassIndigoLight,
    onPrimaryContainer = SyncPassIndigoDark,

    secondary = SyncPassCyan,
    onSecondary = Color.White,
    secondaryContainer = SyncPassCyanLight,
    onSecondaryContainer = SyncPassSlate900,

    tertiary = SyncPassEmerald,
    onTertiary = Color.White,
    tertiaryContainer = SyncPassEmeraldLight,
    onTertiaryContainer = SyncPassEmerald,

    background = SyncPassBackground,
    onBackground = SyncPassTextPrimary,

    surface = SyncPassSurface,
    onSurface = SyncPassTextPrimary,
    surfaceVariant = SyncPassSurfaceVariant,
    onSurfaceVariant = SyncPassTextSecondary,

    outline = SyncPassBorder,
    outlineVariant = SyncPassBorder,

    error = SyncPassRose,
    onError = Color.White,
    errorContainer = SyncPassRoseLight,
    onErrorContainer = SyncPassRose
)

// Paleta Modo Oscuro (Midnight Obsidian & High-Contrast Cyber Indigo)
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF818CF8),
    onPrimary = Color(0xFF0B0F19),
    primaryContainer = Color(0xFF312E81),
    onPrimaryContainer = Color(0xFFEEF2FF),

    secondary = Color(0xFF38BDF8),
    onSecondary = Color(0xFF0B0F19),
    secondaryContainer = Color(0xFF0C4A6E),
    onSecondaryContainer = Color(0xFFE0F2FE),

    tertiary = Color(0xFF10B981),
    onTertiary = Color(0xFF0B0F19),
    tertiaryContainer = Color(0xFF064E3B),
    onTertiaryContainer = Color(0xFFD1FAE5),

    background = Color(0xFF0B0F19),
    onBackground = Color(0xFFF8FAFC),

    surface = Color(0xFF131B2E),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = Color(0xFF94A3B8),

    outline = Color(0xFF293548),
    outlineVariant = Color(0xFF1E293B),

    error = Color(0xFFF87171),
    onError = Color(0xFF450A0A),
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFEE2E2)
)

@Composable
fun KotlinloginTheme(
    darkTheme: Boolean = ThemeState.isDarkTheme,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
