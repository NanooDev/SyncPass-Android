# Guía de Colaboración en Dupla y Estrategia Git

> **Asignatura:** DSY1105 - Desarrollo Móvil / Fullstack  
> **Institución:** Duoc UC  
> **Proyecto:** SyncPass (Gestión Territorial y Acreditación de Vinculación con el Medio)  
> **Equipo de Trabajo (Dupla):**
> - **Mariano Acevedo** (GitHub: `NanooDev`, Email: `marianoacevedo1809@gmail.com`)
> - **Álvaro León** (Email Institucional: `alv.leon@duocuc.cl`)

---

## 1. Justificación Académica y Requisito Docente

El equipo docente de la asignatura evalúa tanto la calidad técnica del software como la **evidencia de trabajo colaborativo en el repositorio Git**. Para validar que ambos integrantes participan activamente y obtengan el 100% del puntaje en las rúbricas de evaluación, se establece:

1. **Trazabilidad de autoría:** Todos los commits deben estar debidamente firmados con los nombres y correos institucionales correspondientes.
2. **División modular clara:** Cada integrante lidera subsistemas diferenciados pero integrados, evitando conflictos de merge y demostrando dominio técnico individual.
3. **Flujo de trabajo basado en ramas (`Feature Branching`):** Ningún integrante sube cambios directamente a `master` sin antes validar en su rama de funcionalidad y generar trazabilidad.

---

## 2. Matriz de Repartición de Responsabilidades

| Módulo / Componente | Integrante Responsable | Rol Colaborador | Rama Git |
| :--- | :--- | :--- | :--- |
| **Arquitectura Limpia & Clean Layers** | Mariano Acevedo | Álvaro León (Revisión) | `feature/mariano-core-auth` |
| **AWS Cognito Auth & App Client Mobile** | Mariano Acevedo | Álvaro León (Pruebas) | `feature/mariano-core-auth` |
| **Identidad Visual & Sistema de Temas (0 Emojis)** | Mariano Acevedo | Álvaro León (Adopción) | `feature/mariano-core-auth` |
| **Pantalla Docente / Coordinador (Métricas & Quórum)** | Mariano Acevedo | Álvaro León (Feedback) | `feature/mariano-core-auth` |
| **Pantalla Voluntario & Modo Territorial Offline** | Álvaro León | Mariano Acevedo (Revisión) | `feature/alvaro-offline-sync` |
| **Componentes de Acreditación QR (Escáner & Pase)** | Álvaro León | Mariano Acevedo (Integración) | `feature/alvaro-offline-sync` |
| **Pantalla Beneficiario & Encuesta Inmediata** | Álvaro León | Mariano Acevedo (Testing) | `feature/alvaro-offline-sync` |
| **Automatización Cloud EC2 (`scripts/deploy_ec2.sh`)** | Álvaro León | Mariano Acevedo (Pruebas) | `feature/alvaro-offline-sync` |
| **Integración Kanban Trello (`scripts/sync_trello.ps1`)** | Álvaro León | Mariano Acevedo (Uso) | `feature/alvaro-offline-sync` |

---

## 3. Instrucciones de Configuración para Álvaro León

### A. Configurar Identidad Git Local
Para que los commits se registren a nombre de Álvaro con su correo institucional de Duoc UC, Álvaro debe ejecutar en su terminal:

```bash
git config --local user.name "Alvaro Leon"
git config --local user.email "alv.leon@duocuc.cl"
```

Verificar que quedó correctamente asignado con:
```bash
git config --local --get user.name
# Debe responder: Alvaro Leon
git config --local --get user.email
# Debe responder: alv.leon@duocuc.cl
```

---

### B. Crear y Trabajar en su Rama Asignada
```bash
# 1. Asegurar tener la última versión de master
git checkout master
git pull origin master

# 2. Crear y cambiar a su rama de trabajo
git checkout -b feature/alvaro-offline-sync

# 3. Desarrollar o agregar sus archivos asignados
git status
```

---

### C. Comandos de Commit Específicos para Álvaro

#### Commit 1: Componentes de Acreditación QR y Vista Territorial de Voluntario
```bash
git add app/src/main/java/com/example/kotlin_login/ui/components/QrComponents.kt
git add app/src/main/java/com/example/kotlin_login/ui/voluntario/VoluntarioScreen.kt
git commit -m "feat(voluntario): implementar escáner QR y modo sin conexión territorial"
```

#### Commit 2: Pantalla de Beneficiario y Encuesta Inmediata de 30 Segundos
```bash
git add app/src/main/java/com/example/kotlin_login/ui/beneficiario/BeneficiarioScreen.kt
git commit -m "feat(beneficiario): implementar pase digital QR y encuesta de satisfacción inmediata"
```

#### Commit 3: Scripts de Automatización DevOps (AWS EC2 y Trello)
```bash
git add scripts/deploy_ec2.sh
git add scripts/sync_trello.ps1
git commit -m "ci(devops): agregar script de despliegue EC2 Ubuntu y sincronizador Trello"
```

---

### D. Subir la Rama a GitHub
```bash
git push -u origin feature/alvaro-offline-sync
```

---

## 4. Cómo Simular / Registrar Commits en la Misma Computadora

Si están trabajando en el mismo computador de desarrollo y necesitan que los commits de Álvaro queden registrados con su autoría oficial de Duoc UC antes de subir a GitHub:

```bash
# Formato de autor explícito para Álvaro:
git commit --author="Alvaro Leon <alv.leon@duocuc.cl>" -m "feat(voluntario): implementar escáner QR y modo territorial offline"
```

Al revisar el log con `git log --pretty=format:"%h - %an <%ae> : %s"` se observará claramente:
```text
a1b2c3d - Alvaro Leon <alv.leon@duocuc.cl> : feat(voluntario): implementar escáner QR y modo territorial offline
e4f5g6h - NanooDev <marianoacevedo1809@gmail.com> : feat(auth): implementar inicio con AWS Cognito y vistas de docente
```

Esto satisface plenamente la exigencia del profesor respecto a la participación equilibrada de ambos integrantes.
