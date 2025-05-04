# 🧠 EAV Store- Entit,Attribute,Value 

This project is a modular data platform based on **Spring Boot** for entity-attribute-value (EAV) management.

API's to store, **in-memory key-value DB (KVserver)** inspired by Redis. Refer https://github.com/rishyank/kvserver

## 🔧 Tech Stack

| Component | Description |
|----------|-------------|
| `Spring Boot` | Java backend providing REST APIs for managing EAV data |
| `Java KV Client` | Socket-based Java client to communicate with the KVserver |




---

## 📂 Project Structure
 ```
eav-api/
├── src/ # Spring Boot application
│ ├── Config
│ ├── controller/ # REST controllers for EAV data and KV operations
│ ├── Dtos/ # DTOs for instance and attribute data
│ ├── Execption / # logic for error handling
│ ├── schema / # EAV hashmaps
│ └── utils/
│ └── KeyValueClient.java # Java socket client for communicating with C++ server
├── resources/
│ └── application.properties # Configuration file for Spring Boot

└── README.md
 ```

 ---

## 🔌 Spring Boot API Overview

REST API is defined using OpenAPI 3.0 and includes endpoints for:

### 🔸 Entity & Attribute Management

- `POST /api/entities/create` – Create new entity
- `POST /api/entities/add-attributes` – Add attributes with types
- `GET /api/entities/definitions` – Get all attributes of an entity

### 🔸 Data Management

- `POST /api/entities/{entityName}/instances` – Create instances (EAV)
- `GET /api/entities/{entityName}/instances` – Get all instances
- `PUT /api/entities/{entityName}/instances/{id}` – Update an instance
- `DELETE /api/entities/{entityName}/instances/{id}` – Delete an instance

### 🔸 Validation Rules

- `POST /api/entities/attribute/rule` – Attach rules like LENGTH, MIN_MAX, etc.
- `GET /api/entities/validate/{entityName}` – Validate all instances

### 🔸 Key-Value Integration (Backed by C++ KVServer)

- `POST /api/set?key=K&value=V` – Store value
- `GET /get?key=K` – Retrieve value

> These endpoints connect to the custom C++ server via raw TCP socket using `KeyValueClient`.The source is in `https://github.com/rishyank/kvserver`.

---

## 🔍 API Documentation
Visit: http://localhost:8951/swagger-ui/index.html

---
## 🛠 Configuration
Change application.properties:

```
server.port=8951
kv.server = 127.0.0.1
kv.server.port = 8085
```

##  📜 License
MIT