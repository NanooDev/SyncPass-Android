import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

val envFile = rootProject.file(".env")
val envProps = Properties()
if (envFile.exists()) {
    envFile.inputStream().use { envProps.load(it) }
}
fun envOr(key: String, fallback: String): String =
    envProps.getProperty(key) ?: System.getenv(key) ?: fallback

// Prioridad: .env > variable de entorno del sistema > fallback solo para compilar
val apiBaseUrl: String =
    envOr("API_BASE_URL", "https://172-233-15-71.ip.linodeusercontent.com/")

val cognitoRedirectDefault = "com.example.kotlin_login://cognito/callback"
val cognitoIssuer: String = envOr("COGNITO_ISSUER", "https://cognito-idp.us-east-1.amazonaws.com/")
val cognitoClientId: String = envOr("COGNITO_CLIENT_ID", "2988naivr3qfmjhhm35k5lubvd")
val cognitoRedirect: String = envOr("COGNITO_REDIRECT_URI", cognitoRedirectDefault)
val cognitoLogout: String = envOr("COGNITO_LOGOUT_URI", cognitoRedirect)
val cognitoScopes: String = envOr("COGNITO_SCOPES", "email openid phone")

android {
    namespace = "com.example.kotlin_login"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.kotlin_login"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        manifestPlaceholders["appAuthRedirectScheme"] = "com.example.kotlin_login"

        buildConfigField("String", "API_BASE_URL", "\"$apiBaseUrl\"")
        buildConfigField("String", "COGNITO_ISSUER", "\"$cognitoIssuer\"")
        buildConfigField("String", "COGNITO_CLIENT_ID", "\"$cognitoClientId\"")
        buildConfigField("String", "COGNITO_REDIRECT_URI", "\"$cognitoRedirect\"")
        buildConfigField("String", "COGNITO_LOGOUT_URI", "\"$cognitoLogout\"")
        buildConfigField("String", "COGNITO_SCOPES", "\"$cognitoScopes\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp.logging)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    // Navigation Compose
    implementation(libs.androidx.navigation.compose)

    // Módulo reutilizable de login Cognito
    implementation(project(":cognito-auth"))
}