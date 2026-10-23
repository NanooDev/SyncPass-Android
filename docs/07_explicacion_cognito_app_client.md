# Explicación Técnica: ¿Por qué se debe crear un App Client nuevo en AWS Cognito?

> **Servicio:** Amazon Cognito User Pools (AWS)  
> **Proyecto:** SyncPass (Móvil & Web)  
> **Autor:** Mariano Acevedo ([@NanooDev](https://github.com/NanooDev))

---

## 1. Conceptos Fundamentales: User Pool vs. App Client

Para entender por qué se crea una "aplicación nueva" (App Client) dentro de Cognito, es clave diferenciar estos dos niveles:

```text
┌─────────────────────────────────────────────────────────────────┐
│                    AWS COGNITO USER POOL                        │
│             (Base de datos central de usuarios)                 │
│         - Directorio de cuentas (emails, passwords)             │
│         - Grupos de roles (coordinator, volunteer, etc.)        │
│         - Dominio de autenticación Hosted UI                    │
├───────────────────────────────┬─────────────────────────────────┤
│    APP CLIENT 1 (MÓVIL)       │       APP CLIENT 2 (WEB)        │
│   - Tipo: Cliente Público     │   - Tipo: Cliente Público       │
│   - Sin Client Secret         │   - Sin Client Secret           │
│   - Redirect URI:             │   - Redirect URI:               │
│     com.example.kotlinlogin://│     http://localhost:5173/      │
│   - Estilo móvil asignado     │   - Estilo web asignado         │
└───────────────────────────────┴─────────────────────────────────┘
```

1. **User Pool (Grupo de Usuarios):** Es el contenedor global que almacena las credenciales de todos los usuarios de Duoc UC, la configuración de contraseñas, los atributos (nombre, correo) y los grupos de roles (`admin`, `coordinator`, `volunteer`, `attendee`).
2. **App Client (Cliente de Aplicación):** Es la entidad que representa a un **consumidor específico** del User Pool. Le otorga permisos a una aplicación para solicitar autenticación en nombre de esos usuarios.

---

## 2. ¿Por qué la App Móvil necesita su propio App Client separado de la Web?

Existen tres razones técnicas y de seguridad obligatorias:

### Razón 1: Protocolos de Redirección Diferentes (Redirect URIs)
- La **App Móvil Android** no funciona en un dominio web; opera con un **esquema nativo del sistema operativo**:
  ```text
  com.example.kotlinlogin://cognito/callback
  ```
  Android intercepta este esquema mediante un *Intent Filter* en el `AndroidManifest.xml` (`RedirectUriReceiverActivity`) y devuelve el control a la aplicación sin necesidad de servidor web.
- La **Plataforma Web (React)** opera mediante URLs HTTP/HTTPS estándar:
  ```text
  http://localhost:5173/   o   https://syncpass.duocuc.cl/
  ```
  Si se usara un único cliente mezclando esquemas, cualquier cambio en la configuración web podría deshabilitar el flujo de la app móvil.

### Razón 2: Manejo de Secretos (Public Client vs. Confidential Client)
- Las aplicaciones móviles y las Single Page Applications (SPA) son **clientes públicos**: el código se descarga en el teléfono o navegador del usuario. Por esta razón, **nunca deben tener Client Secret**, ya que un atacante podría descompilar el APK o abrir las herramientas de desarrollador y extraer la clave.
- Las APIs de backend (como Java Spring Boot en EC2) son **clientes confidenciales**: se ejecutan en un servidor privado protegido y sí pueden utilizar un Client Secret seguro para comunicarse con Cognito.

### Razón 3: Branding y Estilos del Hosted UI (Causa de "Login pages unavailable")
En la consola de AWS Cognito (versión Managed Login):
- Cada App Client debe tener asignado un **Estilo de Inicio de Sesión** (*Managed login branding*).
- Si el profesor o tú tenían un estilo asignado únicamente al cliente web (`react-spa-client`), la app móvil mostraba el error:
  > *"Login pages unavailable. Please contact an administrator."*
- Al crear el App Client para Android y vincularle su propio estilo en **Creación de marca → Inicio de sesión administrado**, la pantalla de login abre de forma inmediata y sin errores.

---

## 3. Resumen de Valores de Configuración

| Parámetro | Valor para App Móvil Android | Valor para App Web React |
|---|---|---|
| **User Pool ID** | `us-east-1_RlB8Ft9Z2` | `us-east-1_RlB8Ft9Z2` |
| **App Client ID** | `2988naivr3qfmjhhm35k5lubvd` | `2988naivr3qfmjhhm35k5lubvd` (o dedicado) |
| **Client Secret** | Desactivado (None) | Desactivado (None) |
| **Flujo OAuth** | Authorization Code Grant + PKCE | Authorization Code Grant + PKCE |
| **Redirect URI** | `com.example.kotlinlogin://cognito/callback` | `http://localhost:5173/` |
| **Scopes** | `openid`, `email`, `phone` | `openid`, `email`, `phone` |
