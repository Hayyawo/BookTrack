# 📚 BookTrack - Book Lending Management System

![CI/CD Pipeline](https://github.com/hayyawo/booktrack/actions/workflows/ci.yml/badge.svg)
![Docker Image](https://img.shields.io/docker/v/czapija/booktrack?label=docker)
![Java Version](https://img.shields.io/badge/Java-21-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)
![License](https://img.shields.io/badge/license-MIT-green)

Modern book lending management system built with Spring Boot 3, featuring JWT authentication, role-based access control, and RESTful API.

## 🚀 Quick Start

### Using Docker (Recommended)
```bash
docker-compose up -d
```

### Using pre-built image from Docker Hub
```bash
docker run -d \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/booktrack \
  -e SPRING_DATASOURCE_USERNAME=booktrack \
  -e SPRING_DATASOURCE_PASSWORD=booktrack123 \
  -e JWT_SECRET_KEY=your-secret-key \
  -p 8080:8080 \
  TWOJ_USERNAME/booktrack:latest
```

## 🚀 Features

- **User Management**
    - User registration and authentication
    - JWT-based security
    - Role-based access control (USER, ADMIN)

- **Book Management**
    - CRUD operations for books
    - Book availability tracking
    - Search and pagination

- **Loan Management**
    - Book lending with 14-day default period
    - Maximum 3 active loans per user
    - Automatic overdue detection
    - Book return handling

- **Technical Features**
    - RESTful API with OpenAPI/Swagger documentation
    - Caching with Caffeine
    - Database migrations with Flyway
    - Comprehensive test coverage (>80%)
    - Docker support

## 🛠️ Tech Stack

- **Backend**: Java 21, Spring Boot 3.x
- **Security**: Spring Security, JWT
- **Database**: PostgreSQL
- **Caching**: Caffeine
- **Documentation**: SpringDoc OpenAPI
- **Testing**: JUnit 5, Mockito, Testcontainers
- **Build**: Maven
- **Containerization**: Docker, Docker Compose

## 📋 Prerequisites

- Java 21 or higher
- Maven 3.9+
- Docker & Docker Compose (for database)
- PostgreSQL 16 (or use Docker)

## 🚀 Quick Start

### 1. Clone the repository
```bash
git clone https://github.com/yourusername/booktrack.git
cd booktrack
```

### 2. Start PostgreSQL
```bash
docker-compose up -d
```

### 3. Run the application
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### 4. Access Swagger UI

Open your browser and navigate to:
```
http://localhost:8080/swagger-ui/index.html
```

## 📚 API Documentation

### Authentication

**Register User**
```bash
POST /api/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "Password123!",
  "firstName": "John",
  "lastName": "Doe"
}
```

**Login**
```bash
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "Password123!"
}
```

### Books

**Get Available Books**
```bash
GET /api/books/available?page=0&size=20
```

**Create Book (ADMIN only)**
```bash
POST /api/books
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "Clean Code",
  "author": "Robert C. Martin",
  "isbn": "9780132350884",
  "publisher": "Prentice Hall",
  "publishYear": 2008
}
```

### Loans

**Create Loan**
```bash
POST /api/loans
Authorization: Bearer {token}
Content-Type: application/json

{
  "userId": 1,
  "bookId": 1,
  "loanDate": "2025-01-15"
}
```

**Return Book**
```bash
PUT /api/loans/{id}/return
Authorization: Bearer {token}
```

**Get Overdue Loans (ADMIN only)**
```bash
GET /api/loans/overdue
Authorization: Bearer {admin_token}
```

## 🧪 Running Tests
```bash
# Run all tests
mvn clean test

# Run with coverage report
mvn clean test jacoco:report

# View coverage report
open target/site/jacoco/index.html
```

## 🐳 Docker Deployment

### Build Docker image
```bash
docker build -t booktrack:latest .
```

### Run with Docker Compose
```bash
docker-compose up -d
```

## 📁 Project Structure
```
src/
├── main/
│   ├── java/com/yourname/booktrack/
│   │   ├── book/           # Book domain
│   │   ├── loan/           # Loan domain
│   │   ├── user/           # User domain
│   │   ├── security/       # Security & JWT
│   │   ├── common/         # Shared utilities
│   │   └── config/         # Configuration
│   └── resources/
│       ├── application.yml
│       └── db/migration/   # Flyway migrations
└── test/
    └── java/               # Tests
```

## 🔧 Configuration

Key configuration in `application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/booktrack
    username: booktrack
    password: booktrack123

jwt:
  secret-key: your-secret-key
  expiration-ms: 86400000  # 24 hours
```

## 📊 Database Schema

- **users**: User accounts and authentication
- **books**: Book catalog
- **loans**: Lending transactions

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License.

## 👤 Author

[@hayywo](https://github.com/hayyawo)

## 🙏 Acknowledgments

- Spring Boot Team
- All contributors

---

Made with ❤️ using Spring Boot 3