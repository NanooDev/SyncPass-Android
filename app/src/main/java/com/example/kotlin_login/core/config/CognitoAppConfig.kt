package com.example.kotlin_login.core.config

import com.cognitoauth.CognitoConfig
import com.example.kotlin_login.BuildConfig

/**
 * Punto centralizado de acceso a la configuración de Amazon Cognito.
 * Expone la configuración leída de forma segura desde BuildConfig (.env).
 */
object CognitoAppConfig {
    val config = CognitoConfig(
        issuerUri = BuildConfig.COGNITO_ISSUER,
        clientId = BuildConfig.COGNITO_CLIENT_ID,
        redirectUri = BuildConfig.COGNITO_REDIRECT_URI,
        logoutUri = BuildConfig.COGNITO_LOGOUT_URI,
        scopes = BuildConfig.COGNITO_SCOPES,
    )

    /** Verifica si la configuración está presente para evitar crashes si falta el archivo .env */
    val isConfigured: Boolean =
        BuildConfig.COGNITO_ISSUER.isNotBlank() && BuildConfig.COGNITO_CLIENT_ID.isNotBlank()
}
