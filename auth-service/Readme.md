# 🔐 Auth-Service

## 📌 Overview

This project is a **auth-service** built using Spring Boot and Spring Cloud.

It provides secure authentication and authorization using **JWT (JSON Web Tokens)** along with **Role-Based Access Control (RBAC)**.

The system is designed following **SOLID principles**, clean architecture, and production-level security practices.

---

## 🚀 Features

* ✅ JWT-based Authentication
* ✅ Role-Based Access Control (RBAC)
* ✅ API Gateway Security
* ✅ Access Token & Refresh Token Flow
* ✅ Refresh Token Rotation (Advanced Security)
* ✅ Logout with Token Invalidation
* ✅ Centralized Exception Handling (JSON responses)
* ✅ Correlation ID Logging (Request tracing)

---

## 🏗️ Architecture

```
Client → API Gateway → Auth Service
```

* **API Gateway**: Handles routing, authentication filter, and security
* **Auth Service**: Handles login, token generation, validation, and refresh logic

---

## 🔑 Authentication Flow

### 1. Login

* User sends username & password
* Server returns:

    * Access Token (short-lived)
    * Refresh Token (long-lived)

### 2. Access Protected APIs

* Access token is sent in request header:

```
Authorization: Bearer <access_token>
```

### 3. Refresh Token

* When access token expires:

    * Client sends refresh token
    * New access + refresh tokens are issued
    * Old refresh token is invalidated (Rotation)

### 4. Logout

* Refresh token is deleted from storage
* User session becomes invalid

---

## 🔄 Refresh Token Rotation

* Each refresh request generates a **new refresh token**
* Old token is invalidated immediately
* Prevents **token replay attacks**

---

## 🧰 Tech Stack

* Java 17
* Spring Boot
* Spring Security
* Spring Cloud Gateway
* Maven

---

## 📂 Project Structure

```
auth-service
 ┣ config
 ┣ controller
 ┣ service
 ┣ security
 ┣ exception
 ┣ model
 ┗ util
```

---

## 📡 API Endpoints

| Method | Endpoint | Description                             |
| ------ | -------- | --------------------------------------- |
| POST   | /login   | Authenticate user & generate tokens     |
| POST   | /refresh | Generate new tokens using refresh token |
| POST   | /logout  | Invalidate refresh token                |

---

## 🛡️ Security Implementation

* JWT-based stateless authentication
* Custom security filter for token validation
* Centralized exception handling for 401 & 403
* Role-based authorization using Spring Security

---

## 📊 Logging

* Correlation ID added per request
* Helps in tracing logs across microservices

---

## 🔮 Future Enhancements

* Redis for distributed token storage
* Rate limiting at API Gateway
* Circuit breaker using Resilience4j
* Docker containerization

---

## 🧑‍💻 How to Run

1. Clone the repository
2. Build using Maven:

```
mvn clean install
```

3. Run the application:

```
mvn spring-boot:run
```

---

## 💡 Key Highlights

* Designed with scalability in mind
* Implements real-world security practices
* Demonstrates strong backend engineering concepts

---

## 👨‍💻 Author

Rohan Mahangare
