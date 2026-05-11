# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

多商户综合电商平台 P0（"优选"），目标为真实上线的多商户交易闭环。分为用户端(H5)、商家端(Web)、平台后台(Web)三个入口。

## Tech Stack

- **Backend**: Java 8, Spring Boot 2.7.18, Spring Cloud 2021.0.8, Spring Cloud Alibaba 2021.0.5.0
- **Frontend**: React 18, TypeScript 5.4, Vite 5, lucide-react
- **Database**: MySQL 8.x, Flyway 8.5.13
- **Middleware**: Redis, RabbitMQ, Nacos, Sentinel
- **Build**: Maven 3.8.1 (repo: `D:\java\maven\repository`)

## Backend Architecture

8-module Maven project under `backend/`:

| Module | Port | Responsibility |
|--------|-----:|----------------|
| youxuan-common | - | Unified response, error codes, pagination, requestId, exception handling, ID generation |
| youxuan-gateway-service | 8080 | API gateway, CORS, health check, service directory |
| youxuan-auth-service | 8081 | Login, JWT, RBAC, Flyway migrations |
| youxuan-product-service | 8082 | Category, product, SKU, inventory, product audit |
| youxuan-order-service | 8083 | Cart, order, state machine, shipping, after-sale |
| youxuan-merchant-service | 8084 | Merchant onboarding, store, staff, operation log |
| youxuan-finance-service | 8085 | Payment, refund, bill, settlement, withdrawal, reconciliation |
| youxuan-admin-service | 8086 | Audit queue, exception pool, platform intervention, audit query |

### Backend Code Conventions

- Package: `com.youxuan.xxx` (e.g. `com.youxuan.auth.controller`)
- Alibaba Java Coding Guidelines style: DO suffix for data objects, DTO suffix for transfer objects
- Unified response via `ApiResponse<T>` with `code`/`message`/`requestId`/`data`
- All write APIs require `X-Request-Id` header; critical writes require `X-Idempotency-Key`
- Global exception handler: `GlobalExceptionHandler` + `BusinessException`
- Request tracing via `RequestIdFilter` and `RequestIdHolder`

## Frontend Architecture

Single SPA under `frontend/` with three portal views:

```
frontend/src/
  main.tsx           # Entry point
  App.tsx            # Root: portal switcher, layout shell
  types.ts           # Shared TypeScript types (Order, Product, etc.)
  domain.ts          # Status mapping (order status, after-sale status, etc.)
  styles.css         # Global styles (pure CSS, no framework)
  components.tsx     # Shared UI components (LoadingScreen, Badge, etc.)
  api/
    backendClient.ts # Real backend API calls
    mockApi.ts       # Mock API for dev without backend
  pages/
    UserPortal.tsx   # User H5 portal
    MerchantPortal.tsx # Merchant web portal
    AdminPortal.tsx  # Admin web portal
```

### Frontend Conventions

- BIGINT IDs handled as strings throughout to avoid JS precision loss
- Monetary amounts handled as strings (not numbers)
- All write operations must pass `X-Request-Id`; key ones add `X-Idempotency-Key`
- Page state driven by API responses; no local inference of business state
- High-risk operations require confirmation dialog + result feedback + audit log trail

## Common Commands

### Backend

```powershell
# Build all modules
cd D:\java\ai\youxuanSystem\backend
mvn "-Dmaven.repo.local=D:\java\maven\repository" "-DskipTests" clean package

# Build a specific module
mvn "-Dmaven.repo.local=D:\java\maven\repository" -pl youxuan-auth-service -am clean package

# Run a service (e.g. gateway)
java -jar .\youxuan-gateway-service\target\youxuan-gateway-service-0.1.0-SNAPSHOT.jar

# Health check
Invoke-RestMethod -Uri "http://localhost:8080/api/v1/health" -Headers @{ "X-Request-Id" = "test" }
```

### Frontend

```powershell
# Dev server (port 5173)
cd D:\java\ai\youxuanSystem\frontend
npm run dev

# Build
npm run build
```

### Database

```sql
-- Create database for local dev
CREATE DATABASE IF NOT EXISTS youxuan_p0 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
```

## API Standards

- Prefix: `/api/v1`
- Auth: `Authorization: Bearer <access_token>`
- Request format: JSON, `Content-Type: application/json`
- Response format: `{ code, message, requestId, data }`
- Success code: `"SUCCESS"`
- Pagination: `PageResponse<T>` with `items`/`total`/`page`/`size`
- Error codes in `ErrorCode` enum with `code`/`message` pairs
- ID generation: `IdGenerator` (distributed ID)

## Database Conventions

- Engine: InnoDB, charset: utf8mb4, collation: utf8mb4_0900_ai_ci
- All tables: `deleted_at`, `created_at`, `updated_at`, `version` (optimistic locking)
- Monetary fields: `DECIMAL(18,2)`
- Status fields: `VARCHAR(32)` with string constants
- ID type: BIGINT (passed as string to frontend)
- Foreign keys used in P0, may migrate to app-level later
