# 🛠️ Real-Time Issue Tracker

> 🧪 This project is my attempt to learn Java by building a backend system using Spring Boot and related technologies.

A backend project built with **Spring Boot**, using a layered architecture (`controller`, `service`, `repository`) and integrating **Kafka**, **Redis**, **Elasticsearch**, and **JWT authentication** for a modern, scalable system.

---

## 📦 Features

- ✅ JWT-based user registration & login
- 🐛 Create, update, and comment on issues
- ⚡ Redis for caching data
- 🔍 Full-text search with Elasticsearch
- 🔁 Asynchronous processing with Kafka (Producer/Consumer)
- 🧱 Clean layered architecture (Controller → Service → Repository)

---

## 🧰 Tech Stack

- **Java 21**
- **Spring Boot 3.x**
- **Kafka** – event streaming
- **Redis** – caching
- **Elasticsearch** – search indexing
- **PostgreSQL** – main DB
- **Docker & Docker Compose** – for setup
- **JWT** – authentication

---

## 🚀 Getting Started

### 🔧 Prerequisites

- Java 21+
- Maven
- Docker & Docker Compose

### 🧱 Clone and Start

```bash
git clone https://github.com/nurmuh-alhakim18/issue-tracker.git
cd issue-tracker
docker-compose up -d
```

---

## 🗃️ Project Structure

```
src/main/java/com/alhakim/issuetracker
├── config         # Security, Kafka, Redis configurations
├── controller     # REST APIs
├── dto            # Request/response DTOs
├── entity         # JPA entities
├── exception      # Custom exceptions and handlers
├── middleware     # Filters and interceptors (e.g., JWT)
├── repository     # JPA repositories
├── service        # Business logic
└── IssuetrackerApplication.java
```

---

## 🔐 Authentication

All protected routes require:

```
Authorization: Bearer <JWT_TOKEN>
```

### Auth Endpoints

| Method | Endpoint              | Description         |
|--------|-----------------------|---------------------|
| POST   | /api/v1/auth/register | Register new user   |
| POST   | /api/v1/auth/login    | Login and get token |

---

## 🔄 Kafka Integration

- 🔸 **Producer:** Emits plain string messages (e.g., "INDEX,1")
- 🔹 **Consumer:** Listens `issue-index` to index issue into Elasticsearch

---

## ⚡ Redis Usage

- Caches frequent data like issue detail
- TTL configured in `CacheConfig.java`

---

## 🔍 Elasticsearch Usage

Supports full-text and filtered search over issues using Elasticsearch.

### 🔎 Search Endpoint

**POST** `/api/issues/search`

**Example Request Body**:

```json
{
  "query": "login bug",
  "status": "OPEN",
  "priority": "MEDIUM",
  "tags": ["bug"],
  "orderBy": "createdAt",
  "direction": "desc",
  "page": 1,
  "size": 10
}
```

**Description**:
- `query`: Full-text search on issue titles/descriptions.
- `status`: Filter by status (e.g., `OPEN`, `CLOSED`).
- `priority`: Issue priority (`LOW`, `MEDIUM`, `HIGH`).
- `tags`: Filter by tags (e.g., `Bug`).
- `orderBy`: Field to sort by (e.g., `createdAt`, `updatedAt`).
- `direction`: Sorting direction (`asc`, `desc`).
- `page` and `size`: Pagination controls.

---

## 🧪 API Endpoints

### 🔸 Admin

| Method | Endpoint                   | Description                         |
|--------|----------------------------|-------------------------------------|
| POST   | /api/v1/admin/index/issues | Bulk index issue into Elasticsearch |

### 🔸 Issues

| Method | Endpoint              | Description        |
|--------|-----------------------|--------------------|
| GET    | /api/v1/issues        | List all issues    |
| POST   | /api/v1/issues/search | Search issue       |
| POST   | /api/v1/issues        | Create a new issue |
| GET    | /api/v1/issues/{id}   | Get issue by ID    |
| PUT    | /api/v1/issues/{id}   | Update issue       |
| DELETE | /api/v1/issues/{id}   | Delete issue       |

### 🔸 Comments

| Method | Endpoint                     | Description        |
|--------|------------------------------|--------------------|
| POST   | /api/v1/issues/{id}/comments | Add comment        |
| GET    | /api/v1/issues/{id}/comments | Get all comments   |

---

## 🐳 Docker Compose Setup

```yaml
services:
  app:
    build: .
    ports:
      - "8000:8000"
    depends_on:
      - db
      - redis
  db:
    image: postgres:13
    ports:
      - "5432:5432"
    environment:
      - POSTGRES_DB=issue_tracker
      - POSTGRES_USER=admin
      - POSTGRES_PASSWORD=rahasiapol
    volumes:
      - postgres_data:/var/lib/postgresql/data
  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:7.17.28
    ports:
      - "9200:9200"
    environment:
      - discovery.type=single-node
      - ES_JAVA_OPTS=-Xms512m -Xmx512m
    volumes:
      - elastic_data:/usr/share/elasticsearch/data
    ulimits:
      memlock:
        soft: -1
        hard: -1
  kafka:
    image: apache/kafka-native
    ports:
      - "9092:9092"
    environment:
      # Configure listeners for both docker and host communication
      KAFKA_LISTENERS: CONTROLLER://localhost:9091,HOST://0.0.0.0:9092,DOCKER://0.0.0.0:9093
      KAFKA_ADVERTISED_LISTENERS: HOST://localhost:9092,DOCKER://kafka:9093
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: CONTROLLER:PLAINTEXT,DOCKER:PLAINTEXT,HOST:PLAINTEXT

      # Settings required for KRaft mode
      KAFKA_NODE_ID: 1
      KAFKA_PROCESS_ROLES: broker,controller
      KAFKA_CONTROLLER_LISTENER_NAMES: CONTROLLER
      KAFKA_CONTROLLER_QUORUM_VOTERS: 1@localhost:9091

      # Listener to use for broker-to-broker communication
      KAFKA_INTER_BROKER_LISTENER_NAME: DOCKER

      # Required for a single node cluster
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
  kafka-ui:
    image: kafbat/kafka-ui:main
    ports:
      - 8080:8080
    environment:
      DYNAMIC_CONFIG_ENABLED: "true"
      KAFKA_CLUSTERS_0_NAME: local
      KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS: kafka:9093
    depends_on:
      - kafka

volumes:
  postgres_data:
  redis_data:
  elastic_data:
```

---

## 📜 API Documentation (Swagger)

This project uses **SpringDoc OpenAPI** for automatic Swagger UI generation.

### 🔗 Access

Once the app is running, access the Swagger UI at:

```
http://localhost:8000/api/v1/swagger-ui/index.html
```

You can explore all endpoints, test them interactively, and view their schema.

---

## 📌 Future Enhancements

- WebSocket notifications
- CI/CD integration with GitHub Actions