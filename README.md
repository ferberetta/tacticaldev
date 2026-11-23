# TacticalDEV – App de visitas técnicas
Aplicación móvil Android para gestionar, registrar y verificar visitas técnicas a equipos de refrigeración en diferentes puntos de venta. Reemplaza el uso de papel o planillas con un sistema digital y auditable.

## Integrantes
- Benjamín Tapia (01 Registrar visita)
- Diego Muñoz Hormazabal (02 Equipos y activos)
- Fernanda Beretta (03 Historial de visitas)
- Eduardo Martínez (04 Evidencia fotográfica)
- Jonathan Barros (05 Ubicación en tiempo real)

## Funcionalidades
- **Login y registro:** Autenticación local segura para iniciar sesión o registrar nuevas cuentas.
- **Inicio con resumen:** Pantalla principal con accesos rápidos y un contador en vivo de visitas pendientes y equipos con fallas.
- **CRUD de equipos:** Listado, creación, edición (prellenando datos) y eliminación de equipos.
- **CRUD de visitas:** Formulario completo para registrar una visita (crear), visualización en detalle (ver), opción rápida para marcarla como sincronizada (marcar como completada) y eliminación de registros (eliminar).
- **Foto con cámara:** Uso de la cámara nativa para adjuntar y visualizar evidencia fotográfica en el detalle de la visita.
- **Ubicación GPS:** Obtención de coordenadas en el registro y un botón directo para visualizar el lugar de la visita en Google Maps.
- **Temperatura del lugar:** Consumo de API externa para mostrar el clima actual del lugar basándose en las coordenadas registradas.
- **Historial con filtros:** Listado completo y dinámico de visitas con pestañas para "Todas", "Completadas" y "Pendientes".

## Endpoints

### Microservicio ms-equipos (Puerto 8081)
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/equipos` | Obtiene la lista de todos los equipos. |
| POST | `/api/equipos` | Crea un equipo nuevo. |
| PUT | `/api/equipos/{id}` | Actualiza un equipo existente por su ID. |
| DELETE | `/api/equipos/{id}` | Elimina un equipo específico. |

### Microservicio ms-visitas (Puerto 8082)
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/visitas` | Obtiene el historial de visitas. |
| POST | `/api/visitas` | Registra una nueva visita. |
| PUT | `/api/visitas/{id}` | Modifica una visita (ej. cambiar su estado a sincronizada). |
| DELETE | `/api/visitas/{id}` | Elimina un registro de visita. |

### API Externa (Open-Meteo)
| Método | Ruta | Descripción |
|---|---|---|
| GET | `https://api.open-meteo.com/v1/forecast?latitude={lat}&longitude={lon}&current_weather=true` | Devuelve la temperatura actual según latitud y longitud. |

## Pasos para ejecutar

**Requisitos:**
- JDK 17 o superior.
- Android Studio.
- Emulador Android configurado y corriendo.

**Instrucciones:**
1. Levantar localmente los microservicios `ms-equipos` y `ms-visitas`. Para ello, abre una terminal en cada carpeta respectiva y ejecuta `./mvnw.cmd spring-boot:run`.
2. Abrir la app (carpeta principal) en Android Studio y esperar a que Gradle termine de sincronizar.
3. Ejecutar la aplicación en el emulador pulsando Run. **Importante:** La app usa internamente la IP `10.0.2.2` para alcanzar el localhost de la computadora desde el emulador; si usarás un celular real conectado por cable/Wi-Fi, asegúrate de cambiar la IP en `ApiClient.kt` por la IPv4 real de tu PC.
4. Para ejecutar las pruebas unitarias y verificar la cobertura, lanza el comando `./gradlew.bat testDebugUnitTest` desde la terminal de Android Studio.
5. El APK de la aplicación lo puedes ubicar en la carpeta `entrega/`.

## APK firmado y .jks

Los archivos finales correspondientes a la entrega de la aplicación se encuentran en la raíz del proyecto dentro del directorio `entrega/`:
- **APK Firmado:** `entrega/tacticaldev-release.apk`
- **Keystore:** `entrega/tacticaldev.jks`

![APK Firmado](docs/capturas/apk-firmado.png)
![Keystore JKS](docs/capturas/jks.png)
![Cobertura de pruebas](docs/capturas/cobertura.png)

## Gestión del proyecto

- **Tablero Kanban:** GitHub Projects, "TacticalDEV Visitas - Tablero": [PEGAR AQUÍ LA URL DEL TABLERO]
- **Issues:** cada tarea está registrada como issue y asignada a su responsable.
- **GitHub:** ramas separadas por integrante (formato `feature/...`) e integración a `main` mediante Pull Requests.

## Limitaciones conocidas
- La foto tomada solo guarda la ruta local del archivo dentro del dispositivo, no la imagen real en un servidor externo.
- La temperatura mostrada en el detalle siempre es la actual (de este preciso momento), no guarda un histórico temporal del clima de cuando se registró.
- El login funciona de manera 100% local, guardando las sesiones y los hash en `SharedPreferences`.
- Se permite tráfico HTTP sin cifrar, pero esto es única y exclusivamente para propósitos de comunicación de desarrollo local con los microservicios.
