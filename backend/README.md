# 优选多商户电商平台 P0 微服务后端

## 约束

- JDK：1.8
- Maven：3.8.1
- Maven 本地仓库：`D:\java\maven\repository`
- 技术栈：Spring Boot 2.7.18、Spring MVC、Spring Security、JWT、Flyway、MySQL
- 代码风格：按阿里巴巴 Java 开发规约约束命名、分层、常量、DTO/DO 后缀、统一异常和统一响应

## 模块

| 模块 | 端口 | 职责 |
|---|---:|---|
| `youxuan-common` | - | 统一响应、错误码、分页、requestId、异常处理、ID 生成 |
| `youxuan-gateway-service` | 8080 | 前端统一入口、CORS、健康检查、服务目录 |
| `youxuan-auth-service` | 8081 | 登录、JWT、RBAC、权限数据迁移 |
| `youxuan-product-service` | 8082 | 类目、商品、SKU、库存、商品审核 |
| `youxuan-order-service` | 8083 | 购物车、订单、状态机、发货、售后 |
| `youxuan-merchant-service` | 8084 | 商家入驻、店铺、员工、商家操作日志 |
| `youxuan-finance-service` | 8085 | 支付、退款、账单、结算、提现、对账 |
| `youxuan-admin-service` | 8086 | 审核队列、异常池、平台介入、审计查询 |

## 中间件

中间件服务端版本以本地 Docker 镜像为准。当前代码使用 Spring Boot 2.7.18 兼容客户端接入，并通过环境变量连接本地 Docker。

| 中间件 | 默认地址 | 用途 | 相关模块 |
|---|---|---|---|
| MySQL | `127.0.0.1:3306` | 业务库、认证权限库、Flyway 迁移 | `auth`，后续业务服务 |
| Redis | `127.0.0.1:6379` | 登录态辅助、验证码、幂等锁、热点商品/类目缓存 | `auth`、`product`、`order`、`finance` |
| RabbitMQ | `127.0.0.1:5672` | 订单超时、支付回调、库存补偿、账单生成 | `order`、`finance` |
| Nacos | `127.0.0.1:8848` | 服务注册发现、后续配置中心 | 全部微服务 |
| Sentinel | `127.0.0.1:8858` | 限流、熔断降级控制台 | 全部微服务 |
| Spring Scheduler | 服务内置 | 对账、账单、结算、订单超时扫描 | `order`、`finance` |
| ELK / Loki | 采集 `logs/*.log` | 应用日志、审计排障 | 全部微服务 |

默认 `NACOS_DISCOVERY_ENABLED=false`，这样本地 Nacos 未启动时服务仍可单独启动。确认 Docker 中 Nacos 已可用后再改为 `true`。

关键环境变量：

```powershell
$env:REDIS_HOST="127.0.0.1"
$env:REDIS_PORT="6379"
$env:RABBITMQ_HOST="127.0.0.1"
$env:RABBITMQ_PORT="5672"
$env:RABBITMQ_USERNAME="guest"
$env:RABBITMQ_PASSWORD="guest"
$env:NACOS_SERVER_ADDR="127.0.0.1:8848"
$env:NACOS_DISCOVERY_ENABLED="false"
$env:SENTINEL_DASHBOARD="127.0.0.1:8858"
$env:LOG_PATH="logs"
```

## 构建

```powershell
cd D:\java\ai\youxuanSystem\backend
mvn "-Dmaven.repo.local=D:\java\maven\repository" "-DskipTests" clean package
```

## 启动网关

```powershell
java -jar .\youxuan-gateway-service\target\youxuan-gateway-service-0.1.0-SNAPSHOT.jar
```

健康检查：

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/v1/health" -Headers @{ "X-Request-Id" = "local-health-check" }
```

预期返回：

```json
{
  "code": "SUCCESS",
  "message": "success",
  "requestId": "local-health-check",
  "data": {
    "status": "UP",
    "serviceName": "youxuan-gateway-service",
    "apiPrefix": "/api/v1",
    "architecture": "microservices"
  }
}
```

## 前端联通

前端配置位于 `frontend/.env.example`：

```text
VITE_API_BASE_URL=http://localhost:8080/api/v1
```

前端页面顶部会调用 `GET /api/v1/health` 显示后端连接状态。当前已完成网关健康接口联通验证。

## 数据库迁移

认证权限服务包含 Flyway：

```text
youxuan-auth-service/src/main/resources/db/migration
```

首次启动 `youxuan-auth-service` 前，需要 Docker MySQL 中存在数据库：

```sql
CREATE DATABASE IF NOT EXISTS youxuan_p0 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
CREATE USER IF NOT EXISTS 'youxuan'@'%' IDENTIFIED BY 'youxuan_password';
GRANT ALL PRIVILEGES ON youxuan_p0.* TO 'youxuan'@'%';
FLUSH PRIVILEGES;
```

## 下一步

当前阶段完成微服务工程、公共规约、网关联通、认证服务骨架和业务服务边界。下一步应按 P0 需求逐个实现：

1. 商品服务：分类、商品列表、商品详情、SKU、库存查询。
2. 订单服务：购物车、创建订单、订单状态机、发货、售后。
3. 资金服务：支付单、退款单、账单、结算、提现、对账。
4. 平台后台：审核队列、异常池、操作日志查询。
