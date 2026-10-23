package com.example.kotlin_login.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = SyncPassBluePrimary,
    onPrimary = Color.White,
    primaryContainer = SyncPassBlueLight,
    onPrimaryContainer = SyncPassBluePrimary,

    secondary = SyncPassBlueSecondary,
    onSecondary = Color.White,
    secondaryContainer = SyncPassSurfaceVariant,
    onSecondaryContainer = SyncPassTextPrimary,

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

@Composable
fun KotlinloginTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Se prioriza la paleta clara institucional definida para SyncPass
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
