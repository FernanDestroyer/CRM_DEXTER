# Esquema Base De Datos CRM DEXTER

Esquema inicial para MySQL 8 y Spring Boot. Está diseñado para recibir varios CSV de distintos proveedores, detectar y mapear columnas, aplicar transformaciones, generar un archivo procesado ligero y configurar dashboards.

## Decisiones

- Los identificadores se almacenan como `BINARY(16)` y se manejan como `UUID` en Spring Boot.
- Para crear UUID desde SQL se usa `UUID_TO_BIN(UUID())`.
- Los archivos no se guardan dentro de MySQL. Se guarda su ruta, hash y metadata.
- Las configuraciones variables de transformaciones y widgets usan `JSON`.
- El archivo consolidado recomendado es Parquet.
- Los códigos legibles son campos separados; no reemplazan al UUID.

## Creación

Ejecutar el siguiente script con MySQL 8.0. El usuario de la aplicación debe tener permisos para crear las tablas y las relaciones.

```sql
CREATE DATABASE IF NOT EXISTS crm_dexter
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE crm_dexter;

-- ================================================================
-- ORGANIZACION Y USUARIOS
-- ================================================================

CREATE TABLE IF NOT EXISTS organizaciones (
    id              BINARY(16) NOT NULL,
    nombre          VARCHAR(150) NOT NULL,
    codigo          VARCHAR(50) NOT NULL,
    descripcion     VARCHAR(500) NULL,
    activo          BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at      DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
                    ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_organizaciones_codigo (codigo)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS usuarios (
    id              BINARY(16) NOT NULL,
    organizacion_id BINARY(16) NOT NULL,
    nombre          VARCHAR(150) NOT NULL,
    email           VARCHAR(180) NOT NULL,
    password_hash   VARCHAR(255) NOT NULL,
    rol             VARCHAR(40) NOT NULL DEFAULT 'ANALISTA',
    estado          VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    ultimo_acceso   DATETIME(6) NULL,
    created_at      DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at      DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
                    ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_usuarios_email (email),
    KEY idx_usuarios_organizacion (organizacion_id),
    CONSTRAINT fk_usuarios_organizacion
        FOREIGN KEY (organizacion_id) REFERENCES organizaciones (id),
    CONSTRAINT chk_usuarios_rol
        CHECK (rol IN ('ADMINISTRADOR', 'PROPIETARIO', 'ANALISTA', 'OPERATIVO')),
    CONSTRAINT chk_usuarios_estado
        CHECK (estado IN ('ACTIVO', 'INACTIVO', 'SUSPENDIDO'))
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS solicitudes_acceso (
    id                  BINARY(16) NOT NULL,
    email_solicitante   VARCHAR(180) NOT NULL,
    estado              VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    notificacion_leida  BOOLEAN NOT NULL DEFAULT FALSE,
    aprobado_por        BINARY(16) NULL,
    decidido_en         DATETIME(6) NULL,
    motivo_rechazo      VARCHAR(500) NULL,
    otp_hash            VARCHAR(255) NULL,
    otp_expira_en       DATETIME(6) NULL,
    intentos_otp        INT UNSIGNED NOT NULL DEFAULT 0,
    created_at          DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at          DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
                        ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    KEY idx_solicitudes_acceso_estado (estado),
    KEY idx_solicitudes_acceso_email (email_solicitante),
    CONSTRAINT fk_solicitudes_acceso_admin
        FOREIGN KEY (aprobado_por) REFERENCES usuarios (id) ON DELETE SET NULL,
    CONSTRAINT chk_solicitudes_acceso_estado
        CHECK (estado IN ('PENDIENTE', 'APROBADA', 'RECHAZADA', 'OTP_VERIFICADO', 'EXPIRADA'))
) ENGINE = InnoDB;

-- ================================================================
-- PROYECTOS, PROVEEDORES Y FUENTES
-- ================================================================

CREATE TABLE IF NOT EXISTS proyectos (
    id                  BINARY(16) NOT NULL,
    organizacion_id     BINARY(16) NOT NULL,
    created_by          BINARY(16) NOT NULL,
    nombre              VARCHAR(180) NOT NULL,
    codigo              VARCHAR(60) NULL,
    descripcion         VARCHAR(1000) NULL,
    tipo_dato           VARCHAR(40) NOT NULL,
    estado              VARCHAR(30) NOT NULL DEFAULT 'CREADO',
    created_at          DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at          DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
                        ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_proyecto_codigo_organizacion (organizacion_id, codigo),
    KEY idx_proyectos_organizacion (organizacion_id),
    KEY idx_proyectos_created_by (created_by),
    CONSTRAINT fk_proyectos_organizacion
        FOREIGN KEY (organizacion_id) REFERENCES organizaciones (id),
    CONSTRAINT fk_proyectos_created_by
        FOREIGN KEY (created_by) REFERENCES usuarios (id),
    CONSTRAINT chk_proyectos_estado
        CHECK (estado IN ('CREADO', 'CON_DATASETS', 'EN_MAPEO', 'EN_LIMPIEZA', 'PROCESADO', 'ARCHIVADO'))
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS proveedores (
    id                  BINARY(16) NOT NULL,
    organizacion_id     BINARY(16) NOT NULL,
    nombre              VARCHAR(150) NOT NULL,
    codigo              VARCHAR(60) NOT NULL,
    descripcion         VARCHAR(500) NULL,
    activo              BOOLEAN NOT NULL DEFAULT TRUE,
    created_at          DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at          DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
                        ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_proveedor_codigo_organizacion (organizacion_id, codigo),
    KEY idx_proveedores_organizacion (organizacion_id),
    CONSTRAINT fk_proveedores_organizacion
        FOREIGN KEY (organizacion_id) REFERENCES organizaciones (id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS fuentes_datos (
    id                  BINARY(16) NOT NULL,
    proyecto_id         BINARY(16) NOT NULL,
    proveedor_id        BINARY(16) NOT NULL,
    nombre              VARCHAR(150) NOT NULL,
    codigo              VARCHAR(60) NULL,
    tipo_fuente         VARCHAR(30) NOT NULL DEFAULT 'CSV',
    descripcion         VARCHAR(500) NULL,
    created_at          DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at          DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
                        ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_fuente_codigo_proyecto (proyecto_id, codigo),
    KEY idx_fuentes_proveedor (proveedor_id),
    CONSTRAINT fk_fuentes_proyecto
        FOREIGN KEY (proyecto_id) REFERENCES proyectos (id) ON DELETE CASCADE,
    CONSTRAINT fk_fuentes_proveedor
        FOREIGN KEY (proveedor_id) REFERENCES proveedores (id)
) ENGINE = InnoDB;

-- ================================================================
-- ARCHIVOS RECIBIDOS Y COLUMNAS DETECTADAS
-- ================================================================

CREATE TABLE IF NOT EXISTS datasets (
    id                  BINARY(16) NOT NULL,
    proyecto_id         BINARY(16) NOT NULL,
    fuente_id           BINARY(16) NOT NULL,
    nombre_original     VARCHAR(255) NOT NULL,
    nombre_almacenado   VARCHAR(255) NOT NULL,
    ruta_archivo        VARCHAR(1000) NOT NULL,
    hash_archivo        CHAR(64) NULL,
    formato             VARCHAR(20) NOT NULL DEFAULT 'CSV',
    tamano_bytes        BIGINT UNSIGNED NOT NULL DEFAULT 0,
    filas_detectadas    BIGINT UNSIGNED NOT NULL DEFAULT 0,
    estado              VARCHAR(30) NOT NULL DEFAULT 'RECIBIDO',
    fecha_carga         DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    created_at          DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at          DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
                        ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    KEY idx_datasets_proyecto (proyecto_id),
    KEY idx_datasets_fuente (fuente_id),
    KEY idx_datasets_hash (hash_archivo),
    CONSTRAINT fk_datasets_proyecto
        FOREIGN KEY (proyecto_id) REFERENCES proyectos (id) ON DELETE CASCADE,
    CONSTRAINT fk_datasets_fuente
        FOREIGN KEY (fuente_id) REFERENCES fuentes_datos (id) ON DELETE CASCADE,
    CONSTRAINT chk_datasets_formato
        CHECK (formato IN ('CSV', 'XLSX')),
    CONSTRAINT chk_datasets_estado
        CHECK (estado IN ('RECIBIDO', 'VALIDADO', 'ANALIZADO', 'MAPEADO', 'LIMPIADO', 'PROCESADO', 'ERROR'))
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS dataset_columnas (
    id                  BINARY(16) NOT NULL,
    dataset_id          BINARY(16) NOT NULL,
    nombre_original     VARCHAR(255) NOT NULL,
    nombre_normalizado  VARCHAR(255) NOT NULL,
    tipo_detectado      VARCHAR(30) NOT NULL,
    posicion            INT UNSIGNED NOT NULL,
    es_obligatoria      BOOLEAN NOT NULL DEFAULT FALSE,
    total_nulos         BIGINT UNSIGNED NOT NULL DEFAULT 0,
    total_unicos        BIGINT UNSIGNED NOT NULL DEFAULT 0,
    porcentaje_nulos    DECIMAL(7,4) NOT NULL DEFAULT 0,
    ejemplo_valor      VARCHAR(500) NULL,
    estadisticas_json   JSON NULL,
    created_at          DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_columna_posicion_dataset (dataset_id, posicion),
    KEY idx_columnas_dataset (dataset_id),
    CONSTRAINT fk_columnas_dataset
        FOREIGN KEY (dataset_id) REFERENCES datasets (id) ON DELETE CASCADE,
    CONSTRAINT chk_columnas_tipo
        CHECK (tipo_detectado IN ('TEXTO', 'ENTERO', 'DECIMAL', 'FECHA', 'BOOLEANO', 'COORDENADA', 'CATEGORIA'))
) ENGINE = InnoDB;

-- ================================================================
-- ESQUEMA CANONICO Y MAPEO SEMANTICO
-- ================================================================

CREATE TABLE IF NOT EXISTS esquemas_canonicos (
    id                  BINARY(16) NOT NULL,
    proyecto_id         BINARY(16) NOT NULL,
    nombre              VARCHAR(150) NOT NULL,
    version             INT UNSIGNED NOT NULL DEFAULT 1,
    activo              BOOLEAN NOT NULL DEFAULT TRUE,
    created_at          DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at          DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
                        ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_esquema_version (proyecto_id, nombre, version),
    CONSTRAINT fk_esquemas_proyecto
        FOREIGN KEY (proyecto_id) REFERENCES proyectos (id) ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS esquema_campos (
    id                  BINARY(16) NOT NULL,
    esquema_id          BINARY(16) NOT NULL,
    nombre_canonico     VARCHAR(150) NOT NULL,
    etiqueta            VARCHAR(180) NOT NULL,
    tipo_dato           VARCHAR(30) NOT NULL,
    descripcion         VARCHAR(500) NULL,
    es_obligatorio      BOOLEAN NOT NULL DEFAULT FALSE,
    posicion            INT UNSIGNED NOT NULL,
    created_at          DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_campo_canonico_esquema (esquema_id, nombre_canonico),
    UNIQUE KEY uk_campo_posicion_esquema (esquema_id, posicion),
    CONSTRAINT fk_campos_esquema
        FOREIGN KEY (esquema_id) REFERENCES esquemas_canonicos (id) ON DELETE CASCADE
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS mapeos_columnas (
    id                  BINARY(16) NOT NULL,
    dataset_columna_id  BINARY(16) NOT NULL,
    esquema_campo_id    BINARY(16) NOT NULL,
    metodo              VARCHAR(30) NOT NULL,
    confianza           DECIMAL(6,5) NOT NULL DEFAULT 0,
    estado              VARCHAR(20) NOT NULL DEFAULT 'SUGERIDO',
    es_confirmado       BOOLEAN NOT NULL DEFAULT FALSE,
    confirmado_por      BINARY(16) NULL,
    created_at          DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at          DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
                        ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_mapeo_columna_campo (dataset_columna_id, esquema_campo_id),
    KEY idx_mapeos_campo (esquema_campo_id),
    CONSTRAINT fk_mapeos_dataset_columna
        FOREIGN KEY (dataset_columna_id) REFERENCES dataset_columnas (id) ON DELETE CASCADE,
    CONSTRAINT fk_mapeos_esquema_campo
        FOREIGN KEY (esquema_campo_id) REFERENCES esquema_campos (id) ON DELETE CASCADE,
    CONSTRAINT fk_mapeos_confirmado_por
        FOREIGN KEY (confirmado_por) REFERENCES usuarios (id) ON DELETE SET NULL,
    CONSTRAINT chk_mapeos_confianza
        CHECK (confianza >= 0 AND confianza <= 1),
    CONSTRAINT chk_mapeos_estado
        CHECK (estado IN ('SUGERIDO', 'CONFIRMADO', 'RECHAZADO'))
) ENGINE = InnoDB;

-- ================================================================
-- LIMPIEZA Y EJECUCIONES DEL PIPELINE
-- ================================================================

CREATE TABLE IF NOT EXISTS ejecuciones_procesamiento (
    id                  BINARY(16) NOT NULL,
    proyecto_id         BINARY(16) NOT NULL,
    ejecutado_por       BINARY(16) NOT NULL,
    tipo_ejecucion      VARCHAR(30) NOT NULL,
    estado              VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    inicio              DATETIME(6) NULL,
    fin                 DATETIME(6) NULL,
    filas_entrada       BIGINT UNSIGNED NOT NULL DEFAULT 0,
    filas_salida        BIGINT UNSIGNED NOT NULL DEFAULT 0,
    mensaje_error       TEXT NULL,
    created_at          DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    KEY idx_ejecuciones_proyecto (proyecto_id),
    CONSTRAINT fk_ejecuciones_proyecto
        FOREIGN KEY (proyecto_id) REFERENCES proyectos (id) ON DELETE CASCADE,
    CONSTRAINT fk_ejecuciones_usuario
        FOREIGN KEY (ejecutado_por) REFERENCES usuarios (id),
    CONSTRAINT chk_ejecuciones_tipo
        CHECK (tipo_ejecucion IN ('ANALISIS', 'LIMPIEZA', 'FUSION', 'GENERACION_DATASET')),
    CONSTRAINT chk_ejecuciones_estado
        CHECK (estado IN ('PENDIENTE', 'EJECUTANDO', 'COMPLETADA', 'ERROR'))
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS transformaciones (
    id                  BINARY(16) NOT NULL,
    dataset_id          BINARY(16) NOT NULL,
    dataset_columna_id  BINARY(16) NULL,
    ejecucion_id        BINARY(16) NULL,
    created_by          BINARY(16) NOT NULL,
    tipo_transformacion VARCHAR(40) NOT NULL,
    configuracion_json  JSON NULL,
    orden_ejecucion     INT UNSIGNED NOT NULL DEFAULT 1,
    filas_afectadas     BIGINT UNSIGNED NOT NULL DEFAULT 0,
    estado              VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    created_at          DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    KEY idx_transformaciones_dataset (dataset_id),
    KEY idx_transformaciones_ejecucion (ejecucion_id),
    CONSTRAINT fk_transformaciones_dataset
        FOREIGN KEY (dataset_id) REFERENCES datasets (id) ON DELETE CASCADE,
    CONSTRAINT fk_transformaciones_columna
        FOREIGN KEY (dataset_columna_id) REFERENCES dataset_columnas (id) ON DELETE SET NULL,
    CONSTRAINT fk_transformaciones_ejecucion
        FOREIGN KEY (ejecucion_id) REFERENCES ejecuciones_procesamiento (id) ON DELETE SET NULL,
    CONSTRAINT fk_transformaciones_usuario
        FOREIGN KEY (created_by) REFERENCES usuarios (id),
    CONSTRAINT chk_transformaciones_estado
        CHECK (estado IN ('PENDIENTE', 'APLICADA', 'REVERTIDA', 'ERROR'))
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS ejecucion_datasets (
    id                 BINARY(16) NOT NULL,
    ejecucion_id       BINARY(16) NOT NULL,
    dataset_id         BINARY(16) NOT NULL,
    filas_procesadas   BIGINT UNSIGNED NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_ejecucion_dataset (ejecucion_id, dataset_id),
    CONSTRAINT fk_ejecucion_datasets_ejecucion
        FOREIGN KEY (ejecucion_id) REFERENCES ejecuciones_procesamiento (id) ON DELETE CASCADE,
    CONSTRAINT fk_ejecucion_datasets_dataset
        FOREIGN KEY (dataset_id) REFERENCES datasets (id) ON DELETE CASCADE
) ENGINE = InnoDB;

-- ================================================================
-- ARCHIVO PROCESADO FINAL
-- ================================================================

CREATE TABLE IF NOT EXISTS datasets_procesados (
    id                  BINARY(16) NOT NULL,
    proyecto_id         BINARY(16) NOT NULL,
    ejecucion_id        BINARY(16) NOT NULL,
    nombre_archivo      VARCHAR(255) NOT NULL,
    ruta_archivo        VARCHAR(1000) NOT NULL,
    formato             VARCHAR(20) NOT NULL DEFAULT 'PARQUET',
    hash_archivo        CHAR(64) NULL,
    tamano_bytes        BIGINT UNSIGNED NOT NULL DEFAULT 0,
    numero_filas        BIGINT UNSIGNED NOT NULL DEFAULT 0,
    numero_columnas     INT UNSIGNED NOT NULL DEFAULT 0,
    version             INT UNSIGNED NOT NULL DEFAULT 1,
    created_at          DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    UNIQUE KEY uk_dataset_procesado_version (proyecto_id, version),
    KEY idx_procesados_ejecucion (ejecucion_id),
    CONSTRAINT fk_procesados_proyecto
        FOREIGN KEY (proyecto_id) REFERENCES proyectos (id) ON DELETE CASCADE,
    CONSTRAINT fk_procesados_ejecucion
        FOREIGN KEY (ejecucion_id) REFERENCES ejecuciones_procesamiento (id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS dataset_procesado_columnas (
    id                      BINARY(16) NOT NULL,
    dataset_procesado_id    BINARY(16) NOT NULL,
    nombre                  VARCHAR(150) NOT NULL,
    tipo_dato               VARCHAR(30) NOT NULL,
    posicion                INT UNSIGNED NOT NULL,
    estadisticas_json       JSON NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_procesado_columna_posicion (dataset_procesado_id, posicion),
    CONSTRAINT fk_procesado_columnas_dataset
        FOREIGN KEY (dataset_procesado_id) REFERENCES datasets_procesados (id) ON DELETE CASCADE
) ENGINE = InnoDB;

-- ================================================================
-- DASHBOARDS Y GRAFICOS CONFIGURABLES
-- ================================================================

CREATE TABLE IF NOT EXISTS dashboards (
    id                      BINARY(16) NOT NULL,
    proyecto_id             BINARY(16) NOT NULL,
    dataset_procesado_id    BINARY(16) NULL,
    created_by              BINARY(16) NOT NULL,
    nombre                  VARCHAR(180) NOT NULL,
    descripcion             VARCHAR(500) NULL,
    activo                  BOOLEAN NOT NULL DEFAULT TRUE,
    created_at              DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at              DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
                            ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    KEY idx_dashboards_proyecto (proyecto_id),
    CONSTRAINT fk_dashboards_proyecto
        FOREIGN KEY (proyecto_id) REFERENCES proyectos (id) ON DELETE CASCADE,
    CONSTRAINT fk_dashboards_dataset
        FOREIGN KEY (dataset_procesado_id) REFERENCES datasets_procesados (id) ON DELETE SET NULL,
    CONSTRAINT fk_dashboards_usuario
        FOREIGN KEY (created_by) REFERENCES usuarios (id)
) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS widgets_dashboard (
    id                  BINARY(16) NOT NULL,
    dashboard_id        BINARY(16) NOT NULL,
    titulo              VARCHAR(180) NOT NULL,
    tipo_widget         VARCHAR(30) NOT NULL,
    posicion_x          INT UNSIGNED NOT NULL DEFAULT 0,
    posicion_y          INT UNSIGNED NOT NULL DEFAULT 0,
    ancho               INT UNSIGNED NOT NULL DEFAULT 4,
    alto                INT UNSIGNED NOT NULL DEFAULT 3,
    orden               INT UNSIGNED NOT NULL DEFAULT 0,
    configuracion_json  JSON NOT NULL,
    created_at          DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at          DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
                        ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (id),
    KEY idx_widgets_dashboard (dashboard_id),
    CONSTRAINT fk_widgets_dashboard
        FOREIGN KEY (dashboard_id) REFERENCES dashboards (id) ON DELETE CASCADE,
    CONSTRAINT chk_widgets_tipo
        CHECK (tipo_widget IN ('KPI', 'BARRAS', 'LINEAS', 'AREA', 'TORTA', 'TABLA', 'MAPA', 'DISPERSION'))
) ENGINE = InnoDB;
```

## Ejemplo De Inserción Con UUID

Los UUID pueden generarse en Spring Boot. Para pruebas directas en MySQL:

```sql
SET @organizacion_id = UUID_TO_BIN(UUID());
SET @usuario_id = UUID_TO_BIN(UUID());
SET @proyecto_id = UUID_TO_BIN(UUID());

INSERT INTO organizaciones (id, nombre, codigo)
VALUES (@organizacion_id, 'Dexter Analytics', 'DEXTER');

INSERT INTO usuarios (
    id, organizacion_id, nombre, email, password_hash, rol
)
VALUES (
    @usuario_id,
    @organizacion_id,
    'Administrador Demo',
    'admin@crmdexter.com',
    'HASH_GENERADO_POR_SPRING_SECURITY',
    'ADMINISTRADOR'
);

INSERT INTO proyectos (
    id, organizacion_id, created_by, nombre, codigo, tipo_dato
)
VALUES (
    @proyecto_id,
    @organizacion_id,
    @usuario_id,
    'Análisis de Ventas Q1',
    'VENTAS-Q1',
    'VENTAS'
);

-- Para visualizar un UUID BINARY(16) como texto:
SELECT BIN_TO_UUID(id) AS id, nombre
FROM proyectos;
```

## Mapeo En Spring Boot

En las entidades JPA se puede declarar:

```java
@Id
@GeneratedValue
@JdbcTypeCode(SqlTypes.BINARY)
private UUID id;
```

Según la versión de Hibernate, también puede ser necesario configurar explícitamente el tipo UUID como binario. Lo importante es que todas las tablas utilicen el mismo tipo físico para las claves primarias y foráneas.

## Notas Importantes

- No uses `INT AUTO_INCREMENT` para entidades de negocio.
- No uses el email como clave primaria.
- El `hash_archivo` debe calcularse sobre el archivo recibido para detectar duplicados.
- `configuracion_json` debe validarse desde Spring Boot antes de ejecutar una transformación o widget.
- La tabla `datasets_procesados` permite conservar versiones anteriores del archivo final.
- Si más adelante necesitas guardar auditoría, agrega una tabla independiente relacionada con usuario, proyecto y entidad afectada.
