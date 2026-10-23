package com.example.kotlin_login.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlin_login.domain.model.UserRole
import com.example.kotlin_login.ui.theme.SyncPassBorder
import com.example.kotlin_login.ui.theme.SyncPassCyan
import com.example.kotlin_login.ui.theme.SyncPassEmerald
import com.example.kotlin_login.ui.theme.SyncPassIndigo
import com.example.kotlin_login.ui.theme.SyncPassIndigoLight
import com.example.kotlin_login.ui.theme.SyncPassSurface
import com.example.kotlin_login.ui.theme.SyncPassTextPrimary
import com.example.kotlin_login.ui.theme.SyncPassTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyncPassTopBar(
    title: String,
    subtitle: String? = null,
    currentRole: UserRole? = null,
    onBackClick: (() -> Unit)? = null,
    onLogoutClick: (() -> Unit)? = null
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Modern SP gradient mark
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(SyncPassIndigo, SyncPassCyan)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "SP",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.3).sp
                        ),
                        color = SyncPassTextPrimary
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(SyncPassEmerald)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = subtitle ?: "EC2: 54.164.69.142",
                            style = MaterialTheme.typography.labelSmall,
                            color = SyncPassTextSecondary
                        )
                    }
                }
            }
        },
        navigationIcon = {
            if (onBackClick != null) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = SyncPassTextPrimary
                    )
                }
            }
        },
        actions = {
            if (currentRole != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(SyncPassIndigoLight)
                        .border(1.dp, SyncPassIndigo.copy(alpha = 0.25f), RoundedCornerShape(999.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = currentRole.displayName.split("/").first().trim(),
                        color = SyncPassIndigo,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (onLogoutClick != null) {
                IconButton(onClick = onLogoutClick) {
                    Icon(
                        imageVector = Icons.Filled.ExitToApp,
                        contentDescription = "Cerrar sesión",
                        tint = SyncPassTextSecondary
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = SyncPassSurface
        ),
        modifier = Modifier.border(0.5.dp, SyncPassBorder)
    )
}