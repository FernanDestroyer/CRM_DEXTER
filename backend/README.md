# CRM Dexter Backend

Backend inicial de CRM Dexter con Spring Boot, MySQL, JWT y OTP.

## Requisitos

- Java 17+
- Maven 3.9+
- MySQL local

La aplicación crea la base `crm_dexter` si el usuario de MySQL tiene permisos. Las variables principales son:

```text
DB_URL=jdbc:mysql://localhost:3306/crm_dexter?createDatabaseIfNotExist=true&serverTimezone=UTC
DB_USERNAME=root
DB_PASSWORD=
JWT_SECRET=una-clave-local-de-al-menos-32-caracteres
```

## Ejecutar

```bash
mvn spring-boot:run
```

Endpoints iniciales:

- `GET /api/health`
- `POST /api/auth/request-otp` con `{ "email": "usuario@correo.com" }`
- `POST /api/auth/verify-otp` con `{ "email": "usuario@correo.com", "code": "123456" }`

El OTP se almacena en memoria y se devuelve en la respuesta sólo para desarrollo local. Debe conectarse un proveedor SMTP antes de usarlo fuera del entorno local.
