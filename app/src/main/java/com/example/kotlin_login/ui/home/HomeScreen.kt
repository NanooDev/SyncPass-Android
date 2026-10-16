package com.example.kotlin_login.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotlin_login.core.jwt.SessionUser
import com.example.kotlin_login.ui.components.AuthCard
import com.example.kotlin_login.ui.theme.KotlinloginTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(accessToken: String, onLogout: () -> Unit = {}) {
    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(accessToken)
    )
    val state by viewModel.uiState.collectAsState()
    HomeContent(state = state, onLogout = onLogout)
}

@Composable
fun HomeContent(state: HomeUiState, onLogout: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AuthCard(title = "Bienvenido") {
            val user = state.user
            if (user != null) {
                Text("Usuario: ${user.username}", style = MaterialTheme.typography.titleMedium)
                Text("ID (sub): ${user.sub}", style = MaterialTheme.typography.bodyMedium)
                Text(
                    "Roles: ${user.roles.joinToString()}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text("Emisor: ${user.iss}", style = MaterialTheme.typography.bodySmall)
                Text("Emitido: ${formatEpoch(user.iat)}", style = MaterialTheme.typography.bodySmall)
                Text("Expira: ${formatEpoch(user.exp)}", style = MaterialTheme.typography.bodySmall)
                if (user.isExpired) {
                    Text("Sesión expirada, vuelve a ingresar.", color = MaterialTheme.colorScheme.error)
                }
            } else {
                Text(state.decodeError ?: "Cargando sesión...")
            }
            Button(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
                Text("Cerrar sesión")
            }
        }
    }
}

private fun formatEpoch(epochSeconds: Long): String {
    if (epochSeconds <= 0) return "-"
    val fmt = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
    return fmt.format(Date(epochSeconds * 1000))
}

@Preview(showBackground = true)
@Composable
private fun HomeContentPreview() {
    KotlinloginTheme {
        HomeContent(
            state = HomeUiState(
                user = SessionUser(
                    username = "claudiodcv",
                    roles = listOf("superadmin"),
                    iss = "https://172-233-15-71.ip.linodeusercontent.com",
                    sub = "1",
                    iat = 1791481004,
                    exp = 1791484604
                )
            )
        )
    }
}
