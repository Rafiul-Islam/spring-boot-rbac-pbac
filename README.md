# Roles & Permissions API

A robust Spring Boot REST API implementing Role-Based Access Control (RBAC) and Permission-Based Access Control (PBAC) with JWT authentication.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-green)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)
![License](https://img.shields.io/badge/License-MIT-yellow)

## 🚀 Features

- **JWT Authentication** - Secure token-based authentication with access and refresh tokens
- **Role-Based Access Control (RBAC)** - Hierarchical roles (SUPER_ADMIN, ADMIN, EDITOR, MODERATOR, USER)
- **Permission-Based Access Control (PBAC)** - Granular permissions for fine-grained access control
- **User-Specific Permissions** - Override role permissions with custom user permissions
- **API Documentation** - Interactive Swagger/OpenAPI documentation
- **Database Migration** - Flyway for version-controlled database schema management
- **Custom Error Handling** - Structured JSON error responses
- **Security** - BCrypt password hashing, token blacklisting, secure headers

## 🛠 Tech Stack

- **Java 17**
- **Spring Boot 4.1.1**
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Database operations
- **MySQL 8.0** - Database
- **Flyway** - Database migrations
- **JWT (jjwt)** - Token generation and validation
- **MapStruct** - Entity-DTO mapping
- **Lombok** - Reduce boilerplate code
- **SpringDoc OpenAPI** - API documentation

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- Git

## 🔧 Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd roles_permissions
   ```

2. **Configure environment variables**
   ```bash
   cp .env.example .env
   ```
   Edit `.env` and add your values:
   ```env
   JWT_SECRET=your-secret-key-minimum-256-bits
   JWT_ACCESS_TOKEN_EXPIRATION_IN_SEC=3600
   JWT_REFRESH_TOKEN_EXPIRATION_IN_SEC=86400
   ```

3. **Configure database**
   Update `src/main/resources/application.yaml` with your MySQL credentials if different from defaults:
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/roles_permissions?createDatabaseIfNotExist=true
       username: root
       password: 1234
   ```

4. **Run database migrations**
   ```bash
   mvn flyway:migrate
   ```

5. **Build the project**
   ```bash
   mvn clean install
   ```

6. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

The API will be available at `http://localhost:8081/api`

## 📚 API Documentation

Once the application is running, access the interactive Swagger documentation:

**Swagger UI**: `http://localhost:8081/api/swagger-ui.html`

**OpenAPI JSON**: `http://localhost:8081/api/v3/api-docs`

## 👥 Roles & Permissions

### Roles Hierarchy

| Role | Description |
|------|-------------|
| SUPER_ADMIN | Full system access, all permissions |
| ADMIN | User management (no delete), full product CRUD |
| EDITOR | Full product CRUD, read user profiles |
| MODERATOR | Read/update products, read user profiles |
| USER | Read products, read own profile |

### Permissions

#### User Permissions
- `USER_CREATE` - Create new users
- `USER_READ_All` - Read all users
- `USER_READ_SINGLE_OWN` - Read own user profile
- `USER_READ_SINGLE_OTHER` - Read other user profiles
- `USER_UPDATE` - Update user details
- `USER_DELETE` - Delete users

#### Product Permissions
- `PRODUCT_CREATE` - Create products
- `PRODUCT_READ_All` - Read all products
- `PRODUCT_READ_SINGLE` - Read single product
- `PRODUCT_UPDATE` - Update products
- `PRODUCT_DELETE` - Delete products

### Default Role Permissions

| Role | USER | PRODUCT |
|------|------|---------|
| SUPER_ADMIN | All | All |
| ADMIN | Create, Read All, Read Single, Update | All |
| EDITOR | Read Single Own, Read Single Other | All |
| MODERATOR | Read Single Own, Read Single Other | Read All, Read Single, Update |
| USER | Read Single Own | Read All, Read Single |

## 🔌 API Endpoints

### Authentication

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/auth/login` | Login with email/password | No |
| POST | `/api/auth/validate` | Validate access token | No |
| GET | `/api/auth/me` | Get current user info | Yes |
| POST | `/api/auth/refresh` | Refresh access token | No (cookie) |
| POST | `/api/auth/logout` | Logout and invalidate tokens | Yes |

### Users

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/users` | Get all users | Yes |
| GET | `/api/users/{id}` | Get user by ID | Yes |
| POST | `/api/users` | Register new user | No |
| PUT | `/api/users/{id}` | Update user | Yes |
| DELETE | `/api/users/{id}` | Delete user | Yes |
| PUT | `/api/users/{id}/roles` | Update user roles | Yes |
| PUT | `/api/users/{id}/permissions` | Update user permissions | Yes |
| POST | `/api/users/{id}/change-password` | Change user password | Yes |

### Products

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/products` | Get all products | Yes |
| GET | `/api/products/{id}` | Get product by ID | Yes |
| POST | `/api/products` | Create product | Yes |
| PUT | `/api/products/{id}` | Update product | Yes |
| DELETE | `/api/products/{id}` | Delete product | Yes |

### Admin

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/admin/greetings` | Admin greeting | Yes |

## 🔐 Default Users

After running migrations, a default superadmin user is created:

- **Email**: `superadmin-1@gmail.com`
- **Password**: `Password1234*`
- **Roles**: SUPER_ADMIN, USER
- **Permissions**: All permissions

## 📊 Database Schema

```
users
├── id (PK)
├── name
├── email (unique)
└── password

roles
├── id (PK)
└── name

permissions
├── id (PK)
└── name

user_roles (junction table)
├── user_id (FK)
└── role_id (FK)

roles_permissions (junction table)
├── role_id (FK)
├── permission_id (FK)
└── isDefaultForRole

user_permissions (junction table)
├── user_id (FK)
└── permission_id (FK)

addresses
├── id (PK)
├── street
├── city
├── state
├── zip
└── user_id (FK)

products
├── id (PK)
├── name
├── description
├── price
└── quantity
```

## 🧪 Testing

Run tests with Maven:
```bash
mvn test
```

## 📝 Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| JWT_SECRET | Secret key for JWT signing | (required) |
| JWT_ACCESS_TOKEN_EXPIRATION_IN_SEC | Access token expiration in seconds | 3600 |
| JWT_REFRESH_TOKEN_EXPIRATION_IN_SEC | Refresh token expiration in seconds | 86400 |

## 🔒 Security Features

- **BCrypt Password Hashing** - Secure password storage
- **JWT Access & Refresh Tokens** - Stateless authentication
- **Token Blacklisting** - Logout invalidates tokens
- **Custom Authentication Entry Point** - Proper 401 error responses
- **Custom Access Denied Handler** - Proper 403 error responses
- **Security Headers** - X-Content-Type-Options, X-Frame-Options, X-XSS-Protection
- **CORS Configuration** - Cross-origin resource sharing support

## 📄 License

This project is licensed under the MIT License.

## 🙏 Acknowledgments

- Spring Boot team for the amazing framework
- JWT library contributors
- Open source community
