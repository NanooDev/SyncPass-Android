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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlin_login.core.jwt.JwtDecoder
import com.example.kotlin_login.ui.theme.SyncPassAmber
import com.example.kotlin_login.ui.theme.SyncPassAmberLight
import com.example.kotlin_login.ui.theme.SyncPassBackground
import com.example.kotlin_login.ui.theme.SyncPassBorder
import com.example.kotlin_login.ui.theme.SyncPassCyan
import com.example.kotlin_login.ui.theme.SyncPassCyanLight
import com.example.kotlin_login.ui.theme.SyncPassEmerald
import com.example.kotlin_login.ui.theme.SyncPassEmeraldLight
import com.example.kotlin_login.ui.theme.SyncPassIndigo
import com.example.kotlin_login.ui.theme.SyncPassIndigoLight
import com.example.kotlin_login.ui.theme.SyncPassSlate800
import com.example.kotlin_login.ui.theme.SyncPassSlate900
import com.example.kotlin_login.ui.theme.SyncPassSurface
import com.example.kotlin_login.ui.theme.SyncPassTextPrimary
import com.example.kotlin_login.ui.theme.SyncPassTextSecondary

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
            .background(SyncPassBackground)
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
                colors = CardDefaults.cardColors(containerColor = SyncPassSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = BorderStroke(1.dp, SyncPassBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Modern Gradient Logo Mark
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(14.dp))
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
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Portal SyncPass",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = SyncPassTextPrimary,
                        letterSpacing = (-0.5).sp
                    )

                    Text(
                        text = "Acreditación Territorial y Operaciones Offline",
                        fontSize = 13.sp,
                        color = SyncPassTextSecondary,
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
                            containerColor = SyncPassSlate900,
                            contentColor = Color.White
                        )
                    ) {
                        if (isCognitoLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
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
                        color = SyncPassBorder,
                        thickness = 1.dp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Evaluation Roles Section
                    Text(
                        text = "ACCESO DIRECTO POR ROL (EVALUACIÓN)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SyncPassTextSecondary,
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
                            bgColor = SyncPassIndigoLight,
                            modifier = Modifier.weight(1f),
                            onClick = { onLoginSuccess(JwtDecoder.createDemoToken("coordinator")) }
                        )

                        ModernRoleCard(
                            title = "Voluntario",
                            subtitle = "Escaneo Offline",
                            accentColor = SyncPassEmerald,
                            bgColor = SyncPassEmeraldLight,
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
                            bgColor = SyncPassCyanLight,
                            modifier = Modifier.weight(1f),
                            onClick = { onLoginSuccess(JwtDecoder.createDemoToken("attendee")) }
                        )

                        ModernRoleCard(
                            title = "Admin",
                            subtitle = "Métricas RBAC",
                            accentColor = SyncPassAmber,
                            bgColor = SyncPassAmberLight,
                            modifier = Modifier.weight(1f),
                            onClick = { onLoginSuccess(JwtDecoder.createDemoToken("admin")) }
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Expand manual login form toggle
                    Text(
                        text = if (showManualForm) "Ocultar formulario manual" else "¿Ingresar con credenciales locales?",
                        color = SyncPassIndigo,
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
                            colors = ButtonDefaults.buttonColors(containerColor = SyncPassIndigo),
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
                    color = SyncPassTextSecondary
                )
                Text(
                    text = "Duoc UC · Vinculación Territorial",
                    fontSize = 11.sp,
                    color = SyncPassTextSecondary
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
            .background(SyncPassSurface)
            .border(1.dp, SyncPassBorder, RoundedCornerShape(12.dp))
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
                color = SyncPassTextPrimary
            )

            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = SyncPassTextSecondary
            )
        }
    }
}