package com.example.kotlin_login.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlin_login.ui.components.AuthCard
import com.example.kotlin_login.ui.theme.KotlinloginTheme

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    isCognitoLoading: Boolean = false,
    onCognitoClick: () -> Unit = {},
    onLoginSuccess: (token: String) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    LoginContent(
        state = state,
        isCognitoLoading = isCognitoLoading,
        onCognitoClick = onCognitoClick,
        onUsernameChange = viewModel::onUsernameChange,
        onPasswordChange = viewModel::onPasswordChange,
        onTogglePassword = viewModel::onTogglePasswordVisibility,
        onLoginClick = viewModel::onLoginClick,
        onErrorShown = viewModel::onErrorShown,
        onLoginSuccess = onLoginSuccess
    )
}

@Composable
fun LoginContent(
    state: LoginUiState,
    isCognitoLoading: Boolean = false,
    onCognitoClick: () -> Unit = {},
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePassword: () -> Unit,
    onLoginClick: () -> Unit,
    onErrorShown: () -> Unit,
    onLoginSuccess: (token: String) -> Unit = {}
) {
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(state.loginError) {
        state.loginError?.let {
            snackbar.showSnackbar(it)
            onErrorShown()
        }
    }

    LaunchedEffect(state.isSuccess, state.authToken) {
        val token = state.authToken
        if (state.isSuccess && !token.isNullOrBlank()) {
            onLoginSuccess(token)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AuthCard(
            title = "SyncPass",
            subtitle = "Inicia sesión para acreditar asistencia"
        ) {
            OutlinedTextField(
                value = state.username,
                onValueChange = onUsernameChange,
                label = { Text("Usuario o correo") },
                singleLine = true,
                isError = state.usernameError != null,
                supportingText = { state.usernameError?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.password,
                onValueChange = onPasswordChange,
                label = { Text("Contraseña") },
                singleLine = true,
                isError = state.passwordError != null,
                supportingText = { state.passwordError?.let { Text(it) } },
                visualTransformation = if (state.isPasswordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = onTogglePassword) {
                        Icon(
                            imageVector = if (state.isPasswordVisible)
                                Icons.Filled.VisibilityOff
                            else
                                Icons.Filled.Visibility,
                            contentDescription = if (state.isPasswordVisible)
                                "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onLoginClick() }
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = onLoginClick,
                enabled = state.canSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Ingresar")
                }
            }

            // Separador para opción Cognito
            androidx.compose.material3.HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // Botón Cognito Hosted UI (Paso 6 cognito-auth)
            androidx.compose.material3.OutlinedButton(
                onClick = onCognitoClick,
                enabled = !isCognitoLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                if (isCognitoLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Text("Continuar con Amazon Cognito")
                }
            }
        }
        SnackbarHost(hostState = snackbar)
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginContentPreview() {
    KotlinloginTheme {
        LoginContent(
            state = LoginUiState(username = "juan_perez", password = "Password123"),
            onUsernameChange = {},
            onPasswordChange = {},
            onTogglePassword = {},
            onLoginClick = {},
            onErrorShown = {}
        )
    }
}
