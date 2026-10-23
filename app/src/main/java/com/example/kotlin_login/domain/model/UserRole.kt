package com.example.kotlin_login.domain.model

/**
 * Matriz de Roles Institucionales para SyncPass (RBAC).
 * Define los 4 perfiles de acceso requeridos en el ecosistema territorial.
 */
enum class UserRole(val slug: String, val displayName: String, val description: String) {
    DOCENTE(
        slug = "coordinator",
        displayName = "Docente / Coordinador",
        description = "Planificación de talleres, quórum y generación de pases QR"
    ),
    VOLUNTARIO(
        slug = "volunteer",
        displayName = "Estudiante Voluntario",
        description = "Acreditación en terreno, escaneo QR y registro offline"
    ),
    BENEFICIARIO(
        slug = "attendee",
        displayName = "Beneficiario Comunitario",
        description = "Pase digital personal y encuesta inmediata de satisfacción"
    ),
    ADMIN(
        slug = "admin",
        displayName = "Administrador General",
        description = "Supervisión institucional, métricas territoriales y roles"
    );

    companion object {
        fun fromSlug(slug: String?): UserRole {
            return when (slug?.lowercase()) {
                "coordinator", "docente", "profesor" -> DOCENTE
                "volunteer", "voluntario" -> VOLUNTARIO
                "attendee", "beneficiario" -> BENEFICIARIO
                "admin", "superadmin" -> ADMIN
                else -> DOCENTE
            }
        }
    }
}
