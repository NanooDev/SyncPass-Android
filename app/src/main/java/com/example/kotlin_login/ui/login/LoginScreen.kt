package com.example.kotlin_login.ui.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlin_login.core.jwt.JwtDecoder
import com.example.kotlin_login.ui.theme.SyncPassAmber
import com.example.kotlin_login.ui.theme.SyncPassCyan
import com.example.kotlin_login.ui.theme.SyncPassEmerald
import com.example.kotlin_login.ui.theme.SyncPassIndigo
import com.example.kotlin_login.ui.theme.ThemeState

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    isCognitoLoading: Boolean = false,
    onCognitoClick: () -> Unit = {},
    onLoginSuccess: (token: String) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    var showManualForm by remember { mutableStateOf(false) }

    LaunchedEffect(state.loginError) {
        state.loginError?.let {
            snackbar.showSnackbar(it)
            viewModel.onErrorShown()
        }
    }

    LaunchedEffect(state.isSuccess, state.authToken) {
        val token = state.authToken
        if (state.isSuccess && !token.isNullOrBlank()) {
            onLoginSuccess(token)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Bar with Centered Logo and Right-aligned Theme Toggle
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(SyncPassIndigo, SyncPassCyan)
                                    )
                                )
                                .align(Alignment.Center),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "SP",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        // Theme Toggle Icon
                        IconButton(
                            onClick = { ThemeState.toggle() },
                            modifier = Modifier.align(Alignment.CenterEnd)
                        ) {
                            Icon(
                                imageVector = if (ThemeState.isDarkTheme) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                                contentDescription = if (ThemeState.isDarkTheme) "Cambiar a Modo Claro" else "Cambiar a Modo Oscuro",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Portal SyncPass",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = (-0.5).sp
                    )

                    Text(
                        text = "Acreditación Territorial y Operaciones Offline",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp, bottom = 20.dp)
                    )

                    // Primary Cloud Action: AWS Cognito Button
                    Button(
                        onClick = onCognitoClick,
                        enabled = !isCognitoLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        if (isCognitoLoading) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Continuar con Amazon Cognito",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.padding(top = 8.dp, bottom = 18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(SyncPassEmerald)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "User Pool us-east-1_RlB8Ft9Z2 · PKCE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SyncPassEmerald
                        )
                    }

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outline,
                        thickness = 1.dp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Evaluation Roles Section
                    Text(
                        text = "ACCESO DIRECTO POR ROL (EVALUACIÓN)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // 2x2 Interactive Modern Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ModernRoleCard(
                            title = "Docente",
                            subtitle = "Quórum & QR",
                            accentColor = SyncPassIndigo,
                            bgColor = SyncPassIndigo.copy(alpha = 0.12f),
                            modifier = Modifier.weight(1f),
                            onClick = { onLoginSuccess(JwtDecoder.createDemoToken("coordinator")) }
                        )

                        ModernRoleCard(
                            title = "Voluntario",
                            subtitle = "Escaneo Offline",
                            accentColor = SyncPassEmerald,
                            bgColor = SyncPassEmerald.copy(alpha = 0.12f),
                            modifier = Modifier.weight(1f),
                            onClick = { onLoginSuccess(JwtDecoder.createDemoToken("volunteer")) }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ModernRoleCard(
                            title = "Beneficiario",
                            subtitle = "Pase & Encuesta",
                            accentColor = SyncPassCyan,
                            bgColor = SyncPassCyan.copy(alpha = 0.12f),
                            modifier = Modifier.weight(1f),
                            onClick = { onLoginSuccess(JwtDecoder.createDemoToken("attendee")) }
                        )

                        ModernRoleCard(
                            title = "Admin",
                            subtitle = "Métricas RBAC",
                            accentColor = SyncPassAmber,
                            bgColor = SyncPassAmber.copy(alpha = 0.12f),
                            modifier = Modifier.weight(1f),
                            onClick = { onLoginSuccess(JwtDecoder.createDemoToken("admin")) }
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Expand manual login form toggle
                    Text(
                        text = if (showManualForm) "Ocultar formulario manual" else "¿Ingresar con credenciales locales?",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clickable { showManualForm = !showManualForm }
                            .padding(vertical = 4.dp)
                    )

                    if (showManualForm) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = state.username,
                            onValueChange = viewModel::onUsernameChange,
                            label = { Text("Correo institucional") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = state.password,
                            onValueChange = viewModel::onPasswordChange,
                            label = { Text("Contraseña") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            visualTransformation = if (state.isPasswordVisible)
                                VisualTransformation.None
                            else
                                PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = viewModel::onTogglePasswordVisibility) {
                                    Icon(
                                        imageVector = if (state.isPasswordVisible)
                                            Icons.Filled.VisibilityOff
                                        else
                                            Icons.Filled.Visibility,
                                        contentDescription = null
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = viewModel::onLoginClick,
                            enabled = state.canSubmit,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                        ) {
                            Text("Iniciar Sesión Local", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Bottom Server Telemetry
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "API EC2: 54.164.69.142",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Duoc UC · Vinculación Territorial",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        SnackbarHost(hostState = snackbar)
    }
}

@Composable
private fun ModernRoleCard(
    title: String,
    subtitle: String,
    accentColor: Color,
    bgColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(bgColor),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(accentColor)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
