# EcoRecicla Backend

Backend REST de EcoRecicla, desarrollado en Java 21 y Spring Boot para Randy Manuel Huaman Gonzales. Esta entrega contiene únicamente el servicio y su base PostgreSQL; no contiene cliente Android/KMP.

## Inicio rápido

Requisitos: Docker Desktop con Compose v2, puerto 8080 libre y conexión a Internet en el primer build. Desde la raíz del repositorio:

```powershell
Copy-Item .env.example .env
docker compose up --build
```

Compose inicia PostgreSQL 16, espera a que el healthcheck esté saludable y después inicia el servicio. API: `http://localhost:8080/api/v1/`. Para detener: `docker compose down`; para conservar la base se mantiene el volumen `ecorecicla-postgres`. Para borrar también sus datos de demostración: `docker compose down -v`.

En un emulador Android, configurar la futura aplicación con `http://10.0.2.2:8080/api/v1/`. Desde un dispositivo físico se debe usar la IP LAN del equipo que ejecuta Docker.

El archivo `.env.example` contiene credenciales y una clave únicamente de desarrollo. Se puede iniciar sin crear `.env` porque Compose ofrece los mismos valores de demostración; en cualquier entorno compartido se deben reemplazar por secretos propios y no versionar `.env`.

## Ejecución local sin Docker

Se requiere Java 21 y PostgreSQL accesible. Copiar `.env.example` a `.env` y exportar `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET` y `TZ` en la consola (Maven no carga `.env` automáticamente). Migraciones Flyway se ejecutan al iniciar la aplicación.

```powershell
.\mvnw.cmd clean verify
```

Para arrancar localmente: `./mvnw.cmd spring-boot:run`. Las pruebas automatizadas usan H2 en memoria y no requieren Docker ni una instancia PostgreSQL.

## Contrato REST

Prefijo: `/api/v1`. La única operación pública es `POST /auth/login`; cualquier otra petición requiere `Authorization: Bearer <token>`. Todos los listados aceptan `pagina` (base cero) y `tamanio` (1–100). El cliente no puede ordenar por campos internos: los puntos se ordenan por razón social y los pesajes del punto por fecha/hora descendente.

| Método | Ruta | Privilegio | Respuesta |
|---|---|---|---|
| POST | `/auth/login` | Pública | 200; credenciales incorrectas 401 |
| GET | `/usuarios/actual` | Token válido | 200 identidad, rol y privilegios |
| GET | `/rutas/asignada?fecha=YYYY-MM-DD` | `PUNTO_LEER` | 200 ruta del usuario autenticado con puntos en secuencia |
| GET | `/puntos?pagina=0&tamanio=20&zona=Centro` | `PUNTO_LEER` | 200 `Pagina<Punto>` |
| GET | `/puntos/{puntoId}/pesajes?pagina=0&tamanio=20` | `PESAJE_LEER` | 200 `Pagina<Pesaje>` más reciente primero |
| POST | `/rutas/{rutaId}/puntos/{puntoId}/pesajes` | `PESAJE_CREAR` | 201 pesaje definitivo; el servidor calcula el neto |
| PUT | `/pesajes/{id}` | `PESAJE_EDITAR` | 200 pesaje corregido |
| POST | `/pesajes/{id}/anulacion` | `PESAJE_ANULAR` | 200 pesaje conservado con estado `ANULADO` |
| POST | `/rutas/{rutaId}/puntos/{puntoId}/omision` | `RUTA_CERRAR` | 200 registra motivo de omisión |
| POST | `/rutas/{id}/cierre` | `RUTA_CERRAR` | 200 ruta cerrada; si faltan puntos, 409 |
| POST | `/puntos` | `PUNTO_GESTIONAR` | 201 crea punto |
| POST | `/rutas` | `PUNTO_GESTIONAR` | 201 crea ruta y asigna puntos en el orden recibido |

Ejemplos de cuerpos:

```json
{"usuario":"operario1","contrasena":"Eco2026*"}
```

```json
{"pesoBruto":31.500,"tara":2.250,"material":"PAPEL","observacion":"Contenedor azul"}
```

El cuerpo de pesaje no acepta un peso neto autoritativo. La API deriva `pesoNeto = pesoBruto - tara` y responde 400 asociado a `pesoBruto` si el resultado no es mayor que cero. Omisión: `{"motivo":"Acceso no disponible"}`. Anulación: `{"motivo":"Balanza fuera de calibración"}`. Asignación de ruta: `{"fecha":"2026-10-28","operarioId":1,"puntosIds":[1,2,3]}`.

Cada lista devuelve `contenido`, `pagina`, `tamanio`, `totalElementos`, `totalPaginas` y `ultima`. Todos los errores usan `timestamp`, `status`, `error`, `message`, `path` y `validationErrors`; validaciones de formulario identifican sus campos. Códigos usados: 200, 201, 400, 401, 403, 404, 409 y 500.

## Seguridad y reglas

Las contraseñas se codifican con BCrypt. JWT se firma con HMAC, expira en 60 minutos y lleva `uid`, `sub` (usuario), `privilegios`, `iat` y `exp`. No hay renovación. El filtro valida estructura, firma y vigencia en todas las rutas protegidas. Spring Security verifica autoridades por privilegio. `PESAJE_EDITAR_TODOS` es un privilegio auxiliar para distinguir al supervisor; los privilegios mínimos del examen se conservan.

| Rol | Privilegios |
|---|---|
| OPERARIO | `PUNTO_LEER`, `PESAJE_LEER`, `PESAJE_CREAR`, `PESAJE_EDITAR` (solo pesajes propios del día) |
| SUPERVISOR | lectura, registro, corrección (`PESAJE_EDITAR_TODOS`), anulación y cierre |
| ADMINISTRADOR | `PUNTO_LEER`, `PESAJE_LEER`, `PUNTO_GESTIONAR`; no registra pesajes |

- RN-01: registro y corrección en ruta cerrada devuelven 409.
- RN-02: el operario solo corrige pesajes propios del día; otros autores o fechas devuelven 403.
- RN-03: el neto se calcula en el servicio y debe ser positivo; si no, 400.
- RN-04: el cierre comprueba cada punto planificado; requiere un pesaje no anulado o un motivo de omisión y responde 409 con el número faltante.
- La anulación es lógica y conserva autoría, valores, motivo y marcas de auditoría.

## Semilla y decisiones documentadas

Al arrancar se insertan de forma idempotente los roles, privilegios y usuarios demo si faltan. No se vacían tablas al reiniciar. La semilla crea 12 puntos en tres zonas, ruta abierta de hoy con seis puntos y varios pendientes, ruta cerrada de ayer, 16 pesajes y una cuenta `operario2` para demostrar la regla de propiedad. Los cuatro usuarios demo son `operario1`, `operario2`, `supervisor1` y `admin`, contraseña `Eco2026*`; se guarda solo el hash BCrypt. Cambiar la contraseña de un usuario existente no se revierte en arranques posteriores.

Supuestos elegidos donde el examen admite más de una firma de API:

1. La fecha de negocio y el “día actual” se calculan en `America/Lima`; Compose configura `TZ` con ese valor.
2. Se crea una ruta por operario y fecha. La consulta de ruta diaria devuelve la asignada al principal autenticado.
3. La supervisión puede registrar omisiones para cumplir RN-04. Una ruta planifica cada punto una sola vez y en una secuencia única.
4. Un punto se considera atendido si tiene al menos un pesaje en estado `REGISTRADO`; un pesaje anulado no satisface RN-04.
5. El administrador crea rutas y asignaciones de puntos en una sola solicitud; no hay API de administración de usuarios porque no se requiere para demostrar el alcance del examen.

La semilla se ejecuta con `CommandLineRunner` transaccional e idempotente, tras las migraciones versionadas de Flyway. El esquema SQL crea las tablas, claves foráneas, índices, restricciones y marcas de auditoría.

## Organización

`config/` seguridad y semilla; `seguridad/` filtro y JWT; `dominio/` entidades JPA; `repositorio/` Spring Data; `servicio/` reglas y transacciones; `web/` controladores REST delgados y DTO; `exception/` respuesta de error común. Persistencia: PostgreSQL. Versiones y migraciones: `src/main/resources/db/migration`.

## Verificación realizada

- `mvn -B -DskipTests package`: compilación Java 21 correcta.
- `mvnw.cmd -q clean verify`: 12 pruebas de integración contra H2, todas aprobadas (semilla mínima, login correcto/incorrecto, identidad y privilegios, token ausente/alterado/vencido, 403, paginación/filtro, cálculo de neto, 400 por neto cero, RN-02, RN-01, anulación lógica, cierre RN-04 incompleto/completo).
- Docker Engine no estaba disponible durante esta sesión; no se pudo levantar PostgreSQL/Compose ni validar el arranque de la imagen. Randy puede ejecutar `docker compose up --build` cuando Docker Desktop esté activo.

