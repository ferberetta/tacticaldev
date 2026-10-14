# TacticalDEV

Aplicación móvil Android para registrar y verificar visitas técnicas a vitrinas freezer y conservadoras en puntos de venta, reemplazando el uso informal de WhatsApp, planillas Excel y papel.

**Asignatura:** Desarrollo de Aplicaciones Móviles
**Evaluación:** Parcial 2 (avance del proyecto)
**Equipo N°:** 1
**Sección:** 002v

## Contexto del problema

TacticalDEV desarrolla soluciones de trazabilidad para distribuidoras de productos refrigerados. Hoy las visitas se reportan de forma informal, lo que permite "visitas fantasma" (visitas que no ocurrieron) y que un 20% de los equipos tenga fallas no detectadas a tiempo. La app busca un registro digital verificable, con historial único y consultable.

## Integrantes y módulos

| Integrante             | Módulo                      | Rol                 |
|---|---|---|
| Benjamín Tapia         | 01 Registrar visita         | Frontend / UI       |
| Diego Muñoz Hormazabal | 02 Equipos y activos        | Backend / Datos     |
| Fernanda Beretta       | 03 Historial de visitas     | UX / Integración    |
| Eduardo Martínez       | 04 Evidencia fotográfica    | Multimedia / Nativo |
| Jonathan Barros        | 05 Ubicación en tiempo real | Geolocalización     |

## Funcionalidades implementadas

- **Equipos y activos (Diego):** listado de equipos con búsqueda y filtros por estado, guardado en base de datos local.
- **Historial de visitas (Fernanda):** lista de visitas con fecha, equipo y estado de sincronización; pestañas Todas / Completadas / Pendientes; pantalla de detalle con técnico, observaciones, foto y ubicación; animaciones de entrada, reordenamiento y cambio de color del estado.
- **Persistencia local:** Room (SQLite) con tablas de equipos y visitas, y datos ficticios de ejemplo.
- **Navegación:** Navigation Compose entre historial, detalle y equipos.
- **Registrar visita (Benjamín):** [COMPLETAR según lo que se integre]
- **Cámara y evidencia fotográfica (Eduardo):** captura de fotografías en alta resolución para respaldo de visitas mediante `ActivityResultContracts.TakePicture()`; gestión de permisos en tiempo de ejecución y configuración de `FileProvider` para almacenamiento seguro; componente `CameraSection` en Jetpack Compose con estado inicial, previsualización de la imagen capturada, opción de eliminación/recaptura y exposición de la `photoUri` para el guardado.
- **Ubicación GPS (Jonathan):** [COMPLETAR según lo que se integre]

## Tecnologías

Kotlin, Jetpack Compose, Material Design 3, arquitectura MVVM, Room, Navigation Compose, StateFlow/Coroutines, Git/GitHub y GitHub Projects (tablero Kanban).

## Estructura del proyecto

cl.tacticaldev.visitas
├── model (entidades y DAOs de Room, AppDatabase)
├── repository (acceso a datos)
├── viewmodel (lógica y estado)
├── ui (pantallas Compose: equipos, historial, detalle, theme)
├── AppNavigation.kt
└── MainActivity.kt


## Cómo ejecutar

1. Clonar el repositorio: `git clone https://github.com/ferberetta/tacticaldev.git`
2. Abrir la carpeta en Android Studio y esperar el sync de Gradle.
3. Conectar un celular Android (API 26 o superior) con depuración USB, o usar un emulador.
4. Pulsar **Run** (▶).

## Datos

Todos los datos son ficticios; no se usan datos reales ni credenciales.

## Gestión del proyecto

Tareas organizadas como issues en GitHub y tablero Kanban (GitHub Projects). Trabajo en ramas `feature/...` integradas a `main` mediante Pull Requests.

---



