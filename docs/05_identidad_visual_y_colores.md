# Identidad Visual, Paleta Cromática y Propuesta de Valor — SyncPass

> **Institución:** Duoc UC • Escuela de Informática y Telecomunicaciones  
> **Asignatura:** DSY1105 · Desarrollo de Aplicaciones Móviles  
> **Proyecto:** SyncPass (Plataforma Territorial de Acreditación y Evaluación)  
> **Autor:** Mariano Acevedo ([@NanooDev](https://github.com/NanooDev))

---

## 1. Propuesta de Valor Única

SyncPass transforma la ejecución en terreno de los proyectos de Vinculación con el Medio de Duoc UC, sustituyendo las listas en papel por un ecosistema digital integrado:

- **Acreditación Rápida con QR:** Reduce el tiempo de ingreso por participante de 45 segundos a menos de 5 segundos.
- **Operación Continua Offline-First:** Garantiza la continuidad operativa en sedes comunitarias y escuelas periféricas sin cobertura de internet, sincronizando automáticamente con AWS al restablecer conexión.
- **Medición de Impacto Inmediata:** Levanta encuestas de valoración comunitaria de 30 segundos en el mismo momento del taller, aumentando la tasa de respuesta efectiva de un 15% histórico a más de un 70%.
- **Gobernanza y Transparencia RBAC:** Control de acceso estricto basado en roles para Docentes, Voluntarios, Beneficiarios y Administradores.

---

## 2. Directrices de Diseño Visual

- **Estilo:** Corporativo, sobrio, moderno y acogedor ("monono"), optimizado para su lectura en dispositivos móviles en terreno.
- **Política de Emojis:** **Cero emojis** en toda la interfaz de usuario. En su lugar, se utilizan exclusivamente iconos vectoriales del sistema Material Design (`material-icons-extended`) y etiquetas tipográficas jerárquicas.
- **Superficies y Fondos:** Predominio de fondos claros (`#F8FAFC`) que reducen la fatiga visual en exteriores y optimizan el contraste bajo luz solar.

---

## 3. Paleta Cromática Oficial

| Variable / Token | Código HEX | Rol Semántico en la Aplicación |
|---|---|---|
| `SyncPassBluePrimary` | `#1E3A8A` | Azul marino institucional Duoc UC. Encabezados, títulos de sección y botones principales. |
| `SyncPassBlueSecondary` | `#2563EB` | Azul activo para llamadas a la acción secundarias, enlaces y foco interactivo. |
| `SyncPassBlueLight` | `#DBEAFE` | Azul pastel suave para fondos de badges informativos y chips de perfil. |
| `SyncPassEmerald` | `#059669` | Verde esmeralda para asistencias acreditadas, estado conectado y quórum alcanzado. |
| `SyncPassEmeraldLight` | `#D1FAE5` | Fondo pastel para estados de éxito y confirmación de asistencia. |
| `SyncPassAmber` | `#D97706` | Ámbar cálido para alertas de modo Offline y sincronizaciones pendientes. |
| `SyncPassAmberLight` | `#FEF3C7` | Fondo pastel para advertencias de desconexión territorial. |
| `SyncPassBackground` | `#F8FAFC` | Fondo general claro de la aplicación (Slate 50). |
| `SyncPassSurface` | `#FFFFFF` | Superficie blanca pura para tarjetas (`AuthCard`) y paneles modulares. |
| `SyncPassBorder` | `#E2E8F0` | Líneas divisorias sutiles de 1dp que delimitan áreas sin saturar visualmente. |
| `SyncPassTextPrimary` | `#0F172A` | Texto primario de alto contraste y legibilidad óptima (Slate 900). |
| `SyncPassTextSecondary` | `#64748B` | Texto secundario para subtítulos, metadatos y sellos de tiempo (Slate 500). |

---

## 4. Tipografía y Jerarquía

- **Títulos de Pantalla:** `headlineMedium` (24sp, SemiBold, color `SyncPassTextPrimary`).
- **Subtítulos Institucionales:** `titleMedium` / `bodyMedium` (14sp a 16sp, color `SyncPassTextSecondary`).
- **Badges y Etiquetas de Estado:** `labelSmall` (11sp, Medium, con padding horizontal de 8dp y esquinas redondeadas de 6dp).
- **Lecturas de Datos / RUT:** `titleSmall` (14sp, SemiBold, alto contraste).
