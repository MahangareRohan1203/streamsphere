# StreamSphere - Scalable Video Streaming Platform

StreamSphere is a modern, microservices-based video streaming architecture designed for high scalability and event-driven processing.

## 🏗️ System Architecture

### 🛠️ Core Microservices
- **API Gateway (Port 8080):** Central entry point using Spring Cloud Gateway. Handles JWT validation and routes requests to downstream services.
- **Auth Service (Port 8081):** Manages user authentication, JWT issuance, and secure login/registration flows.
- **User Service (Port 8082):** Handles user profile management and persistence in PostgreSQL.
- **Video Service (Port 8083):** Core service for video metadata management, upload orchestration (MinIO), and event publishing (Kafka).
- **Eureka Server (Port 8761):** Service discovery and registry for all microservices.

### 🌐 Infrastructure & Middleware
- **PostgreSQL:** Primary relational database for user and video metadata.
- **Redis:** Used for session management and caching (via Auth Service).
- **Apache Kafka:** Event bus for asynchronous video processing (Transcoding, Notification).
- **MinIO:** S3-compatible object storage for raw and processed video files.
- **Zookeeper:** Coordination service for the Kafka cluster.

## 🚀 Key Workflows

### Video Upload Flow
1. **Client Request:** User uploads video via React UI (`/upload`).
2. **Gateway Validation:** API Gateway verifies JWT and extracts `X-User-Role`.
3. **Authorization:** Video Service ensures only `ADMIN` roles can hit the `/upload` endpoint.
4. **Storage:** Video Service streams the file directly to **MinIO** (bucket: `raw-videos`).
5. **Persistence:** Video metadata is saved to PostgreSQL with status `UPLOADED`.
6. **Event Trigger:** A `VideoUploadedEvent` is published to the Kafka topic `video-events`.
7. **Downstream:** Processing services (to be implemented) consume from Kafka to transcode the video into multiple resolutions.

## 🛠️ Technical Implementation Notes

### Authentication
- Stateless JWT-based authentication.
- Gateway automatically propagates user context via `X-User-Name` and `X-User-Role` headers to downstream services.
- Tokens are stored in Redux state and LocalStorage on the frontend.

### Frontend (streamsphere-ui)
- **Framework:** React + TypeScript + Vite.
- **State Management:** Redux Toolkit & RTK Query for efficient API communication.
- **Styling:** Tailwind CSS.
- **Routing:** React Router with Role-based Protected Routes.

## 🚦 Getting Started

### Prerequisites
- Docker & Docker Compose
- Java 17+ (for local development)
- Node.js 20+

### Deployment
```bash
# Start all infrastructure and services
docker compose up -d
```

### Access Points
- **Web UI:** http://localhost:5173
- **API Gateway:** http://localhost:8080
- **MinIO Console:** http://localhost:9001
- **Eureka Dashboard:** http://localhost:8761
