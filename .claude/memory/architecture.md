# 技术架构

## 后端

- **语言**: Java 8
- **框架**: Spring Boot 2.7.18 + Spring Cloud 2021.0.8 + Spring Cloud Alibaba 2021.0.5.0
- **构建**: Maven 3.8.1 (repo: D:\java\maven\repository)
- **数据库**: MySQL 8.x + Flyway 8.5.13
- **中间件**: Redis 7, RabbitMQ 3, Nacos 2.3.2, Sentinel 1.8.8, Seata 1.6.1
- **模块分布**: 网关(8080) → 认证(8081) / 商品(8082) / 订单(8083) / 商家(8084) / 财务(8085) / 后台(8086)
- **所有中间件**: 通过 Docker Desktop 运行

## 前端

- **框架**: React 18 + TypeScript 5.4 + Vite 5
- **图标**: lucide-react
- **样式**: 纯 CSS (无框架), glassmorphism + minimalism 风格
- **端口**: 5173 (Vite dev server)
- **Vite 代理**: /api/v1 → gateway(8080), /uploads → gateway(8080)

## 容器

- redis:7, mysql:8.0, nacos/nacos-server:v2.3.2, rabbitmq:3-management, seataio/seata-server:1.6.1, bladex/sentinel-dashboard:1.8.8
- MySQL root 密码: root, 数据库: youxuan_p0
