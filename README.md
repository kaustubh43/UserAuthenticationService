# User Authentication Service

A robust Spring Boot microservice for user authentication and authorization in an e-commerce ecosystem. This service handles user registration, login, token validation, and user management with JWT-based authentication.

## 🚀 Features

- **User Registration**: Sign up new users with email, name, password, and phone number
- **User Login**: Authenticate users and generate JWT tokens
- **Token Validation**: Validate JWT tokens for secure API access
- **User Management**: Retrieve user details by ID
- **Role-Based Access Control**: Support for user roles and permissions
- **Session Management**: Track and manage user sessions
- **Service Discovery**: Integrated with Netflix Eureka for microservice architecture
- **Event-Driven**: Kafka integration for asynchronous messaging
- **Security**: Spring Security with JWT authentication

## 🛠️ Tech Stack

- **Java**: 17
- **Spring Boot**: 3.5.6
- **Spring Security**: 3.5.7
- **Spring Data JPA**: For data persistence
- **MySQL**: Production database
- **H2**: In-memory database for testing
- **JWT (JSON Web Tokens)**: 0.12.5
- **Netflix Eureka Client**: 4.3.0 (Service Discovery)
- **Apache Kafka**: 3.3.10 (Message Streaming)
- **Lombok**: For reducing boilerplate code
- **Maven**: Build and dependency management

## 📋 Prerequisites

Before running this application, ensure you have:

- **Java 17** or higher installed
- **Maven 3.6+** (or use the included Maven wrapper)
- **MySQL 8.0+** running locally
- **Eureka Server** running on port 8761 (for service discovery)
- **Apache Kafka** (optional, if using messaging features)

## ⚙️ Installation & Setup

### 1. Clone the Repository

```bash
git clone https://github.com/kaustubh43/UserAuthenticationService.git
cd UserAuthenticationService
```

### 2. Configure Database

Create a MySQL database:

```sql
CREATE DATABASE userauthenticationservice;
```

### 3. Update Configuration

Edit `src/main/resources/application.properties` with your database credentials:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/userauthenticationservice
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
```

### 4. Build the Project

Using Maven wrapper (recommended):

```bash
./mvnw clean install
```

Or using Maven:

```bash
mvn clean install
```

### 5. Run the Application

Using Maven wrapper:

```bash
./mvnw spring-boot:run
```

Or using Maven:

```bash
mvn spring-boot:run
```

The application will start on **port 9000**.

## 📡 API Endpoints

### Authentication Endpoints

#### Sign Up
```http
POST /auth/signup
Content-Type: application/json

{
  "email": "user@example.com",
  "name": "John Doe",
  "password": "securePassword123",
  "phoneNumber": "+1234567890"
}
```

**Response**: `201 CREATED`
```json
{
  "id": 1,
  "email": "user@example.com",
  "name": "John Doe",
  "phoneNumber": "+1234567890",
  "roles": []
}
```

#### Login
```http
POST /auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "securePassword123"
}
```

**Response**: `200 OK`
- Returns user details with JWT token in `Set-Cookie` header

#### Validate Token
```http
POST /auth/validate-token
Content-Type: application/json

{
  "token": "your.jwt.token.here",
  "userId": 1
}
```

**Response**: `200 OK` or `401 UNAUTHORIZED`

### User Management Endpoints

#### Get User Details
```http
GET /users/{id}
```

**Response**: `200 OK`
```json
{
  "id": 1,
  "email": "user@example.com",
  "name": "John Doe",
  "phoneNumber": "+1234567890",
  "roles": []
}
```

## 🧪 Testing

Run the test suite:

```bash
./mvnw test
```

Run specific test class:

```bash
./mvnw test -Dtest=AuthControllerTest
```

The project includes comprehensive tests for:
- Controllers (AuthController, UserController)
- Services (AuthenticationService, UserService)
- Repositories (UserRepository, SessionRepository)
- Models, Configurations, and Exception handling

## 📁 Project Structure

```
UserAuthenticationService/
├── src/
│   ├── main/
│   │   ├── java/org/ecommerce/userauthenticationservice/
│   │   │   ├── clients/          # Kafka producers/consumers
│   │   │   ├── configurations/   # Spring Security & other configs
│   │   │   ├── controllers/      # REST controllers
│   │   │   ├── dtos/             # Data Transfer Objects
│   │   │   ├── exceptions/       # Custom exceptions
│   │   │   ├── models/           # JPA entities
│   │   │   ├── repositories/     # Data repositories
│   │   │   └── services/         # Business logic
│   │   └── resources/
│   │       └── application.properties
│   └── test/                     # Unit and integration tests
├── .mvn/wrapper/                 # Maven wrapper files
├── mvnw                          # Maven wrapper script (Unix)
├── mvnw.cmd                      # Maven wrapper script (Windows)
├── pom.xml                       # Maven dependencies
└── README.md
```

## 🔐 Security

This service implements JWT-based authentication with the following security features:

- Password encryption using BCrypt
- Token-based stateless authentication
- Session management
- Role-based authorization
- CORS configuration
- Spring Security integration

## 🌐 Service Discovery

The service registers with Netflix Eureka Server running at `http://localhost:8761/eureka/` for service discovery in a microservices architecture.

## 📊 Database Schema

The application automatically creates/updates the database schema using Hibernate DDL with the following main entities:

- **User**: Stores user credentials and profile information
- **Role**: Defines user roles for authorization
- **Session**: Manages user sessions and JWT tokens

## 🐛 Troubleshooting

### Common Issues

**Port 9000 already in use:**
```bash
# Change the port in application.properties
server.port=9001
```

**MySQL connection refused:**
- Ensure MySQL is running
- Verify database credentials
- Check if the database exists

**Eureka registration fails:**
- Ensure Eureka Server is running on port 8761
- Or disable Eureka by setting: `eureka.client.enabled=false`

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is part of an e-commerce microservices ecosystem.

## 📧 Contact

**Author**: Kaustubh Ajgaonkar  
**Email**: kaustubhajgaonkar43@gmail.com  
**GitHub**: [@kaustubh43](https://github.com/kaustubh43)

## 🔄 CI/CD

This project uses GitHub Actions for continuous integration. The workflow includes:
- Building the project with Maven
- Running all tests
- Code quality checks

---

**Note**: This is a microservice designed to work within a larger e-commerce ecosystem. Ensure all dependent services (Eureka Server, Kafka) are running for full functionality.
