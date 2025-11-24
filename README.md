# 💠 EasyBank Backend

Este repositorio contiene la API REST para **EasyBank**, una plataforma de banca en línea desarrollada con **Spring Boot**, **PostgreSQL** y **Docker**, y desplegada en producción mediante **Railway**.

Provee autenticación segura con JWT, control de usuarios y administración de transacciones financieras para la aplicación frontend [EasyBank Frontend](https://github.com/amgems/easybank-frontend).

---

## 🚀 Stack Tecnológico

- ☕ **Java 21**
- 🌱 **Spring Boot**
  - Spring Web
  - Spring Data JPA
  - Spring Security
  - Validación
- 🐘 **PostgreSQL**
- 🔐 **JWT** (Json Web Token)
- 🐳 **Docker**
- ☁️ **Railway** (hosting del backend)
- ⚙️ **Lombok** (para evitar boilerplate)
- 🧪 **JUnit** (para testing)

---

## 📦 Dependencias principales

```xml
<dependencies>
  <!-- Spring Boot -->
  spring-boot-starter-web
  spring-boot-starter-data-jpa
  spring-boot-starter-validation
  spring-boot-starter-security
  spring-boot-devtools

  <!-- PostgreSQL -->
  postgresql

  <!-- Seguridad JWT -->
  jjwt-api
  jjwt-impl
  jjwt-jackson

  <!-- Lombok -->
  lombok

  <!-- Testing -->
  spring-boot-starter-test
</dependencies>
```

---

## 📁 Estructura general del proyecto

```
📆 src/
├── main/
│   ├── java/com/example/easybank/
│   │   ├── config    
│   │   ├── controller/      
│   │   ├── domain/         
│   │   ├── exception/           
│   │   ├── repository/      
│   │   ├── security/
│   │   ├── service
│   │   ├── util          
│   │   └── EasyBankApplication.java # Clase principal
│   └── resources/
│       ├── application.properties
│       └── static/
└── test/
    └── java/...
```

---

## 🔐 Seguridad

La API implementa autenticación mediante **JWT**.

- Registro y login de usuarios.
- Protección de rutas según roles (ADMIN, CLIENTE).
- Middleware para validar tokens en cada petición.

---

## 🐳 Docker

La aplicación está dockerizada para facilitar su despliegue.

### 📄 `Dockerfile`

```dockerfile
FROM openjdk:21
COPY target/easybank.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

### 🧪 Comandos útiles

```bash
# Construir imagen
docker build -t easybank-backend .

# Ejecutar contenedor
docker run -p 8080:8080 easybank-backend
```

---

## ☀️ Despliegue

La API está desplegada en [**Railway**](https://railway.app/).

- Se conecta automáticamente a una base de datos PostgreSQL gestionada desde Railway.
- Variables de entorno como `DB_URL`, `DB_USER`, `DB_PASS`, `JWT_SECRET` deben estar configuradas desde el panel.

---

## ⚙️ Configuración local

1. Clona el repositorio:

```bash
git clone https://github.com/amgems/easybank-backend.git
cd easybank-backend
```

2. Crea un archivo `application.properties` en `src/main/resources/` con:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/easybank
spring.datasource.username=postgres
spring.datasource.password=tu_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

jwt.secret=una_clave_secreta_segura
```

3. Ejecuta el proyecto con:

```bash
./mvnw spring-boot:run
```

> Requiere tener PostgreSQL corriendo localmente.

---

## 🔍 Pruebas

Ejecuta los tests con:

```bash
./mvnw test
```

---

## ✨ Créditos

Desarrollado con ❤️ por el equipo **Amgems**.

