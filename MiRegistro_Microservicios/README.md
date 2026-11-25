# MiRegistro — Microservicios

Dos microservicios Spring Boot 4.1 (JDK 21, JPA, MySQL) que usará la app MiRegistro.

| Servicio | Puerto | Base de datos | Responsabilidad |
|---|---|---|---|
| `roles-service` | 8081 | `miregistro_roles` | Catálogo de roles |
| `usuarios-service` | 8082 | `miregistro_usuarios` | Usuarios, login y contraseña. Consulta a `roles-service` con **WebClient** |

Cada servicio tiene su propia base de datos (desacoplados): `usuarios` solo guarda el `rol_id`
y le pide el nombre del rol a `roles-service` por HTTP.

## Lombok

Las entidades usan `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor` y `@Builder`
(no `@Data`: su `equals`/`hashCode` da problemas con las entidades de JPA), y los servicios, controladores
y configuraciones usan `@RequiredArgsConstructor` para inyectar dependencias. Los DTOs son `record` de Java 21.

En IntelliJ IDEA el soporte de Lombok viene incluido. Si aparecen errores como `cannot find symbol getNombre()`,
revisar **Settings → Build, Execution, Deployment → Compiler → Annotation Processors → Enable annotation processing**.

## Cómo levantarlos

1. Iniciar MySQL (Laragon, XAMPP o MySQL Server) en el puerto 3306, usuario `root` sin contraseña.
   Si tu MySQL tiene otra clave, cámbiala en `src/main/resources/application.properties`.
   Las bases de datos se crean solas (`createDatabaseIfNotExist=true`).
2. Abrir y ejecutar **primero** `roles-service` (`RolesServiceApplication`).
3. Abrir y ejecutar `usuarios-service` (`UsuariosServiceApplication`).

Al iniciar por primera vez se cargan los mismos datos que tiene la app:

- Roles: 1 Administrador, 2 Docente, 3 Estudiante
- Usuarios: camila, diego, valentina, matias y fernanda `@duocuc.cl`, todos con contraseña `123456`

## Endpoints

### roles-service — `http://localhost:8081`

| Método | Ruta | Respuesta |
|---|---|---|
| GET | `/api/roles` | Lista de roles |
| GET | `/api/roles/{id}` | Un rol · 404 si no existe |

### usuarios-service — `http://localhost:8082`

| Método | Ruta | Cuerpo | Respuesta |
|---|---|---|---|
| GET | `/api/usuarios?buscar=texto` | — | Lista (el filtro es opcional) |
| GET | `/api/usuarios/{id}` | — | Usuario · 404 |
| POST | `/api/usuarios` | `{"nombre","correo","contrasena","ciudad"}` | 201 usuario creado · 400 · 409 correo repetido |
| POST | `/api/usuarios/login` | `{"correo","contrasena"}` | 200 usuario · 401 credenciales incorrectas |
| PUT | `/api/usuarios/{id}` | `{"nombre","correo","ciudad","rolId"}` | 200 usuario · 404 · 409 |
| PUT | `/api/usuarios/{id}/contrasena` | `{"actual","nueva"}` | 204 · 401 clave actual incorrecta |
| DELETE | `/api/usuarios/{id}` | — | 204 · 404 (borra también su foto) |
| POST | `/api/usuarios/{id}/avatar` | multipart/form-data, campo `archivo` (imagen) | 200 usuario con `avatarUrl` · 400 si no es imagen |
| GET | `/api/usuarios/{id}/avatar` | — | La imagen JPEG (la URL completa viene en `avatarUrl`) · 404 si no tiene foto |

Respuesta de un usuario (nunca incluye la contraseña):

```json
{
  "id": 1,
  "nombre": "Camila Rojas",
  "correo": "camila@duocuc.cl",
  "ciudad": "Santiago",
  "rolId": 1,
  "rolNombre": "Administrador",
  "avatarUrl": null
}
```

`avatarUrl` es `null` mientras el usuario no suba una foto. Al subirla queda, por ejemplo,
`"http://10.0.2.2:8082/api/usuarios/1/avatar?v=3f2a...e1.jpg"` (el host es el mismo con el que se hizo la petición).
El parámetro `v` cambia con cada foto nueva, así la app no muestra una versión antigua guardada en caché.

## Fotos de perfil

- Las imágenes se guardan como archivo en la carpeta `uploads/avatares` del proyecto `usuarios-service`
  (se crea sola). En MySQL solo queda el **nombre** del archivo (columna `avatar`).
- Cada foto nueva recibe un nombre único (UUID) y la anterior se borra, así la app nunca muestra una foto vieja en caché.
- Probar en Postman: `POST http://localhost:8082/api/usuarios/1/avatar` → Body → **form-data** →
  key `archivo` de tipo **File** → elegir una imagen.

Ejemplo para probar en Postman — crear cuenta (`POST http://localhost:8082/api/usuarios`):

```json
{
  "nombre": "Ana Torres",
  "correo": "ana@duocuc.cl",
  "contrasena": "secreto1",
  "ciudad": "Santiago"
}
```

Si `roles-service` está apagado, `usuarios-service` responde **503** ("El servicio de roles no está disponible").

## Desde la app Android

- Emulador: la URL base es `http://10.0.2.2:8082/` (`10.0.2.2` = el `localhost` del computador).
- Celular físico: usar la IP del computador en la misma red WiFi, por ejemplo `http://192.168.1.10:8082/`.
- Como es `http` (sin `s`), la app debe permitir tráfico sin cifrar (`usesCleartextTraffic`).
