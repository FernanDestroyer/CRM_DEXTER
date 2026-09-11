# Registro de cambios: autenticación CRM Dexter

Este archivo registra los cambios realizados durante la implementación del flujo de autenticación.

## Información de ejecución

- Modelo de IA: `openai/gpt-5.6-luna`
- Fecha y hora de inicio registrada: `2026-09-11 01:47:58 -05:00`
- Fecha y hora del registro final: `2026-09-11 01:48:40 -05:00`
- Zona horaria: `UTC-05:00`

## Cambios realizados

### 1. Autenticación backend

- Se reemplazó el query defectuoso `isAdmin` por una consulta JPA que busca administradores activos por email.
- Se normalizan emails eliminando espacios y convirtiéndolos a minúsculas.
- Se agregaron estados de respuesta: `AUTHENTICATED`, `PENDING`, `OTP_REQUIRED` y `REJECTED`.
- Se agregó `POST /api/auth/verify-otp`.
- Se agregó `GET /api/auth/me`.
- Se agregó `POST /api/auth/logout`.
- Se agregó soporte para JWT desde la cookie `crm_dexter_token` en `JwtAuthenticationFilter`.
- El JWT se envía desde el backend en una cookie `HttpOnly`, `SameSite=Lax` y con duración de 24 horas.
- Se dejó de depender del token guardado en `localStorage`.
- Se agregó validación de email y de OTP de exactamente cuatro dígitos.

### 2. Solicitudes de acceso y OTP

- Se agregó `SolicitudAccesoController` bajo `/api/admin/solicitudes`.
- Se agregó listado de solicitudes pendientes.
- Se agregó aprobación de solicitudes.
- Se agregó rechazo de solicitudes.
- La aprobación exige un administrador activo.
- El administrador puede asignar los roles `PROPIETARIO`, `ANALISTA` u `OPERATIVO`.
- Se agregó `OtpService`.
- El OTP se genera aleatoriamente, se almacena únicamente como hash BCrypt y expira según `app.otp.expiration-seconds`.
- Se estableció un máximo de cinco intentos.
- Se agregó envío por `JavaMailSender`, compatible con MailHog/Mailpit local.
- Al aprobar se crea o actualiza el usuario asociado con la organización del administrador.

### 3. Seguridad y configuración backend

- Se agregaron getters y setters necesarios a `Usuario` y `SolicitudAcceso`.
- El constructor de `Usuario` quedó disponible para la creación durante la aprobación.
- Se agregaron respuestas uniformes para `IllegalArgumentException` y `ResponseStatusException`.
- La contraseña de la base de datos y las credenciales de correo dejaron de tener valores reales hardcodeados.
- El secreto JWT ahora puede configurarse mediante `JWT_SECRET`.

### 4. Autenticación frontend

- Axios ahora usa `withCredentials: true` para enviar y recibir cookies.
- Se eliminó la inyección del JWT desde `localStorage`.
- Se agregó `AuthContext` para consultar y mantener el usuario autenticado.
- Se agregó `ProtectedRoute` para impedir acceso anónimo a `/app/*`.
- Se agregó interceptor global para redirigir al login ante un `401`.
- `LoginPage` ahora soporta los estados email, solicitud pendiente y verificación OTP.
- Se agregó campo para introducir el OTP de cuatro dígitos.
- El logout del `Header` llama al backend y elimina la sesión.

### 5. Panel administrativo frontend

- `TeamPage` dejó de ser un placeholder.
- Se muestran solicitudes pendientes.
- Se puede aprobar o rechazar cada solicitud.
- Antes de aprobar se puede seleccionar el rol inicial del usuario.

## Archivos principales modificados

### Backend

- `backend/src/main/java/com/crmdexter/backend/controller/AuthController.java`
- `backend/src/main/java/com/crmdexter/backend/controller/SolicitudAccesoController.java`
- `backend/src/main/java/com/crmdexter/backend/dto/AuthDto.java`
- `backend/src/main/java/com/crmdexter/backend/model/Usuario.java`
- `backend/src/main/java/com/crmdexter/backend/model/SolicitudAcceso.java`
- `backend/src/main/java/com/crmdexter/backend/repository/UsuarioRepository.java`
- `backend/src/main/java/com/crmdexter/backend/repository/SolicitudAccesoRepository.java`
- `backend/src/main/java/com/crmdexter/backend/service/Auth/LoginService.java`
- `backend/src/main/java/com/crmdexter/backend/service/Auth/OtpService.java`
- `backend/src/main/java/com/crmdexter/backend/service/Auth/JwtAuthenticationFilter.java`
- `backend/src/main/java/com/crmdexter/backend/config/GlobalExceptionHandler.java`
- `backend/src/main/resources/application.yml`

### Frontend

- `frontend/crmdx/src/App.tsx`
- `frontend/crmdx/src/context/AuthContext.tsx`
- `frontend/crmdx/src/components/auth/ProtectedRoute.tsx`
- `frontend/crmdx/src/components/layout/Header.tsx`
- `frontend/crmdx/src/pages/LoginPage.tsx`
- `frontend/crmdx/src/pages/Team/TeamPage.tsx`
- `frontend/crmdx/src/router/AppRouter.tsx`
- `frontend/crmdx/src/services/api.config.ts`
- `frontend/crmdx/src/types/auth.types.ts`

## Verificación

- Frontend `npm run build`: exitoso.
- Frontend `npm run lint`: exitoso.
- `git diff --check`: sin errores de espacios; Git mostró únicamente advertencias normales de conversión LF/CRLF.
- Backend: no se pudo ejecutar la compilación porque `mvn` no está instalado y no existe `backend/mvnw`.

## Verificación de ejecución posterior

- Fecha y hora: `2026-09-11 01:48:40 -05:00`.
- Java predeterminado detectado: Java 8, incompatible con el requisito Java 17.
- Java 17+ disponible en `C:\Program Files\Java\jdk-23` y Java 17 en `C:\Program Files\Java\jdk-17`.
- Maven no está disponible en `PATH`.
- MySQL responde en `localhost:3306`.
- El puerto `8080` está ocupado por una instancia Java ya iniciada.
- `GET http://localhost:8080/api/health`: exitoso.
- La instancia actualmente ejecutada devuelve el mensaje antiguo del login, por lo que debe recompilarse y reiniciarse para cargar estos cambios.
- `npm run build`: exitoso nuevamente.
- `npm run lint`: exitoso nuevamente.

## Segunda revisión

- Fecha y hora: `2026-09-11 02:14:14 -05:00`.
- Frontend `npm run build`: exitoso.
- Frontend `npm run lint`: exitoso.
- `GET /api/health`: exitoso.
- El backend continúa ejecutándose con Java 17 en el puerto `8080`.
- `POST /api/auth/login` continúa devolviendo la respuesta antigua, por lo que la instancia activa todavía no contiene los cambios nuevos.
- Conclusión: el frontend está listo, pero el flujo completo de autenticación sigue pendiente de recompilar y reiniciar el backend actualizado.

## Revisión de errores

- Fecha y hora: `2026-09-11 02:38:21 -05:00`.
- Corregido: `LoginPage` ahora actualiza `AuthContext` después de login administrativo o verificación OTP; antes la guardia podía devolver al usuario a `/`.
- Corregido: usuarios no administradores con estado `OTP_VERIFICADO` reciben un nuevo OTP al volver a iniciar sesión.
- Corregido: no se puede reprocesar una solicitud ya aprobada, rechazada o verificada.
- Corregido: no se puede utilizar el endpoint de aprobación para modificar una cuenta administradora.
- Verificado: frontend compila y pasa lint después de estas correcciones.
- Bloqueo restante: backend no está actualmente ejecutándose con el código nuevo y todavía no se ha ejecutado una compilación Maven real.

## Ajuste de conexión MySQL

- Fecha y hora: `2026-09-11 02:42:35 -05:00`.
- Se confirmó que `spring.datasource.username` debe utilizar `root` en este entorno, no `crm_dexter`.
- El archivo `application.yml` ya refleja el valor `root`.

## Normalización del rol administrador

- Se acepta `administrador`, `ADMINISTRADOR` o cualquier combinación de mayúsculas/minúsculas.
- Las verificaciones de rol y estado en login y endpoints administrativos ahora son case-insensitive.
- Fecha y hora: `2026-09-11 02:42:35 -05:00`.

## Corrección de correo y separación de responsabilidades

- Se configuró SMTP Gmail por defecto (`smtp.gmail.com:587`) con credenciales mediante variables de entorno.
- Se agregó STARTTLS y remitente configurable mediante `MAIL_FROM`.
- Se dejó de ocultar la excepción de envío: si SMTP falla, la aprobación devuelve error y no aparenta haber enviado el OTP.
- La lógica de autenticación fue trasladada a `services/auth.service.ts` y `hooks/useLoginFlow.ts`.
- La lógica de solicitudes fue trasladada a `services/accessRequest.service.ts` y `hooks/useAccessRequests.ts`.
- `LoginPage.tsx` y `TeamPage.tsx` quedaron enfocados en presentación y eventos de UI.
- Verificación de esta implementación: `2026-09-11 02:53:19 -05:00`; frontend build y lint exitosos, conectividad SMTP `smtp.gmail.com:587` disponible.

## Corrección de `/auth/me` y seguridad SMTP

- Se corrigió `SecurityConfig`: `/auth/me` ya no es público y requiere sesión válida.
- `AuthController` ahora responde `401` cuando `/auth/me` se solicita sin autenticación, en vez de producir `500` por `Authentication=null`.
- Se eliminaron valores predeterminados de correo del repositorio; deben configurarse mediante `MAIL_USERNAME`, `MAIL_PASSWORD` y `MAIL_FROM`.
- Se detectaron credenciales SMTP expuestas previamente en `application.yml`; deben revocarse y regenerarse inmediatamente en el proveedor de correo.
- Verificación: `2026-09-11 03:02:36 -05:00`; el proceso Java activo utiliza clases compiladas previamente, por lo que aún devuelve el `500` antiguo de `/auth/me`.

## Corrección de comprobación de sesión

- Se agregó `GET /api/auth/session`, público, para comprobar la existencia de sesión sin generar errores en la consola al abrir el login.
- `AuthContext` ahora consulta `/auth/session` y solo consulta `/auth/me` cuando existe una sesión autenticada.
- `/auth/me` continúa protegido y responde `401` cuando se solicita directamente sin cookie.
- La comprobación excluye explícitamente el `AnonymousAuthenticationToken`, que Spring puede crear aunque no exista una sesión real.
- Se configuró `HttpStatusEntryPoint` para que una petición sin cookie responda `401` en lugar de `403`.
- Se eliminaron los valores SMTP del archivo de configuración; el remitente y las credenciales deben existir únicamente en variables de entorno.
- Los fallos de `JavaMailSender` ahora devuelven `503` con un mensaje claro al administrador.
- Revisión aplicada: `2026-09-11 03:13:36 -05:00`.
- Fecha de cambio: `2026-09-11 03:02:36 -05:00`.

## Integración directa con Resend API

- Se reemplazó `JavaMailSender/SMTP` por la API HTTP oficial `https://api.resend.com/emails`.
- La API key se envía mediante `Authorization: Bearer` y se lee exclusivamente desde `RESEND_API_KEY`.
- Se agregó `ResendEmailService` y `EmailDeliveryException` dentro de `service/Email`.
- Los errores de Resend ahora se registran en el backend con su causa real y responden `503` sin exponer secretos.
- Se eliminó la API key que estaba escrita como valor predeterminado en `application.yml`.
- Fecha y hora: `2026-09-11 03:13:36 -05:00`.

## Verificación de sesión y diagnóstico Resend

- Fecha y hora: `2026-09-11 03:26:24 -05:00`.
- `GET /api/auth/session` sin cookie responde `{"authenticated":false}`.
- `GET /api/auth/me` sin cookie responde `401`.
- Login del administrador registrado en la BD responde `AUTHENTICATED`, crea cookie `crm_dexter_token` y permite consultar `/auth/me` con `200`.
- La configuración activa usa la API HTTP de Resend; el envío requiere `RESEND_API_KEY` en el proceso que inicia Spring Boot.
- Se trasladó la lógica de aprobación/rechazo a `service/AccessRequestService` y se agregó transacción para revertir la creación del usuario si el envío falla.
- Se confirmó el error `403 validation_error` de Resend: la cuenta está en modo prueba y solo admite el correo propietario.
- Para destinatarios externos se requiere verificar un dominio en Resend y usar un remitente perteneciente a ese dominio.
- Revisión y refactor aplicados: `2026-09-11 03:30:58 -05:00`.

## Cambio de proveedor a EmailJS

- Se reemplazó Resend por EmailJS para utilizar el correo conectado del remitente sin contraseña de Gmail.
- Se agregó `EmailJsEmailService` con la API `https://api.emailjs.com/api/v1.0/email/send`.
- El backend continúa generando, almacenando como hash y validando el OTP; EmailJS únicamente entrega el correo.
- Se requieren `EMAILJS_SERVICE_ID`, `EMAILJS_TEMPLATE_ID` y `EMAILJS_PUBLIC_KEY` en el proceso backend.
- Fecha y hora: `2026-09-11 03:30:58 -05:00`.

## Simplificación final del envío OTP

- Se eliminó la dependencia funcional de Resend.
- El envío utiliza directamente EmailJS con el servicio de correo conectado por el propietario.
- La aprobación sigue siendo transaccional y el OTP continúa protegido en el backend.
- Fecha y hora: `2026-09-11 03:36:16 -05:00`.

## EmailJS en modo estricto

- Se agregó `EMAILJS_PRIVATE_KEY` al servicio backend.
- La API de EmailJS recibe la clave privada en `accessToken`, tal como exige el modo estricto.
- La clave privada nunca se envía al frontend.
- Fecha y hora: `2026-09-11 03:58:44 -05:00`.

## Sidebar y proyectos reales

- Se agregó `ProjectContext` respaldado por `GET /api/proyectos`.
- El `Sidebar` ya no utiliza `mockProyectos`, ni muestra `Proyecto Activo` cuando el usuario no tiene proyectos.
- Cuando existen proyectos reales, el selector se alimenta exclusivamente de la respuesta del backend.
- Se agregó el botón `Cerrar sesión` en la parte inferior del `Sidebar`.
- Se eliminó el uso de datos demo del `Header`; ahora muestra el usuario y proyecto reales.
- `ProjectsPage` actualiza el contexto después de crear o eliminar un proyecto.
- Fecha y hora: `2026-09-11 04:03:00 -05:00`.

## Preparación segura para subir cambios

- Se eliminaron del código fuente la contraseña predeterminada de MySQL y la Private Key predeterminada de EmailJS.
- Ambas credenciales ahora deben definirse mediante variables de entorno.
- Se detectó que `backend/target` contiene archivos generados y rastreados por Git; no deben agregarse al commit de la funcionalidad.
- La Private Key que estuvo escrita en el archivo debe revocarse y regenerarse.
- Fecha y hora: `2026-09-11 04:07:57 -05:00`.

## Restauración de valores predeterminados

- Por solicitud del equipo, se restauraron en `application.yml` los valores predeterminados de base de datos, correo, JWT y EmailJS.
- Fecha y hora: `2026-09-11 04:09:00 -05:00`.

## Limpieza de elementos demo del sidebar

- Se eliminó el perfil ficticio `Usuario Demo / Administrador` del `Sidebar`.
- Se eliminó el selector visual `Rol Demo` y sus opciones de roles.
- Se conservaron la marca, selector de proyecto y navegación de la aplicación.
- Fecha y hora: `2026-09-11 03:53:32 -05:00`.

## Integración Resend

- Se cambió el relay SMTP de Gmail a `smtp.resend.com:587`.
- Resend utiliza el usuario SMTP `resend` y `RESEND_API_KEY` como contraseña SMTP.
- El remitente ahora es `onboarding@resend.dev` por defecto y puede sustituirse con `MAIL_FROM`.
- Se documentó la necesidad de verificar un dominio para enviar a destinatarios arbitrarios.
- Fecha y hora: `2026-09-11 03:13:36 -05:00`.

## Pendientes recomendados

- Instalar Maven o agregar Maven Wrapper y ejecutar `mvn test`.
- Configurar MailHog/Mailpit o SMTP real para comprobar el envío del OTP.
- Probar con una organización y un usuario `ADMINISTRADOR` reales en MySQL.
- Implementar permisos granulares si el administrador debe controlar permisos individuales, no solamente roles.
- Configurar `Secure=true` para la cookie cuando el sistema se ejecute bajo HTTPS.
