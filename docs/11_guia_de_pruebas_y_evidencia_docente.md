# Guía de Pruebas, Validación y Evidencia para Evaluación Docente

> **Institución:** Duoc UC • Escuela de Informática y Telecomunicaciones  
> **Asignatura:** DSY1105 · Desarrollo Móvil y Fullstack  
> **Proyecto:** SyncPass (Plataforma Territorial de Acreditación y Evaluación)  
> **Integrantes (Dupla):**  
> - Mariano Acevedo (`NanooDev` · `marianoacevedo1809@gmail.com`)  
> - Álvaro León (`alv.leon@duocuc.cl`)  
> **Versión:** 2.1.0

---

## 1. Objetivo del Documento

Este documento proporciona una guía paso a paso para la comisión evaluadora docente. Detalla cómo ejecutar, probar y validar cada uno de los componentes de la arquitectura exigidos en la cátedra:
1. Autenticación federada con **AWS Cognito** (Web y Móvil).
2. Control de Acceso Basado en Roles (**RBAC** con 4 perfiles).
3. Acreditación territorial y **Modo Sin Conexión (Offline-First)**.
4. Generación y lectura de **Códigos QR** en terreno.
5. Despliegue de la API en la nube con **AWS EC2**.

---

## 2. Matriz de Casos de Prueba

| ID | Caso de Prueba | Rol Evaluado | Componente | Resultado Esperado |
| :--- | :--- | :--- | :--- | :--- |
| **CP-01** | Login Federado con AWS Cognito (PKCE) | Docente (`coordinador`) | Web (`/login`) | Redirección a Cognito Hosted UI, ingreso de credenciales y retorno a `/dashboard` con nombre y rol activo. |
| **CP-02** | Demostración Rápida por Rol (Web) | 4 Roles | Web (`/login`) | Botones de acceso directo simulan tokens JWT válidos y cargan las vistas correspondientes sin credenciales cloud. |
| **CP-03** | Monitoreo de Quórum y Talleres | Docente / Coordinador | Móvil / Web | Visualización de quórum en vivo (ej. 28/40 cupos) y proyectos comunitarios vigentes. |
| **CP-04** | Generador de Pase QR | Docente | Móvil (`DocenteScreen`) | Renderizado Canvas del código QR del taller para proyección o escaneo en sede comunitaria. |
| **CP-05** | Acreditación y Modo Offline | Estudiante Voluntario | Móvil (`VoluntarioScreen`) | Al activar "Modo Offline", la app guarda registros localmente y muestra contador de sincronización pendiente. |
| **CP-06** | Escáner QR y Fallback por RUT | Estudiante Voluntario | Móvil (`QrComponents`) | Lector simulado con animación láser y campo alternativo de cédula de identidad para contingencia. |
| **CP-07** | Encuesta Inmediata de Satisfacción | Beneficiario | Móvil (`BeneficiarioScreen`) | Formulario ágil de 30 segundos con calificación 1-5 estrellas y comentarios cualitativos. |
| **CP-08** | API Backend en AWS EC2 | Sistema | Servidor Ubuntu | Respuesta HTTP 200 en puerto 80 a través de Nginx Reverse Proxy y Systemd Daemon. |

---

## 3. Protocolo de Pruebas Paso a Paso

### Prueba 1: Autenticación AWS Cognito en Web
1. Iniciar el servidor web local:
   ```bash
   cd C:\dev\web\syncpass-web
   npm run dev
   ```
2. Abrir el navegador en `http://localhost:5173/login`.
3. Hacer clic en el botón institucional **AWS Cognito (SSO)**.
4. Se abrirá la interfaz alojada de Amazon Cognito con el dominio:
   `https://us-east-1rlb8ft9z2.auth.us-east-1.amazoncognito.com`
5. Ingresar credenciales verificadas:
   - **Correo:** `marianoacevedo1809@gmail.com`
   - **Contraseña:** *(la configurada en AWS Cognito)*
6. **Comportamiento esperado:**
   - La barra de direcciones recibe el parámetro temporal `?code=...` (desafío PKCE RFC 7636).
   - El cliente intercambia el código por tokens en `/oauth2/token`.
   - Se decodifica el token ID obteniendo el grupo `coordinador`.
   - Se presenta el Dashboard con el saludo `"Bienvenido, Mariano"` y el distintivo `"Docente / Coordinador"`.

---

### Prueba 2: Navegación y Roles en Android (Jetpack Compose)
1. Abrir el proyecto `C:\dev\android\SyncPass-Android` en Android Studio.
2. Ejecutar la aplicación en emulador o dispositivo físico.
3. En la pantalla de bienvenida (`LoginScreen`), dirigirse a la sección **ACCESO DIRECTO POR ROL (EVALUACIÓN)**:

#### A. Como Docente / Coordinador:
- Presione **Docente / Coordinador**.
- Se abre `DocenteScreen.kt`:
  - Muestra talleres activos: *Alfabetización Digital*, *Huertos Urbanos*, *Primeros Auxilios*.
  - Indicador de quórum con barra de progreso porcentual.
  - Botón **Generar Código QR de Taller** que despliega el canvas 2D nativo.

#### B. Como Estudiante Voluntario:
- Presione **Estudiante Voluntario**.
- Se abre `VoluntarioScreen.kt`:
  - Indicador de conectividad con botón de alternancia: `En Línea` / `Modo Offline`.
  - Botón **Escanear Código QR** con animación de escaneo.
  - Al ingresar un RUT manual y presionar *Registrar*, la asistencia se añade a la lista local. Si está sin conexión, incrementa la insignia amarilla `"X registros pendientes de sincronización"`.
  - Al recuperar conexión, el botón **Sincronizar con Servidor** limpia la cola.

#### C. Como Beneficiario Comunitario:
- Presione **Beneficiario Comunitario**.
- Se abre `BeneficiarioScreen.kt`:
  - Muestra la credencial personal del vecino con su código QR intransferible.
  - Módulo inferior **Encuesta Inmediata**: permite seleccionar de 1 a 5 estrellas, escribir observaciones sobre la actividad comunitaria y registrar feedback en 30 segundos.

---

### Prueba 3: Validación del Backend en AWS EC2
1. Abrir navegador o terminal y consultar la IP pública del servidor EC2:
   ```bash
   curl -I http://<IP_PUBLICA_EC2>/
   ```
2. **Respuesta esperada:** Cabecera HTTP `HTTP/1.1 200 OK` servida por Nginx, demostrando que la API Spring Boot (`fs2-api`) se encuentra ejecutándose bajo el demonio Systemd.

---

## 4. Evidencia de Colaboración en Git
- Rama principal `master` al día con commits verificados.
- Rama de trabajo [NanooDev/SyncPass-Android/tree/feature/alvaro-offline-sync](https://github.com/NanooDev/SyncPass-Android/tree/feature/alvaro-offline-sync) con commits firmados por Álvaro León (`alv.leon@duocuc.cl`).
- Pull Request #1 cerrado y fusionado con revisión de pares.
