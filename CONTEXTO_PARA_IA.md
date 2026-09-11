# CONTEXTO GLOBAL PARA IA — CRM DEXTER ACCESO
> Proyecto: `crm_dexter_acceso` | Fecha contexto: 2026-09-11 | Propósito: dar a cualquier IA todo el contexto necesario para continuar el desarrollo sin tener que re-explorar el repo.

---

## 1. VISIÓN GENERAL DEL PROYECTO

**CRM Dexter** es una plataforma de integración y análisis de datos (mini-CRM / ETL + BI):
Flujo de negocio: `Crear Proyecto → Cargar Datasets (CSV/XLSX) → Mapeo semántico a esquema canónico → Limpieza/Transformaciones → Fusión/Master dataset (Parquet) → Dashboards/KPIs + Oportunidades + Módulos operativos + Auditoría + Equipo`.

Monorepo con 2 apps separadas:
```
crm_dexter_acceso/
  backend/  -> Spring Boot 3.4.4 + Java 17 + MySQL 8 + JWT (jjwt 0.12.6) + JPA/Hibernate
  frontend/crmdx/ -> React 19.2.8 + Vite 8.2.2 + TypeScript ~6.0.2 + react-router-dom 7.18.1 + axios 1.19.0 + TailwindCSS 4.3.3 + lucide-react 0.553.0
  backend/dumps/Dump20260909/ -> 18 dumps .sql (uno por tabla)
  backend/src/main/java/com/crmdexter/backend/context/ -> CONTEXTO_IA.md + ESQUEMA_BASE_DATOS.md (fuente de verdad del modelo)
```

Puertos por defecto:
- Backend: `http://localhost:8080` (`/api/*`)
- Frontend: `http://localhost:5173` (Vite)
- BD: `jdbc:mysql://localhost:3306/crm_dexter`

Estado actual (MVP parcial):
- ✅ Funciona: `GET /api/health`, `POST /api/auth/login` (solo email, sin password/OTP), `GET/POST/DELETE /api/proyectos` con JWT, LoginPage + ProjectsPage + CreateProjectModal conectados.
- 🚧 Mock/placeholder: Header, Sidebar (usan `mockData.ts`), 8 páginas son `PendingPage` (Datasets, Mapeo, Limpieza, Fusión, Dashboard, Oportunidades, Módulos, Equipo, Auditoría).
- ❌ Falta: todo lo de Datasets, Mapeos, Transformaciones, Ejecuciones, Procesados, Dashboards, Widgets, Usuarios/Org, Solicitudes aprobación, OTP/Mail, roles reales.

---

## 2. BACKEND — `backend/` DETALLE EXHAUSTIVO

### 2.1 Stack y config (`pom.xml`, `application.yml`)
- Parent `spring-boot-starter-parent 3.4.4`, `java.version 17`, `jjwt.version 0.12.6`.
- Deps: `spring-boot-starter-web`, `spring-boot-starter-security`, `spring-boot-starter-data-jpa`, `spring-boot-starter-validation`, `spring-boot-starter-mail` (instalado pero NO usado), `mysql-connector-j (runtime)`, `jjwt-api/impl/jackson 0.12.6`, `spring-boot-starter-test`, `spring-security-test`.
- Build: solo `spring-boot-maven-plugin`. Paquete base: `com.crmdexter.backend`. Sin seeds, sin `@EnableJpaAuditing`.
- `src/main/resources/application.yml`:
```yaml
spring.datasource.url: ${DB_URL:jdbc:mysql://localhost:3306/crm_dexter}
spring.datasource.username: ${DB_USERNAME:root}
spring.datasource.password: ${DB_PASSWORD:Kevin20##}  # credencial compartida de desarrollo
spring.jpa.hibernate.ddl-auto: update
spring.jpa.open-in-view: false
spring.mail.host: ${MAIL_HOST:localhost}  port:1025 (MailHog)
server.port: ${SERVER_PORT:8080}
app.frontend-url: http://localhost:5173
app.jwt.secret: hWECRrGIJQckGPfyEqGxesnfYlEz71nw5Ci14VEqLGt  # Base64 compartido de desarrollo
app.jwt.expiration-ms: 86400000 (24h)
app.otp.expiration-seconds: 300 (no usado)
```
- `backend/README.md` está DESACTUALIZADO: habla de `request-otp/verify-otp`, `createDatabaseIfNotExist=true`, `JWT_SECRET` por env → nada de eso existe en código.

### 2.2 Arranque y salud
- `CrmDexterApplication.java`: solo `@SpringBootApplication` + `main`.
- `HealthController.java`: `GET /api/health` → `{"status":"ok","service":"CRM DEXTER Spring Boot API"}` (público).

### 2.3 Controllers (solo 2)
**`controller/AuthController.java` `@RequestMapping("/api/auth")`:**
- `POST /api/auth/login` body `AuthDto.LoginRequest{email}` (sin `@Valid`, var llamada `pepe`) → `LoginService.login(email)` → `AuthDto.LoginResponse{message, rol, token}` siempre 200. Sin password/OTP.

**`controller/ProyectoController.java` `@RequestMapping("/api/proyectos")` (requiere JWT):**
- `GET /api/proyectos` → `proyectoRepository.findByCreatedByOrderByCreatedAtDesc(usuarioActual(auth).getId())` → `List<ProyectoDto.Response>`. Sin paginación.
- `POST /api/proyectos` body `{nombre, rubro, descripcion}` (`@Valid` pero DTO sin constraints) → validación manual `nombre blank → 400`, `rubro null/blank ? "otros" : lowerCase`, crea `new Proyecto(orgId, userId, nombre.trim(), descripcion.trim(), rubro)` con `estado=CREADO` → `201 Response`.
- `DELETE /api/proyectos/{id:UUID}` → 404 si no existe, 403 si `createdBy != usuario.id`, sino `delete()` → 204.
- Helper `usuarioActual(Authentication)`: `usuarioRepository.findByEmail(auth.getName()).orElseThrow(401)`. Asume `sub=email`.
- NO existe `GET /{id}`, `PUT/PATCH`.

### 2.4 DTOs
- `dto/AuthDto.java`: `LoginRequest{email + getter/setter}`, `LoginResponse{message,rol,token}`.
- `dto/ProyectoDto.java` (`final class` ctor privado): `CrearRequest{nombre,rubro,descripcion}` sin anotaciones; `record Response(id, usuario_propietario_id (@JsonProperty), empresa_id (@JsonProperty, siempre null), nombre, rubro, descripcion, estado (lowercase), creado_en, actualizado_en)` + `from(Proyecto p)` donde `rubro ↔ Proyecto.tipoDato`.
- `dto/ErrorResponseDto.java`: `{status, message, timestamp}` usado solo en 2 handlers.

### 2.5 Repositories (solo 3)
- `UsuarioRepository extends JpaRepository<Usuario,UUID>`: `findByEmail(String)`.
- `ProyectoRepository`: `findByCreatedByOrderByCreatedAtDesc(UUID)`.
- `SolicitudAccesoRepository`: `existsByEmailSolicitanteAndEstado(email,estado)`, `findTopByEmailSolicitanteOrderByCreatedAtDesc` (no usada), y query NATIVA CON BUG:
```sql
SELECT CASE WHEN email=:email THEN 1 ELSE 0 END as EsAdmin FROM usuarios;
```
Falla si hay >1 usuario (`IncorrectResultSizeDataAccessException`) y es arbitrario. Debería ser `SELECT COUNT(*) FROM usuarios WHERE email=:email` o `EXISTS`.

### 2.6 Services Auth
- `service/Auth/JwtService.java`: ctor decodifica `app.jwt.secret` Base64 → `Keys.hmacShaKeyFor`. `generateToken(subject=email)` con `iat/exp`, `extractSubject()`, `isValid()` (try/catch → false). Sin roles/claims, sin refresh/blacklist.
- `service/Auth/LoginService.java` `@Transactional`: lógica 3 ramas:
  1. `isAdmin(email)>0` → `new LoginResponse("Bienvenido administrador","administrador", jwt)`.
  2. `existsBy...PENDIENTE` → `new LoginResponse("Tiene que esperar la aprobacion del admin.","","")` (sin token).
  3. else crea `SolicitudAcceso{email, estado=PENDIENTE, notificacionLeida=false, intentosOtp=0}` y retorna `"Solicitud enviada..."` (sin token). No verifica password, no crea Usuario, no toca `ultimo_acceso`.
- `service/Auth/JwtAuthenticationFilter.java extends OncePerRequestFilter`: lee `Authorization: Bearer <token>`, si `isValid` → `UsernamePasswordAuthenticationToken(subject, null, emptyList)` en `SecurityContext`. Sin authorities, sin lookup BD. Token inválido = anónimo → 401/403 después.

### 2.7 Config seguridad y errores
- `config/SecurityConfig.java` `@EnableWebSecurity`: `csrf.disable()`, `cors` solo `http://localhost:5173, http://localhost:3000` (methods GET/POST/PUT/PATCH/DELETE/OPTIONS, headers Authorization/Content-Type, `allowCredentials=true`), `sessionCreationPolicy=STATELESS`, `permitAll("/api/health","/api/auth/**","/error")`, resto `authenticated()`, `addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter)`. Bean `BCryptPasswordEncoder` (sin uso).
- `config/GlobalExceptionHandler.java` `@RestControllerAdvice`: maneja `jakarta.security.auth.message.AuthException → 401` (nunca lanzada) y `Exception → 500`. NO maneja `ResponseStatusException` (sale en formato Spring default), ni `MethodArgumentNotValidException`, ni `IllegalArgumentException` (login null → 500 en vez de 400).

### 2.8 Modelos JPA — 18 entidades (`model/*.java`)
Patrón: `@Id @GeneratedValue(UUID) @JdbcTypeCode(BINARY) @Column(BINARY(16)) UUID id` + `@CreationTimestamp/@UpdateTimestamp`. SIN `@ManyToOne/@OneToMany`: FKs son `UUID` escalares. Cascadas solo en DDL SQL, no JPA. Mayoría SIN getters/setters (solo Usuario/Solicitud/Proyecto tienen parciales) → hay que agregar accesores para usarlas.

| Entidad | Tabla | Campos clave |
|---|---|---|
| Organizacion | organizaciones | nombre(150)!, codigo(50)! unique, descripcion(500), activo=true |
| Usuario | usuarios | organizacion_id!, nombre!, email! unique, password_hash!, rol=ANALISTA, estado=ACTIVO, ultimo_acceso |
| SolicitudAcceso | solicitudes_acceso | email_solicitante!, estado=PENDIENTE, notificacion_leida=false, aprobado_por null, decidido_en, motivo_rechazo, otp_hash (solo hash), otp_expira_en, intentos_otp=0 |
| Proyecto | proyectos | organizacion_id!, created_by!, nombre!, codigo null, descripcion(1000), tipo_dato! (=rubro frontend), estado=CREADO; ctor (orgId,createdBy,nombre,descripcion,tipoDato) |
| Proveedor | proveedores | organizacion_id!, nombre!, codigo!, descripcion, activo |
| FuenteDato | fuentes_datos | proyecto_id!, proveedor_id!, nombre!, codigo, tipo_fuente=CSV, descripcion |
| Dataset | datasets | proyecto_id!, fuente_id!, nombre_original!, nombre_almacenado!, ruta_archivo!, hash_archivo, formato=CSV, tamano_bytes, filas_detectadas, estado=RECIBIDO, fecha_carga |
| DatasetColumna | dataset_columnas | dataset_id!, nombre_original!, nombre_normalizado!, tipo_detectado!, posicion!, es_obligatoria, total_nulos/unicos, porcentaje_nulos, ejemplo_valor, estadisticas_json (Map/JSON) |
| EsquemaCanonico | esquemas_canonicos | proyecto_id!, nombre!, version=1, activo |
| EsquemaCampo | esquema_campos | esquema_id!, nombre_canonico!, etiqueta!, tipo_dato!, descripcion, es_obligatorio, posicion |
| MapeoColumna | mapeos_columnas | dataset_columna_id!, esquema_campo_id!, metodo!, confianza 0-1, estado=SUGERIDO, es_confirmado, confirmado_por |
| EjecucionProcesamiento | ejecuciones_procesamiento | proyecto_id!, ejecutado_por!, tipo_ejecucion!, estado=PENDIENTE, filas_entrada/salida, mensaje_error TEXT (omite inicio/fin del SQL) |
| Transformacion | transformaciones | dataset_id!, dataset_columna_id, ejecucion_id, created_by!, tipo_transformacion!, configuracion_json (Map), orden=1, filas_afectadas, estado=PENDIENTE |
| EjecucionDataset | ejecucion_datasets | ejecucion_id!, dataset_id!, filas_procesadas (puente N:M) |
| DatasetProcesado | datasets_procesados | proyecto_id!, ejecucion_id!, nombre_archivo!, ruta_archivo!, formato=PARQUET, hash, tamano, num_filas/columnas, version=1 |
| DatasetProcesadoColumna | dataset_procesado_columnas | dataset_procesado_id!, nombre!, tipo_dato!, posicion, estadisticas_json |
| Dashboard | dashboards | proyecto_id!, dataset_procesado_id, created_by!, nombre!, descripcion, activo |
| WidgetDashboard | widgets_dashboard | dashboard_id!, titulo!, tipo_widget!, posicion_x/y, ancho=4, alto=3, orden, configuracion_json! |

Ver `backend/src/main/java/com/crmdexter/backend/context/ESQUEMA_BASE_DATOS.md` (489 líneas, DDL completo MySQL 8 utf8mb4, CHECKs, FKs, UNIQUEs, ejemplos `UUID_TO_BIN`) y `CONTEXTO_IA.md` (126 líneas, descripción frontend original + regla "no agregar backend al repo frontend").

Orden dependencia BD: organizaciones → usuarios → solicitudes_acceso → proyectos → proveedores → fuentes_datos → datasets → dataset_columnas → esquemas_canonicos → esquema_campos → mapeos_columnas → ejecuciones_procesamiento → transformaciones + ejecucion_datasets → datasets_procesados → dataset_procesado_columnas → dashboards → widgets_dashboard. Roles: ADMINISTRADOR/PROPIETARIO/ANALISTA/OPERATIVO. Estados proyecto: CREADO/CON_DATASETS/EN_MAPEO/EN_LIMPIEZA/PROCESADO/ARCHIVADO. Widgets: KPI/BARRAS/LINEAS/AREA/TORTA/TABLA/MAPA/DISPERSION.

---

## 3. FRONTEND — `frontend/crmdx/` DETALLE EXHAUSTIVO

### 3.1 Stack (`package.json`, `vite.config.ts`, `index.html`)
```json
dependencies: react 19.2.8, react-dom 19.2.8, react-router-dom 7.18.1, axios 1.19.0, lucide-react 0.553.0, tailwindcss 4.3.3, @tailwindcss/vite 4.3.3
devDeps: typescript ~6.0.2, vite 8.2.2, @vitejs/plugin-react 6.1.0, oxlint 1.79.0, @types/react 19.2.18
scripts: dev/build (tsc -b && vite build)/lint/preview
```
- `vite.config.ts`: `plugins[react(), tailwindcss()]`, alias `@→./src`, `outDir dist`. Sin proxy.
- `index.html`: `lang=en`, `<title>CRM Dexter</title>`, `#root`, `/src/main.tsx`, favicon `/favicon.svg`.
- Estado: solo `useState` local, sin Context/Redux/Zustand/ReactQuery. `hooks/`, `assets/`, `img/` vacíos (`.gitkeep`).

### 3.2 Estructura `src/`
```
App.tsx, main.tsx, index.css
router/AppRouter.tsx (router real + ROUTES), router/routes.ts (PUBLIC_ROUTES+APP_ROUTES duplicados)
types/auth.types.ts, types/project.types.ts
services/api.config.ts (axios), services/mockData.ts (mocks)
components/layout/AppLayout.tsx, Header.tsx, Sidebar.tsx
components/projects/CreateProjectModal.tsx
components/common/PendingPage.tsx
pages/LoginPage.tsx, Projects/ProjectsPage.tsx, Datasets/DatasetsPage.tsx, Mapping/MappingPage.tsx, Cleaning/CleaningPage.tsx, Fusion/FusionPage.tsx, Analytics/AnalyticsPage.tsx, Opportunities/OpportunitiesPage.tsx, Modules/ModulesPage.tsx, Team/TeamPage.tsx, Audit/AuditPage.tsx
```

### 3.3 Router y guards
- `routes.ts`: `PUBLIC_ROUTES={LOGIN:'/'}`, `APP_ROUTES={ROOT:'/app', PROYECTOS:'/app/proyectos', DATASETS:'/app/datasets', MAPEO:'/app/mapeo', LIMPIEZA:'/app/limpieza', FUSION:'/app/fusion', DASHBOARD:'/app/dashboard', OPORTUNIDADES:'/app/oportunidades', MODULOS_OPERATIVOS:'/app/modulos-operativos', USUARIOS:'/app/usuarios', AUDITORIA:'/app/auditoria'}`.
- `AppRouter.tsx`: `ROUTES` idéntico duplicado + `routes=[{/:LoginPage}, {/app:AppLayout + children[ index→proyectos, proyectos, datasets, mapeo, limpieza, fusion, dashboard, oportunidades, modulos-operativos, usuarios, auditoria]}, {*→/}]` con `createBrowserRouter` en `App.tsx` + `RouterProvider`.
- ⚠️ SIN `ProtectedRoute`/guard/loader: cualquier URL `/app/*` es accesible sin token. `Header.handleLogout` solo `navigate('/')` sin limpiar `localStorage`.

### 3.4 Types (completos)
```ts
// auth.types.ts
RolUsuario='administrador'|'propietario_empresa'|'analista'|'usuario_operativo';
EstadoUsuario='activo'|'inactivo'|'suspendido';
Usuario{id, empresa_id, nombre, email, rol, estado, ultimo_acceso?, creado_en, actualizado_en}
LoginCredentials{email, password} // password no usado
// project.types.ts
RubroProyecto='ventas'|'comercio'|'demografia'|'poblacion'|'territorial'|'inventario'|'otros';
EstadoProyecto='creado'|'con_datasets'|'en_mapeo'|'en_limpieza'|'procesado'|'archivado';
Proyecto{id, usuario_propietario_id, empresa_id, nombre, rubro, descripcion?, estado, creado_en, actualizado_en}
```
Desfase: UI solo ofrece `ventas/demografia/territorial/inventario` (CreateProjectModal, ProjectsPage badges 💰👥🗺️📦), tipos contemplan 7.

### 3.5 Services
- `api.config.ts`: `apiClient=axios.create({baseURL: VITE_API_URL ?? 'http://localhost:8080/api', Content-Type:json})` + interceptor request inyecta `Authorization: Bearer <localStorage crm_dexter_token>`. Sin interceptor response/401, sin `VITE_API_URL` en repo.
- `mockData.ts`: `mockUsuario{usr_001/demo@crmdexter.com/administrador}`, `mockEmpresa{emp_001/Mi Empresa}`, `mockProyectos[proj_001 Ventas Norte/procesado, proj_002 Demografía Lima/en_mapeo, proj_003 Inventario/con_datasets]`, `randomStat()`. Cabecera dice "se reemplazarán por backend".

### 3.6 Components layout + modal
- `AppLayout.tsx`: `h-screen flex`: `<Sidebar/>` + `<Header/><main><Outlet/></main>` (`bg-slate-100`, `max-w-7xl`, `custom-scrollbar`).
- `Header.tsx`: sin props/estado. `activeProject=mockProyectos[0]` hardcodeado. Stages `Carga(DATASETS,✓)→Mapeo(MAPEO,✓)→Fusión(FUSION,○)→Dashboard(DASHBOARD,○)` con `navigate`. Muestra `mockEmpresa`, `activeProject.rubro`, botón `Exportar Informe PDF → navigate(DASHBOARD)` (no exporta), `mockUsuario`, `LogOut → navigate('/')`. Iconos FileDown/LogOut/Building2/CheckCircle2/Circle. 100% mock.
- `Sidebar.tsx`: `activeProjectId useState(mockProyectos[0].id)`. 10 navItems (Proyectos/Layers, Carga/Database, Mapeo/Sparkles, Limpieza/Activity, Fusión/GitMerge, Dashboard/BarChart3, Oportunidades/Lightbulb, Tablas/Table2, Equipo/Users, Auditoría/ShieldCheck) con `NavLink` activo `bg-sky-600`. `<select>` proyecto mock, Role Switcher visual (resalta mockUsuario.rol, sin onClick), footer perfil inicial. Todo mock, diverge de `activeProjectId` real de ProjectsPage.
- `CreateProjectModal.tsx` `{isOpen,onClose,onSuccess}`: `nombre, rubro='ventas', descripcion, isSaving, error`. `handleSubmit → POST /proyectos {nombre.trim(), rubro, descripcion.trim()} → onSuccess(data)`. Early return `if(!isOpen) return null` tras hooks. Error: "No se pudo crear... backend esté activo".
- `PendingPage.tsx` `{title,description,icon:LucideIcon}`: card dashed + "Diseño pendiente de portar desde el prototipo original". Usado por 8 páginas.

### 3.7 Pages
- `LoginPage.tsx`: `email,error,isLoading`. `POST /auth/login {email}` → si `!token` muestra `message` ("todavía no tiene acceso"), si token → `localStorage crm_dexter_token` + `navigate(/app/proyectos)`. Catch → "No se pudo conectar con el backend". UI 2 col: branding slate-900 (logo Database gradient, stages, "Acceso protegido") + form 400px (icono Mail, input email). Solo email, sin password.
- `ProjectsPage.tsx`: `projects,activeProjectId,isCreateOpen,isLoading,error`. `useEffect GET /proyectos → setProjects`. `handleDelete → confirm() + DELETE /proyectos/:id + filtro local`. Grid md:2 lg:3 cards (`onClick setActive + navigate(DATASETS)`), badge rubro, etiqueta ACTIVO, Trash hover, estados Cargando/Vacío. Integra `CreateProjectModal onSuccess=prepend+activar+cerrar`.
- 8 placeholders `PendingPage`: Datasets(Carga CSV/XLSX/Database), Mapping(Mapeo Semántico/Sparkles), Cleaning(Limpieza/Activity), Fusion(Fusión & Master/GitMerge), Analytics(Dashboard Analítico/BarChart3), Opportunities(Oportunidades/Lightbulb), Modules(Tablas Adaptativas/Table2), Team(Equipo & Permisos/Users), Audit(Auditoría/ShieldCheck).

### 3.8 Estilos `index.css`
```css
@import "tailwindcss";
@theme{ --font-display:Space Grotesk; --font-body:Inter; --font-mono:IBM Plex Mono; --color-ink:#10201d; --color-ink-soft:#4b5d58; --color-paper:#f4f6f4; --color-line:#d8dfdb; --color-pine:#1f6f5c; --color-clay:#b5652e; --color-steel:#3b7a94; ... }
.custom-scrollbar (6px, thumb #cbd5e1)
```
Paleta custom casi NO usada (código usa slate/sky/emerald/indigo). Tailwind v4 sin config JS.

---

## 4. CONTRATO API ACTUAL + FLUJOS

Base: `http://localhost:8080/api`. Auth: `Authorization: Bearer <JWT 24h, sub=email, sin roles>`.

| Método | Ruta | Auth | Req | Res | Frontend que lo usa |
|---|---|---|---|---|---|
| GET | /health | no | — | {status,service} | ninguno (manual) |
| POST | /auth/login | no | {email} | {message,rol,token} token="" si pendiente | LoginPage.tsx:21 |
| GET | /proyectos | sí | — | Proyecto[]{id,usuario_propietario_id,empresa_id:null,nombre,rubro,descripcion,estado,creado_en,actualizado_en} | ProjectsPage.tsx:28 |
| POST | /proyectos | sí | {nombre,rubro,descripcion} | 201 Proyecto | CreateProjectModal.tsx:34 |
| DELETE | /proyectos/:id | sí | — | 204 / 404 / 403 | ProjectsPage.tsx:36 |

Flujo login: `LoginPage email → POST /auth/login → si token guarda + /app/proyectos, si no muestra message`. Backend: `isAdmin? token admin : pendiente? espera : crea solicitud PENDIENTE`.
Flujo proyectos: `GET lista por createdBy → crear (rubro lower, estado CREADO) → click card activa + va a datasets → delete con confirm si dueño`.

---

## 5. DEUDA TÉCNICA / BUGS CONOCIDOS (no romper, corregir a propósito)
1. `SolicitudAccesoRepository.isAdmin` SQL multi-fila roto → reescribir con `COUNT/EXISTS + WHERE`.
2. Auth sin password/OTP: `otp_*`, `spring-mail`, `app.otp.*`, `PasswordEncoder` sin uso; README OTP inexistente.
3. `GlobalExceptionHandler` no cubre `ResponseStatusException`/`MethodArgumentNotValid`/`IllegalArgumentException` → formatos inconsistentes.
4. Secret JWT + password DB hardcodeados en `application.yml`; CORS solo localhost; JWT sin roles (`emptyList`).
5. Entidades sin relaciones JPA ni getters → agregar accesores según se necesite, no borrar `BINARY(16)` ni CHECKs.
6. Frontend sin `ProtectedRoute`, sin Context proyecto/usuario, logout no limpia token, sin manejo 401, `ROUTES` duplicado, Header/Sidebar 100% mock divergentes, 8 páginas placeholder, tipos Empresa/Dataset/Mapeo/etc faltantes.
7. `ProyectoDto.empresa_id` siempre null; `EjecucionProcesamiento` omite inicio/fin; `rubro UI (4) vs tipo (7)`; paleta `@theme` sin usar; `hooks/` vacío; sin tests.

## 6. CÓMO CORRER (para IA que vaya a verificar)
- Backend: `cd backend; ./mvnw spring-boot:run` o `mvn spring-boot:run` (requiere MySQL `crm_dexter` + env `DB_URL/DB_USERNAME/DB_PASSWORD` o defaults root/Kevin20##; `ddl-auto:update` crea tablas). Probar `curl localhost:8080/api/health`.
- Frontend: `cd frontend/crmdx; npm install; npm run dev` (5173, `VITE_API_URL` opcional, default 8080/api). `npm run build = tsc -b && vite build`, `npm run lint = oxlint`.
- Dumps: `backend/dumps/Dump20260909/*.sql` (18 tablas) para reconstruir/comparar esquema.

## 7. REGLAS PARA LA IA QUE CONTINÚE
- No cambiar puertos/baseURL sin avisar; mantener snake_case del backend (`usuario_propietario_id, empresa_id, creado_en`) en DTOs/Responses.
- `rubro` frontend ↔ `tipo_dato` backend (lowercase, default "otros").
- IDs `UUID string` en JSON, `BINARY(16)` en MySQL (`UUID_TO_BIN/BIN_TO_UUID`).
- Primero corregir `isAdmin`, agregar `ProtectedRoute` + `AuthContext/ProjectContext`, conectar Header/Sidebar a datos reales, luego implementar Datasets→Mapeo→Limpieza→Fusión→Dashboard en ese orden.
- No inventar endpoints: si creas uno nuevo en Spring, crea su consumo en `api.config.ts`/página y su tipo en `types/`.
- Mantener Tailwind v4 (`@import "tailwindcss"` + `@theme`), iconos `lucide-react`, router `createBrowserRouter`.
