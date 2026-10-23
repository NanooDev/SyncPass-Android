#!/bin/bash
# ==============================================================================
# Script de Despliegue Automatizado - SyncPass Java API en AWS EC2 (Ubuntu 22.04)
# Autor: Alvaro Leon (alv.leon@duocuc.cl)
# Proyecto: SyncPass Duoc UC - DSY1105
# ==============================================================================

set -e

echo "=== INICIANDO DESPLIEGUE AUTOMATIZADO DE SYNCPASS API ==="

# 1. Actualizar paquetes del sistema
echo "[1/6] Actualizando paquetes del sistema..."
sudo apt update -y && sudo apt upgrade -y

# 2. Instalar dependencias necesarias
echo "[2/6] Instalando OpenJDK 21, Git y Nginx..."
sudo apt install -y openjdk-21-jdk git curl nginx ufw

# 3. Configurar directorio del proyecto
APP_DIR="/opt/syncpass-api"
echo "[3/6] Preparando directorio en $APP_DIR..."
sudo mkdir -p $APP_DIR
sudo chown -R $USER:$USER $APP_DIR

if [ ! -d "$APP_DIR/.git" ]; then
    echo "Clonando repositorio de fs2-api..."
    git clone https://github.com/docentedev/fs2-api.git $APP_DIR
else
    echo "Repositorio existente detectado. Actualizando con git pull..."
    cd $APP_DIR && git pull origin main || git pull origin master
fi

cd $APP_DIR

# 4. Compilar archivo ejecutable JAR con Gradle Wrapper
echo "[4/6] Compilando Spring Boot JAR con Gradle Wrapper..."
chmod +x gradlew
./gradlew bootJar --no-daemon

JAR_FILE=$(find build/libs -name "*.jar" ! -name "*plain*" | head -n 1)
echo "JAR generado exitosamente: $JAR_FILE"

# 5. Configurar servicio systemd para ejecución continua
echo "[5/6] Creando servicio systemd syncpass-api.service..."
sudo tee /etc/systemd/system/syncpass-api.service > /dev/null <<EOF
[Unit]
Description=SyncPass Spring Boot API
After=network.target

[Service]
User=$USER
WorkingDirectory=$APP_DIR
ExecStart=/usr/bin/java -Xms256m -Xmx512m -jar $APP_DIR/$JAR_FILE
SuccessExitStatus=143
Restart=always
RestartSec=10
StandardOutput=syslog
StandardError=syslog
SyslogIdentifier=syncpass-api

[Install]
WantedBy=multi-user.target
EOF

sudo systemctl daemon-reload
sudo systemctl enable syncpass-api
sudo systemctl restart syncpass-api

# 6. Configurar Nginx Reverse Proxy
echo "[6/6] Configurando Nginx Reverse Proxy (Puerto 80 -> 8080)..."
sudo tee /etc/nginx/sites-available/syncpass-api > /dev/null <<EOF
server {
    listen 80;
    server_name _;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
        proxy_connect_timeout 60s;
        proxy_read_timeout 60s;
    }
}
EOF

sudo ln -sf /etc/nginx/sites-available/syncpass-api /etc/nginx/sites-enabled/
sudo rm -f /etc/nginx/sites-enabled/default
sudo nginx -t
sudo systemctl restart nginx

echo "=== DESPLIEGUE FINALIZADO EXITOSAMENTE ==="
echo "Estado del servicio:"
sudo systemctl status syncpass-api --no-pager
