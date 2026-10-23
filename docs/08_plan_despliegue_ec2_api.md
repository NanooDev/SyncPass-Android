# Plan de Despliegue de la API Java (Spring Boot) en AWS EC2

> **Proyecto:** SyncPass Backend API (`fs2-api` / `LC02-API`)  
> **Servidor:** Amazon Elastic Compute Cloud (AWS EC2)  
> **Tecnología:** Java 21 / Spring Boot + Gradle + Systemd  
> **Autor:** Mariano Acevedo ([@NanooDev](https://github.com/NanooDev))

---

## 1. Arquitectura de Despliegue

```text
  [ Cliente Móvil ]          [ Navegador Web ]
          │                         │
          ▼                         ▼
   AWS EC2 Security Group (Puertos 80 HTTP, 22 SSH)
          │
          ▼
   Nginx Reverse Proxy (Puerto 80)
          │
          ▼ (Redirección interna localhost:8080)
   Spring Boot Java API (systemd daemon: syncpass-api.service)
          │
          ▼
   AWS Cognito (Validación de JWT via JWKS) / Base de Datos
```

---

## 2. Plan de Ejecución Paso a Paso

### Fase 1: Aprovisionamiento de la Instancia EC2 en AWS Console
1. **Lanzar Instancia:**
   - **Nombre:** `syncpass-api-server`
   - **AMI (Sistema Operativo):** Ubuntu Server 22.04 LTS (HVM), SSD Volume Type (Capa gratuita / Free tier).
   - **Tipo de Instancia:** `t2.micro` o `t3.micro` (1 vCPU, 1 GiB RAM).
   - **Par de Claves (Key Pair):** Crear o seleccionar par de llaves RSA (`.pem`) llamado `syncpass-key.pem` y guardarlo en una carpeta segura.
2. **Grupo de Seguridad (Security Group):**
   - Regla 1: SSH (Puerto 22) desde `Mi IP` (o `0.0.0.0/0` para acceso remoto).
   - Regla 2: HTTP (Puerto 80) desde `0.0.0.0/0` (Cualquier lugar).
   - Regla 3: Custom TCP (Puerto 8080) desde `0.0.0.0/0` (Para pruebas directas de la API).
3. **Almacenamiento:** 8 GiB a 15 GiB gp3 (Estándar).
4. Hacer clic en **Lanzar instancia**. Anotar la **Dirección IP pública IPv4** asignada.

---

### Fase 2: Configuración Inicial del Servidor (Vía SSH)
En tu terminal local de PowerShell o consola:
```bash
# Permisos de la llave (si usas Linux/WSL o terminal bash)
chmod 400 syncpass-key.pem

# Conexión al servidor EC2
ssh -i "syncpass-key.pem" ubuntu@54.204.137.182
```

Una vez dentro del servidor Ubuntu:
```bash
# 1. Actualizar repositorios del sistema
sudo apt update && sudo apt upgrade -y

# 2. Instalar OpenJDK 21 y herramientas auxiliares
sudo apt install -y openjdk-21-jdk git curl nginx

# 3. Validar versión de Java
java -version
```

---

### Fase 3: Descarga y Compilación de la API
```bash
# 1. Crear directorio para la aplicación
sudo mkdir -p /opt/syncpass-api
sudo chown -R ubuntu:ubuntu /opt/syncpass-api
cd /opt/syncpass-api

# 2. Clonar el repositorio de la API del profesor
git clone https://github.com/docentedev/fs2-api.git .

# 3. Dar permisos de ejecución a Gradle Wrapper
chmod +x gradlew

# 4. Compilar el archivo ejecutable JAR
./gradlew bootJar --no-daemon
```
El archivo ejecutable compilado quedará generado en `build/libs/fs2-api-0.0.1-SNAPSHOT.jar` (o nombre similar según la versión).

---

### Fase 4: Configuración de Systemd (Servicio Automático 24/7)
Para garantizar que la API se ejecute en segundo plano y se reinicie automáticamente si el servidor se reinicia o falla:

1. Crear el archivo de servicio:
```bash
sudo nano /etc/systemd/system/syncpass-api.service
```

2. Pegar la siguiente configuración:
```ini
[Unit]
Description=SyncPass Backend API Service (Spring Boot)
After=network.target

[Service]
User=ubuntu
WorkingDirectory=/opt/syncpass-api
ExecStart=/usr/bin/java -Xmx512m -jar /opt/syncpass-api/build/libs/fs2-api-0.0.1-SNAPSHOT.jar
SuccessExitStatus=143
Restart=always
RestartSec=10
Environment="PORT=8080"
Environment="COGNITO_USER_POOL_ID=us-east-1_RlB8Ft9Z2"
Environment="COGNITO_CLIENT_ID=2988naivr3qfmjhhm35k5lubvd"

[Install]
WantedBy=multi-user.target
```

3. Habilitar e iniciar el servicio:
```bash
sudo systemctl daemon-reload
sudo systemctl enable syncpass-api.service
sudo systemctl start syncpass-api.service

# Verificar estado
sudo systemctl status syncpass-api.service
```

---

### Fase 5: Configuración de Nginx como Reverse Proxy (Opcional recomendado)
Permite que las peticiones que lleguen al puerto 80 estándar sean redirigidas internamente al puerto 8080 de Java:

1. Editar la configuración del sitio por defecto:
```bash
sudo nano /etc/nginx/sites-available/default
```

2. Configurar la regla `location /`:
```nginx
server {
    listen 80;
    server_name _;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

3. Reiniciar Nginx:
```bash
sudo nginx -t
sudo systemctl restart nginx
```

---

### Fase 6: Validación y Pruebas de Conectividad
Desde tu computadora local, ejecuta en el navegador o en PowerShell:
```powershell
# Probar endpoint de salud
Invoke-RestMethod -Uri "http://54.204.137.182/api/users"
```
Si devuelve el listado JSON o el código de respuesta esperado, el despliegue está 100% operativo.
