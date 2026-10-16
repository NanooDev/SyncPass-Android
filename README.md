# SyncPass • Duoc UC (Fullstack 2 & Desarrollo Móvil)

Sistema de acreditación y vinculación comunitaria desarrollado para **Duoc UC** (Asignatura DSY1105 / Fullstack 2 y Móvil).

---

## 📌 Requerimientos del Proyecto

1. **Login con Servicio JWT:** Autenticación centralizada conectada al backend (`/auth/login`).
2. **4 Roles -> 4 Vistas (Wireframes):**
   - **Docente / Coordinador:** Dashboard de proyectos comunitarios, monitoreo en vivo y reportes de vinculación.
   - **Estudiante Voluntario:** Acreditación móvil con escáner de pases QR, búsqueda manual y soporte offline (SQLite / Room).
   - **Beneficiario / Vecino:** Pase digital con código QR de acceso rápido y encuesta de satisfacción inmediata.
   - **Administrador / Superadmin:** Gestión centralizada de sedes Duoc UC, asignación de roles y auditoría de sincronización.
3. **Modelo de Base de Datos:** DDL relacional documentado en [`docs/syncpass-ddl.sql`](./docs/syncpass-ddl.sql).
4. **CRUD Negocio API & Local:**
   - **Remoto:** Conexión REST a API en la nube (Linode / AWS EC2).
   - **Local:** Persistencia local offline con Room / SQLite en Android.

---

## 🎨 Mockups y Wireframes (Estilo Figma)

Los wireframes de baja/media fidelidad para las 4 vistas están disponibles en:
- [`docs/mockups/syncpass-figma-mockups.html`](./docs/mockups/syncpass-figma-mockups.html)

Para visualizarlos, basta con abrir el archivo HTML en cualquier navegador web. Incluye selector de vistas individuales y vista completa de tablero (board Figma).

---

## 🛠️ Tecnologías Utilizadas

- **Android Studio & Kotlin:** Jetpack Compose, Material 3, ViewModel, StateFlow.
- **Networking:** Retrofit 2, OkHttp 3, Gson Converter.
- **Seguridad:** Decodificación JWT cliente (`JwtDecoder`) para extracción de claims (`sub`, `role`, `authorities`).
- **Persistencia Local:** SQLite / Room DB para contingencia offline en juntas vecinales.
- **Web Frontend:** React + TypeScript + Wouter (`proyecto-react-fs2`).
