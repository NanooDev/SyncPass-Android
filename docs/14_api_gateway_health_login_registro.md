# Integracion de API Gateway con Health, Login y Registro (SyncPass Android)

Este documento detalla el flujo de consumo de la API a traves de **AWS API Gateway** (punto de entrada seguro en HTTPS) hacia el backend Spring Boot (`fs2-api`) en **AWS EC2**, cumpliendo con la separacion de endpoints publicos y protegidos solicitada por el docente.

---

## 1. Arquitectura de Acceso Seguro

```text
[ Cliente Movil Android ]             [ Cliente Web ]
          │                                  │
          ▼ (Peticiones HTTPS Cifradas)      ▼
 ┌──────────────────────────────────────────────────┐
 │     AWS API Gateway (ID: 5xvspck6jb)            │
 │  https://5xvspck6jb.execute-api.us-east-1...     │
 └────────────────────────┬─────────────────────────┘
                          │ Integracion HTTP /{proxy}
                          ▼
          ┌───────────────────────────────┐
          │    AWS EC2 (Ubuntu 22.04)     │
          │    Spring Boot en Puerto 8080 │
          └───────────────┬───────────────┘
                          │
                          ▼
          ┌───────────────────────────────┐
          │  AWS Cognito / PostgreSQL DB  │
          └───────────────────────────────┘
```

---

## 2. Endpoints Consumidos por la Aplicacion Movil

- **Health Check:** `GET https://5xvspck6jb.execute-api.us-east-1.amazonaws.com/api/health`
- **Login:** `POST https://5xvspck6jb.execute-api.us-east-1.amazonaws.com/api/auth/login`
- **Registro:** `POST https://5xvspck6jb.execute-api.us-east-1.amazonaws.com/api/auth/registro`
- **Notificaciones Push:** `POST https://5xvspck6jb.execute-api.us-east-1.amazonaws.com/api/notificar`
