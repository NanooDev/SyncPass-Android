package com.example.kotlin_login.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ExitToApp
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlin_login.domain.model.UserRole
import com.example.kotlin_login.ui.theme.SyncPassAmber
import com.example.kotlin_login.ui.theme.SyncPassAmberDark
import com.example.kotlin_login.ui.theme.SyncPassCyan
import com.example.kotlin_login.ui.theme.SyncPassEmerald
import com.example.kotlin_login.ui.theme.SyncPassEmeraldDark
import com.example.kotlin_login.ui.theme.SyncPassIndigo
import com.example.kotlin_login.ui.theme.ThemeState

/**
 * Barra superior minimalista estilo iOS con indicador opcional de conectividad.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyncPassTopBar(
    title: String,
    subtitle: String? = null,
    currentRole: UserRole? = null,
    isOffline: Boolean? = null,
    onStatusClick: (() -> Unit)? = null,
    onBackClick: (() -> Unit)? = null,
    onLogoutClick: (() -> Unit)? = null
) {
    Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
        TopAppBar(
            navigationIcon = {
                if (onBackClick != null) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .padding(start = 16.dp, end = 6.dp)
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Brush.linearGradient(listOf(SyncPassIndigo, SyncPassCyan))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "SP",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            title = {
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (!subtitle.isNullOrBlank()) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            },
            actions = {
                if (isOffline != null) {
                    val isDark = ThemeState.isDarkTheme
                    val pillBg = when {
                        isOffline && isDark -> SyncPassAmber
                        isOffline -> Color(0xFFFEF3C7)
                        isDark -> Color(0xFF064E3B)
                        else -> Color(0xFFECFDF5)
                    }
                    val pillFg = when {
                        isOffline && isDark -> Color(0xFF090C12)
                        isOffline -> Color(0xFF92400E)
                        isDark -> Color(0xFF6EE7B7)
                        else -> SyncPassEmeraldDark
                    }
                    val pillBorder = when {
                        isOffline -> SyncPassAmberDark.copy(alpha = 0.45f)
                        isDark -> SyncPassEmerald.copy(alpha = 0.45f)
                        else -> SyncPassEmeraldDark.copy(alpha = 0.25f)
                    }
                    Row(
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .clip(CircleShape)
                            .background(pillBg)
                            .border(1.dp, pillBorder, CircleShape)
                            .then(
                                if (onStatusClick != null) Modifier.clickable(onClick = onStatusClick)
                                else Modifier
                            )
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = if (isOffline) Icons.Rounded.CloudOff else Icons.Rounded.CloudDone,
                            contentDescription = null,
                            tint = pillFg,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = if (isOffline) "Offline" else "En línea",
                            style = MaterialTheme.typography.labelSmall,
                            color = pillFg
                        )
                    }
                }

                IconButton(onClick = { ThemeState.toggle() }) {
                    Icon(
                        imageVector = if (ThemeState.isDarkTheme) Icons.Rounded.LightMode else Icons.Rounded.DarkMode,
                        contentDescription = if (ThemeState.isDarkTheme) "Cambiar a Modo Claro" else "Cambiar a Modo Oscuro",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (onLogoutClick != null) {
                    IconButton(onClick = onLogoutClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ExitToApp,
                            contentDescription = "Cerrar sesión",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )
        HorizontalDivider(
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}
