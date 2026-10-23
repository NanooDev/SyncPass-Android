# Protocolo de Cierre, Encendido en Clases y Presentación Docente

> **Proyecto:** SyncPass (Plataforma Territorial de Acreditación y Evaluación)  
> **Institución:** Duoc UC • DSY1105  
> **Integrantes:** Mariano Acevedo (`NanooDev`) y Álvaro León (`alv.leon@duocuc.cl`)  
> **Versión:** 1.0.0

---

## 1. Cómo Cerrar y Apagar Todo Hoy

### A. En tu Computador Local
1. **Terminales de desarrollo:**  
   - En la terminal donde esté corriendo `npm run dev`, presiona `Ctrl + C` para detener el servidor web local.
   - Cierra las ventanas de PowerShell o Git Bash.
2. **Pestaña del Navegador con la consola de Ubuntu (EC2 Instance Connect):**  
   - Simplemente **cierra la pestaña del navegador**.  
   - Como dejamos configurado el servicio como un demonio de segundo plano (**Systemd**), la API de Java y PostgreSQL seguirán corriendo en el servidor sin importar que cierres la ventana.

---

### B. En AWS (Gestión de la Máquina EC2)

Tienes dos opciones según tu preferencia:

#### Opción 1: Dejar la máquina encendida (Recomendada para mantener la IP fija)
- **Costo:** **$0.00 (Totalmente Gratis).** La Capa Gratuita de AWS (Free Tier) incluye **750 horas mensuales** de instancias `t2.micro`. Como un mes tiene 720 horas, una sola máquina `t2.micro` puede estar encendida las 24 horas del día durante todo el mes sin generar ningún costo.
- **Ventaja:** La IP pública (**`54.204.137.182`**) no cambiará y no tendrás que modificar ningún archivo antes de clases.

#### Opción 2: Detener la máquina (Si prefieres apagarla por seguridad)
1. En AWS Console -> **EC2** -> **Instancias**.
2. Selecciona `syncpass-api-server`.
3. Haz clic en **Estado de la instancia** -> **Detener la instancia** (Stop instance).
   > **ADVERTENCIA CRÍTICA:** Haz clic en **Detener** (Stop). **NUNCA hagas clic en "Terminar la instancia" (Terminate)**, porque "Terminar" en AWS significa borrar y destruir la máquina.
4. **Al volver a encenderla en clases:**  
   - Seleccionas la máquina -> **Estado de la instancia** -> **Iniciar la instancia** (Start instance).
   - AWS le asignará una nueva IP pública. Solo copias esa nueva IP y la pegas en el archivo `.env` de Android y Web.
   - **¡No necesitas configurar nada más!** PostgreSQL, Nginx y la API Java de Spring Boot se encenderán solas automáticamente al arrancar el servidor.

---

## 2. Protocolo de Encendido Rápido para el Día de Clases (3 Minutos)

Cuando estés en la sala de clases o reunión con la profesora:

### Paso 1: Validar el Servidor en AWS EC2 (1 minuto)
1. Entra a [AWS Console](https://console.aws.amazon.com/) -> **EC2** -> **Instancias**.
2. Verifica que `syncpass-api-server` esté en estado verde **En ejecución** (Running).
3. Si la habías detenido, dale a **Iniciar instancia**.
4. Abre una pestaña del navegador con `http://<IP_DE_TU_EC2>/`. Al responderte, la API está lista.

---

### Paso 2: Encender el Frontend Web (1 minuto)
1. Abre tu terminal PowerShell y escribe:
   ```powershell
   cd C:\dev\web\syncpass-web
   npm run dev
   ```
2. Abre en tu navegador: `http://localhost:5173/`.

---

### Paso 3: Encender la App Móvil en Android Studio (1 minuto)
1. Abre **Android Studio**.
2. Abre el proyecto `C:\dev\android\SyncPass-Android`.
3. Selecciona tu emulador (o conecta tu teléfono físico por USB con Depuración USB activa).
4. Haz clic en el botón verde **Play** (Run 'app').

---

## 3. Guion de Demostración para Obtener Nota 7.0 ante el Profesor

Sigue este orden exacto de presentación que aborda todos los puntos que pidió la profesora:

1. **Mostrar la Arquitectura y Código Limpio:**  
   Muestra la separación de capas en Android (`domain`, `core`, `ui`, `data`) y en React (`contexts`, `hooks`, `pages`), explicando que no hay lógica de negocio en controladores ni valores quemados (hardcoded).
2. **Demostrar AWS Cognito:**  
   En la pantalla de Login, presiona **AWS Cognito (SSO)** y entra con tu correo verificado `marianoacevedo1809@gmail.com`. Muestra cómo Cognito redirige con flujo seguro PKCE y te asigna el rol `coordinador`.
3. **Demostrar el Rol Docente / Coordinador:**  
   Entra a la vista de Docente. Muestra el quórum de los talleres en vivo y genera un código QR Canvas de acreditación.
4. **Demostrar el Rol Voluntario y Modo Territorial Offline:**  
   Entra a la vista de Voluntario. Simula la pérdida de internet activando **Modo Offline**. Registra asistencias manuales y muestra cómo se acumulan en la cola de sincronización diferida.
5. **Demostrar el Rol Beneficiario:**  
   Muestra el pase digital QR del vecino y la **Encuesta Inmediata de Satisfacción de 30 segundos** (estrellas 1 a 5 y comentarios).
6. **Demostrar la API Desplegada en AWS EC2:**  
   Muestra la IP pública de tu servidor EC2 en el navegador respondiendo con Nginx y Spring Boot bajo Systemd.
7. **Demostrar el Trabajo en Equipo en GitHub:**  
   Abre tu repositorio en GitHub y muestra:
   - Los commits de **Mariano Acevedo** y **Álvaro León**.
   - El Pull Request #1 cerrado y fusionado.
   - La carpeta `docs/` con toda la documentación técnica.
