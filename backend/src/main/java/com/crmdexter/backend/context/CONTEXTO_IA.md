# CRM DEXTER: contexto para IA

Documento de referencia del frontend. Describe el estado actual del proyecto y evita asumir que existe un servidor dentro de este repositorio.

## 1. Objetivo

CRM DEXTER es la interfaz web de un CRM académico para proyectos de analítica de datos. El alcance actual de este repositorio es la experiencia visual y la navegación del cliente. El backend será una aplicación independiente desarrollada con Spring Boot.

La interfaz contempla estas áreas:

- Acceso y sesión de usuario.
- Proyectos y selección de proyecto activo.
- Carga y vista previa de datasets.
- Mapeo de columnas, limpieza y fusión.
- Dashboard de analítica y mapa de calor.
- Oportunidades de negocio.
- Módulos operativos con campos dinámicos.
- Usuarios, permisos y auditoría.
- Exportación de informes a PDF desde el navegador.

Todas las vistas funcionan con datos locales de demostración para conservar el diseño. No hay integración con backend ni persistencia real.

## 2. Stack y ejecución

### Frontend

- React 19 + TypeScript.
- Vite 6.
- Tailwind CSS 4 mediante `@tailwindcss/vite`.
- ECharts y `echarts-for-react` para visualizaciones.
- `lucide-react` para iconos.
- `papaparse` y `xlsx` para utilidades de archivos.
- `jspdf` y `html2canvas` para exportación PDF.
- No existe servidor Express, Node API ni capa de persistencia en este proyecto.

### Backend futuro

- Spring Boot será un proyecto independiente cuando se retome la integración.
- En esta etapa no debe agregarse código de conexión, autenticación real ni persistencia.

### Comandos

```bash
npm install
npm run dev       # Vite en http://localhost:5173
npm run lint      # comprobación TypeScript
npm run build     # compilación de producción
npm run preview   # vista previa de la compilación
```


## 3. Estructura del frontend

```text
src/
├── main.tsx                    Entrada React
├── App.tsx                     Composición y navegación por estado
├── index.css                   Estilos globales
├── types/index.ts              Tipos del cliente
├── data/demoData.ts            Datos locales para la maqueta
├── context/AuthContext.tsx     Sesión, roles y permisos
├── context/ProjectContext.tsx  Proyecto activo y bloqueos visuales
└── components/
    ├── auth/                   Acceso
    ├── layout/                 Sidebar y Header
    ├── projects/               Proyectos
    ├── datasets/               Datasets
    ├── mapping/                Mapeo
    ├── cleaning/               Limpieza
    ├── fusion/                 Fusión
    ├── analytics/              Dashboard y mapa
    ├── opportunities/          Oportunidades
    ├── modules/                Módulos operativos
    ├── team/                   Usuarios y permisos
    ├── audit/                  Auditoría
    └── reports/                Informe PDF
```

No se usa React Router. `App.tsx` mantiene un `activeTab` de tipo `TabKey` y renderiza las vistas condicionalmente.

## 4. Estado y navegación

`main.tsx` monta `App` dentro de `AuthProvider` y `ProjectProvider`.

`AuthContext` mantiene un usuario demo, empresa, permisos y estado de carga en memoria. No usa tokens ni almacenamiento persistente.

`ProjectContext` mantiene el proyecto activo y los bloqueos visuales en memoria. Los datos iniciales provienen de `src/data/demoData.ts`.

Las pestañas globales son proyectos, módulos operativos, usuarios y auditoría. El resto puede requerir un proyecto activo y datos procesados.

## 5. Datos locales

`src/data/demoData.ts` contiene usuarios, proyectos, datasets, filas de preview y estados de flujo mínimos para que la interfaz sea recorrible sin servicios externos. Los formularios sólo modifican estado en memoria y no representan operaciones persistentes.

## 6. Estado visual de las vistas

| Vista | Propósito | Estado actual |
|---|---|---|
| `LoginView.tsx` | Acceso por correo y verificación | Flujo visual con usuario demo |
| `ProjectListView.tsx` | Listar y seleccionar proyectos | Datos locales |
| `CreateProjectModal.tsx` | Crear proyecto | Agrega datos sólo en memoria |
| `DatasetUploadView.tsx` | Cargar, previsualizar y borrar archivos | Datos locales y preview visual |
| `DatasetCompareMappingView.tsx` | Comparar columnas y guardar mapeos | Diseño con datos de demostración |
| `DataCleaningView.tsx` | Mostrar acciones de limpieza | Estado local |
| `DatasetFusionView.tsx` | Mostrar fusión de datasets | Resultado local de demostración |
| `AnalyticsDashboardView.tsx` | KPIs, gráficos y mapa | Diseño con datos de demostración |
| `OpportunitiesView.tsx` | Mostrar y gestionar oportunidades | Estado local |
| `OperationalModulesView.tsx` | CRUD visual de módulos y registros | Estado local |
| `TeamPermissionsView.tsx` | Usuarios y permisos | Estado local |
| `AuditLogsView.tsx` | Historial de acciones | Datos locales |
| `ReportExportModal.tsx` | Exportar informe PDF | Composición PDF en el navegador con datos demo |

## 7. Reglas de mantenimiento

- El diseño existente es la prioridad: no cambiar colores, espaciado, tipografía, layout o jerarquía visual sin una solicitud explícita.
- Mantener el frontend como maqueta autónoma, sin llamadas HTTP ni clientes API.
- No crear carpetas `server/`, archivos Express, repositorios, esquemas SQL ni almacenamiento local de datos de negocio en este repositorio.
- No documentar como implementada una función que sólo tenga datos mock.
- Tratar las credenciales demo y los tokens como datos temporales de desarrollo, nunca como seguridad real.
- Después de cambios TypeScript ejecutar `npm run lint` y `npm run build`.

## 8. Integración futura

Cuando se retome el desarrollo, la conexión con Spring Boot deberá diseñarse desde cero sin modificar innecesariamente el layout. Hasta entonces, este repositorio debe permanecer como una maqueta visual autónoma.

**Regla de oro:** este repositorio contiene únicamente la interfaz React y datos temporales de demostración.
