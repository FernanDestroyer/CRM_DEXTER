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
EMAILJS_SERVICE_ID=service_xxxxxxx
EMAILJS_TEMPLATE_ID=template_xxxxxxx
EMAILJS_PUBLIC_KEY=xxxxxxxxxxxxxxx
EMAILJS_PRIVATE_KEY=xxxxxxxxxxxxxxx
```

## Ejecutar

```bash
mvn spring-boot:run
```

Endpoints iniciales:

- `GET /api/health`
- `POST /api/auth/login` con `{ "email": "usuario@correo.com" }`
- `POST /api/auth/verify-otp` con `{ "email": "usuario@correo.com", "otp": "1234" }`
- `GET /api/auth/me` y `POST /api/auth/logout`
- `GET /api/admin/solicitudes` (administrador autenticado)

El JWT se entrega mediante una cookie `HttpOnly`. El OTP se almacena únicamente como hash y se envía mediante la API de EmailJS usando el servicio de correo conectado por su propietario.

## Configurar EmailJS

1. En EmailJS conecta el correo que será el remitente, por ejemplo Gmail.
2. Crea una plantilla con las variables `{{to_email}}`, `{{otp}}` y `{{expiration_minutes}}`.
3. Copia el `Service ID`, `Template ID`, `Public Key` y `Private Key` desde EmailJS.
4. Configúralos como variables de entorno del backend. La Private Key es necesaria en modo estricto.

En PowerShell:

```powershell
$env:EMAILJS_SERVICE_ID="service_xxxxxxx"
$env:EMAILJS_TEMPLATE_ID="template_xxxxxxx"
$env:EMAILJS_PUBLIC_KEY="xxxxxxxxxxxxxxx"
$env:EMAILJS_PRIVATE_KEY="xxxxxxxxxxxxxxx"
```
