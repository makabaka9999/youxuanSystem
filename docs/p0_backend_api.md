# 多商户电商平台 P0 后端 API 接口文档

## 1. 文档说明

本文档基于 P0 版本产品需求与 `database/p0_schema.sql` 数据库设计编写，覆盖用户端、商家端、平台后台、支付回调、售后、结算、提现、对账与异常处理接口。

P0 API 目标：

- 支持真实上线的交易闭环。
- 所有写接口具备 `requestId`，关键写接口具备幂等能力。
- 用户、商家、平台后台权限边界清晰。
- 资金、订单、售后、结算、提现、异常处理均可审计。

## 2. 通用接口规范

### 2.1 基础约定

| 项目 | 规范 |
|---|---|
| 协议 | HTTPS |
| 数据格式 | JSON |
| 字符集 | UTF-8 |
| API 前缀 | `/api/v1` |
| 时间格式 | `yyyy-MM-dd HH:mm:ss.SSS` |
| 金额格式 | 字符串或数字均可接收，服务端统一按 `DECIMAL(18,2)` 处理，响应建议返回字符串 |
| ID 格式 | BIGINT，响应建议返回字符串，避免前端精度丢失 |
| 认证方式 | `Authorization: Bearer <access_token>` |
| 请求追踪 | 所有请求建议携带 `X-Request-Id`，所有写接口必须携带 |

### 2.2 请求头

| Header | 必填 | 说明 |
|---|---:|---|
| Authorization | 登录接口否，其余是 | Bearer Token |
| X-Request-Id | 写接口是 | 全链路请求 ID，建议 UUID |
| X-Idempotency-Key | 关键写接口是 | 幂等 Key，下单、支付回调、退款、提现必填 |
| X-Timestamp | 外部回调是 | 毫秒时间戳 |
| X-Signature | 外部回调是 | 签名 |
| Content-Type | 是 | `application/json` |

### 2.3 统一响应格式

成功响应：

```json
{
  "code": "SUCCESS",
  "message": "success",
  "requestId": "7a0b7d0e7b7d4d5b9a1a1c1b9f000001",
  "data": {}
}
```

失败响应：

```json
{
  "code": "ORDER_STOCK_NOT_ENOUGH",
  "message": "商品库存不足",
  "requestId": "7a0b7d0e7b7d4d5b9a1a1c1b9f000001",
  "data": null
}
```

### 2.4 分页规范

请求参数：

| 参数 | 类型 | 必填 | 说明 |
|---|---|---:|---|
| pageNo | integer | 否 | 默认 1，最小 1 |
| pageSize | integer | 否 | 默认 20，最大 100 |
| sortField | string | 否 | 排序字段，只允许白名单字段 |
| sortOrder | string | 否 | `asc` 或 `desc`，默认 `desc` |

分页响应：

```json
{
  "code": "SUCCESS",
  "message": "success",
  "requestId": "req-001",
  "data": {
    "pageNo": 1,
    "pageSize": 20,
    "total": 128,
    "list": []
  }
}
```

### 2.5 HTTP 状态码规范

| HTTP 状态码 | 使用场景 |
|---:|---|
| 200 | 业务处理成功或业务可预期失败，具体看业务 `code` |
| 201 | 创建资源成功，可选使用 |
| 400 | 参数格式错误、JSON 不合法 |
| 401 | 未登录或 Token 失效 |
| 403 | 无权限访问资源或操作 |
| 404 | 资源不存在 |
| 409 | 幂等冲突、状态冲突、重复提交 |
| 429 | 限流 |
| 500 | 服务端未知异常 |
| 503 | 服务暂不可用 |

### 2.6 业务错误码

| 错误码 | HTTP | 说明 |
|---|---:|---|
| SUCCESS | 200 | 成功 |
| PARAM_INVALID | 400 | 参数不合法 |
| AUTH_REQUIRED | 401 | 未登录 |
| TOKEN_EXPIRED | 401 | Token 过期 |
| PERMISSION_DENIED | 403 | 权限不足 |
| RESOURCE_NOT_FOUND | 404 | 资源不存在 |
| IDEMPOTENT_PROCESSING | 409 | 相同幂等请求处理中 |
| IDEMPOTENT_CONFLICT | 409 | 幂等 Key 相同但请求参数不一致 |
| STATE_CONFLICT | 409 | 当前状态不允许操作 |
| USER_DISABLED | 403 | 用户被禁用 |
| MERCHANT_NOT_APPROVED | 403 | 商家未审核通过 |
| MERCHANT_FROZEN | 403 | 商家已冻结 |
| PRODUCT_NOT_ON_SALE | 409 | 商品不可售 |
| PRODUCT_AUDIT_REQUIRED | 409 | 商品待审核或审核未通过 |
| ORDER_NOT_FOUND | 404 | 订单不存在 |
| ORDER_STOCK_NOT_ENOUGH | 409 | 库存不足 |
| ORDER_PRICE_CHANGED | 409 | 商品价格变化 |
| ORDER_PAY_AMOUNT_MISMATCH | 409 | 支付金额不一致 |
| PAYMENT_SIGNATURE_INVALID | 403 | 支付回调签名无效 |
| PAYMENT_DUPLICATED | 409 | 支付重复回调 |
| REFUND_AMOUNT_INVALID | 409 | 退款金额不合法 |
| REFUND_FAILED | 409 | 退款失败 |
| SETTLEMENT_AMOUNT_INVALID | 409 | 结算金额不合法 |
| WITHDRAW_BALANCE_NOT_ENOUGH | 409 | 可提现余额不足 |
| RECONCILE_DIFF_EXISTS | 409 | 存在未处理对账差异 |
| RATE_LIMITED | 429 | 请求过于频繁 |
| INTERNAL_ERROR | 500 | 服务端异常 |

## 3. 权限校验规范

### 3.1 角色模型

| 角色 | 说明 |
|---|---|
| USER | 普通用户 |
| MERCHANT_OWNER | 商家老板账号 |
| MERCHANT_STAFF | 商家员工账号 |
| PLATFORM_ADMIN | 平台管理员 |
| PLATFORM_FINANCE | 平台财务 |
| PLATFORM_AUDITOR | 平台审核员 |
| SYSTEM | 系统任务 |

### 3.2 权限校验原则

| 场景 | 校验规则 |
|---|---|
| 用户端 | 只能访问自己的地址、购物车、订单、售后 |
| 商家端 | 只能访问所属 `merchant_id` 下的店铺、商品、订单、售后、账单 |
| 商家员工 | 除商家归属校验外，还需校验菜单权限 |
| 平台后台 | 校验平台角色和具体动作权限 |
| 财务动作 | 结算审核、提现审核、打款状态维护只允许 `PLATFORM_FINANCE` 或更高权限 |
| 审核动作 | 商家审核、商品审核只允许 `PLATFORM_AUDITOR` 或更高权限 |
| 高风险动作 | 冻结商家、退款重试、异常关闭、提现审核必须写入 `operation_logs` |

## 4. 用户端 API

### 4.1 手机号登录

`POST /api/v1/auth/mobile-login`

权限：公开接口。

请求参数：

| 参数 | 类型 | 必填 | 说明 |
|---|---|---:|---|
| mobile | string | 是 | 手机号 |
| smsCode | string | 是 | 短信验证码 |

响应：

```json
{
  "accessToken": "token",
  "expiresIn": 7200,
  "user": {
    "id": "10001",
    "mobile": "13800000000",
    "nickname": "用户"
  }
}
```

### 4.2 获取当前用户信息

`GET /api/v1/users/me`

权限：USER。

响应字段：

| 字段 | 类型 | 说明 |
|---|---|---|
| id | string | 用户 ID |
| mobile | string | 手机号 |
| nickname | string | 昵称 |
| avatarUrl | string | 头像 |
| status | string | 用户状态 |

### 4.3 收货地址

| 接口 | 方法 | 路径 | 权限 | 说明 |
|---|---|---|---|---|
| 地址列表 | GET | `/api/v1/user-addresses` | USER | 查询当前用户地址 |
| 新增地址 | POST | `/api/v1/user-addresses` | USER | 新增地址 |
| 修改地址 | PUT | `/api/v1/user-addresses/{addressId}` | USER | 修改自己的地址 |
| 删除地址 | DELETE | `/api/v1/user-addresses/{addressId}` | USER | 软删除自己的地址 |
| 设置默认 | POST | `/api/v1/user-addresses/{addressId}/default` | USER | 设置默认地址 |

新增/修改地址请求：

```json
{
  "receiverName": "张三",
  "receiverMobile": "13800000000",
  "province": "浙江省",
  "city": "杭州市",
  "district": "西湖区",
  "detailAddress": "文三路 100 号",
  "isDefault": true
}
```

### 4.4 商品浏览

| 接口 | 方法 | 路径 | 权限 | 说明 |
|---|---|---|---|---|
| 类目树 | GET | `/api/v1/categories/tree` | 公开 | 查询启用类目 |
| 商品列表 | GET | `/api/v1/products` | 公开 | 查询可售商品 |
| 商品详情 | GET | `/api/v1/products/{productId}` | 公开 | 查询商品和 SKU |

商品列表请求参数：

| 参数 | 类型 | 必填 | 说明 |
|---|---|---:|---|
| keyword | string | 否 | 商品关键词 |
| categoryId | string | 否 | 类目 ID |
| storeId | string | 否 | 店铺 ID |
| minPrice | decimal | 否 | 最低价 |
| maxPrice | decimal | 否 | 最高价 |
| pageNo | integer | 否 | 页码 |
| pageSize | integer | 否 | 页大小 |

商品列表只返回：

- `products.audit_status = APPROVED`
- `products.sale_status = ON_SALE`
- `product_skus.status = ENABLED`
- `stores.status = ENABLED`
- `merchants.status = ENABLED`

### 4.5 购物车

| 接口 | 方法 | 路径 | 权限 | 说明 |
|---|---|---|---|---|
| 购物车列表 | GET | `/api/v1/carts` | USER | 按店铺分组 |
| 加入购物车 | POST | `/api/v1/carts/items` | USER | 新增或累加 SKU |
| 修改数量 | PUT | `/api/v1/carts/items/{cartItemId}` | USER | 修改数量 |
| 删除商品 | DELETE | `/api/v1/carts/items/{cartItemId}` | USER | 删除购物车项 |
| 勾选商品 | POST | `/api/v1/carts/items/check` | USER | 批量勾选 |

加入购物车请求：

```json
{
  "skuId": "30001",
  "quantity": 2
}
```

校验：

- 商品必须可售。
- SKU 必须启用。
- 商家和店铺必须启用。
- 数量必须大于 0，且不超过可售库存。

### 4.6 创建订单

`POST /api/v1/orders`

权限：USER。

Header：

| Header | 必填 | 说明 |
|---|---:|---|
| X-Request-Id | 是 | 请求 ID |
| X-Idempotency-Key | 是 | 幂等 Key |

请求参数：

```json
{
  "addressId": "20001",
  "items": [
    {
      "skuId": "30001",
      "quantity": 2
    }
  ],
  "remark": "请尽快发货"
}
```

响应：

```json
{
  "orders": [
    {
      "orderId": "50001",
      "orderNo": "O202605080001",
      "storeId": "90001",
      "payableAmount": "199.00",
      "orderStatus": "CREATED",
      "payStatus": "UNPAID"
    }
  ]
}
```

业务规则：

- 跨店铺商品自动拆成多个订单。
- 创建订单校验商品状态、SKU 状态、库存、价格快照、商家状态、地址归属。
- 创建成功后锁定库存。
- 重复提交同一幂等 Key 返回首次处理结果。

### 4.7 用户订单

| 接口 | 方法 | 路径 | 权限 | 说明 |
|---|---|---|---|---|
| 订单列表 | GET | `/api/v1/orders` | USER | 查询自己的订单 |
| 订单详情 | GET | `/api/v1/orders/{orderId}` | USER | 查询自己的订单详情 |
| 取消订单 | POST | `/api/v1/orders/{orderId}/cancel` | USER | 仅待支付可取消 |
| 确认收货 | POST | `/api/v1/orders/{orderId}/confirm-receipt` | USER | 已发货可确认 |

订单列表参数：

| 参数 | 类型 | 必填 | 说明 |
|---|---|---:|---|
| orderStatus | string | 否 | 订单状态 |
| payStatus | string | 否 | 支付状态 |
| startTime | string | 否 | 创建开始时间 |
| endTime | string | 否 | 创建结束时间 |
| pageNo | integer | 否 | 页码 |
| pageSize | integer | 否 | 页大小 |

取消订单请求：

```json
{
  "reason": "不想买了"
}
```

## 5. 支付 API

### 5.1 创建支付单

`POST /api/v1/payments`

权限：USER。

Header：必须包含 `X-Request-Id`、`X-Idempotency-Key`。

请求参数：

```json
{
  "orderNo": "O202605080001",
  "channel": "WECHAT"
}
```

响应：

```json
{
  "paymentNo": "P202605080001",
  "orderNo": "O202605080001",
  "payAmount": "199.00",
  "channel": "WECHAT",
  "payStatus": "PAYING",
  "payParams": {
    "prepayId": "wx-prepay-id"
  }
}
```

校验：

- 订单必须属于当前用户。
- 订单状态必须为 `CREATED`。
- 支付状态必须为 `UNPAID`。
- 支付金额必须等于订单应付金额。

### 5.2 支付回调

`POST /api/v1/payment-callbacks/{channel}`

权限：支付渠道签名校验。

Header：

| Header | 必填 | 说明 |
|---|---:|---|
| X-Request-Id | 是 | 请求 ID |
| X-Timestamp | 是 | 渠道时间戳 |
| X-Signature | 是 | 渠道签名 |
| X-Idempotency-Key | 是 | 建议使用渠道交易号 |

请求示例：

```json
{
  "paymentNo": "P202605080001",
  "orderNo": "O202605080001",
  "thirdTradeNo": "4200000000000001",
  "payAmount": "199.00",
  "payStatus": "SUCCESS",
  "paidAt": "2026-05-08 12:00:00.000"
}
```

响应：

```json
{
  "success": true
}
```

业务规则：

- 必须验签，验签失败返回 `PAYMENT_SIGNATURE_INVALID`。
- 必须幂等，重复成功回调返回成功。
- 金额不一致进入支付异常池。
- 支付成功但订单已取消，进入异常订单池，由后台触发退款或人工处理。
- 支付成功后写入支付单、订单状态、订单状态日志、账务流水。

## 6. 售后与退款 API

### 6.1 申请售后

`POST /api/v1/after-sales`

权限：USER。

Header：必须包含 `X-Request-Id`、`X-Idempotency-Key`。

请求参数：

```json
{
  "orderId": "50001",
  "orderItemId": "51001",
  "type": "REFUND_ONLY",
  "applyAmount": "99.00",
  "reason": "不想要了",
  "description": "未发货申请退款",
  "evidenceUrls": [
    "https://cdn.example.com/a.jpg"
  ]
}
```

响应：

```json
{
  "afterSaleId": "70001",
  "afterSaleNo": "AS202605080001",
  "status": "APPLYING"
}
```

校验：

- 用户只能申请自己的订单。
- 待发货支持 `REFUND_ONLY`。
- 已发货支持 `RETURN_REFUND`。
- P0 部分退款只允许按订单项维度申请。
- 申请金额不得超过订单项可退金额。

### 6.2 售后详情

`GET /api/v1/after-sales/{afterSaleId}`

权限：

- USER：只能查看自己的售后。
- MERCHANT：只能查看本商家的售后。
- PLATFORM：可查看全部。

### 6.3 用户填写退货物流

`POST /api/v1/after-sales/{afterSaleId}/return-shipment`

权限：USER。

请求参数：

```json
{
  "logisticsCompany": "顺丰速运",
  "trackingNo": "SF1234567890",
  "shippedAt": "2026-05-08 12:30:00.000"
}
```

状态要求：

- 售后类型为 `RETURN_REFUND`。
- 售后状态为 `MERCHANT_APPROVED`。

### 6.4 商家处理售后

`POST /api/v1/merchant/after-sales/{afterSaleId}/handle`

权限：MERCHANT_OWNER 或有售后处理权限的 MERCHANT_STAFF。

请求参数：

```json
{
  "action": "APPROVE",
  "approvedAmount": "99.00",
  "reason": "同意退款"
}
```

`action` 取值：

| 值 | 说明 |
|---|---|
| APPROVE | 同意 |
| REJECT | 拒绝 |
| CONFIRM_RETURN_RECEIVED | 确认收到退货 |

业务规则：

- 拒绝必须填写原因。
- 退货退款需先同意退货，再确认收货后触发退款。
- 操作写入 `operation_logs`。

### 6.5 平台介入处理

`POST /api/v1/admin/after-sales/{afterSaleId}/intervene`

权限：PLATFORM_ADMIN。

请求参数：

```json
{
  "decision": "REFUND",
  "approvedAmount": "99.00",
  "reason": "用户凭证充分，平台判定退款"
}
```

## 7. 商家端 API

### 7.1 商家入驻

`POST /api/v1/merchant-applications`

权限：USER。

请求参数：

```json
{
  "companyName": "杭州某某贸易有限公司",
  "licenseNo": "91330100MA00000000",
  "contactName": "李四",
  "contactMobile": "13900000000",
  "storeName": "优选店铺",
  "categoryId": "1001"
}
```

响应：

```json
{
  "merchantId": "80001",
  "merchantNo": "M202605080001",
  "auditStatus": "PENDING"
}
```

### 7.2 商家工作台

`GET /api/v1/merchant/dashboard`

权限：MERCHANT_OWNER 或 MERCHANT_STAFF。

响应字段：

| 字段 | 类型 | 说明 |
|---|---|---|
| pendingShipmentCount | integer | 待发货数 |
| afterSalePendingCount | integer | 待处理售后数 |
| todayOrderAmount | string | 今日订单金额 |
| availableWithdrawAmount | string | 可提现金额 |
| frozenAmount | string | 冻结金额 |

### 7.3 商家商品管理

| 接口 | 方法 | 路径 | 权限 | 说明 |
|---|---|---|---|---|
| 商品列表 | GET | `/api/v1/merchant/products` | 商品查看权限 | 本商家商品 |
| 商品详情 | GET | `/api/v1/merchant/products/{productId}` | 商品查看权限 | 本商家商品详情 |
| 创建商品 | POST | `/api/v1/merchant/products` | 商品管理权限 | 创建后待审核 |
| 修改商品 | PUT | `/api/v1/merchant/products/{productId}` | 商品管理权限 | 关键字段变更后待审核 |
| 上架商品 | POST | `/api/v1/merchant/products/{productId}/on-sale` | 商品管理权限 | 审核通过后可上架 |
| 下架商品 | POST | `/api/v1/merchant/products/{productId}/off-sale` | 商品管理权限 | 下架商品 |

创建商品请求：

```json
{
  "categoryId": "1001",
  "productName": "P0 示例商品",
  "mainImageUrl": "https://cdn.example.com/p.jpg",
  "detailHtml": "<p>商品详情</p>",
  "skus": [
    {
      "skuName": "默认规格",
      "salePrice": "99.00",
      "originalPrice": "129.00",
      "skuAttrs": {
        "规格": "默认"
      },
      "totalStock": 100
    }
  ]
}
```

### 7.4 商家订单管理

| 接口 | 方法 | 路径 | 权限 | 说明 |
|---|---|---|---|---|
| 订单列表 | GET | `/api/v1/merchant/orders` | 订单查看权限 | 本商家订单 |
| 订单详情 | GET | `/api/v1/merchant/orders/{orderId}` | 订单查看权限 | 本商家订单详情 |
| 发货 | POST | `/api/v1/merchant/orders/{orderId}/ship` | 发货权限 | 填写物流 |

发货请求：

```json
{
  "logisticsCompany": "顺丰速运",
  "trackingNo": "SF1234567890"
}
```

状态要求：

- 订单状态为 `PAID`。
- 商家未冻结，或平台允许冻结商家继续履约。
- 订单不存在售后阻断状态。

### 7.5 商家账务

| 接口 | 方法 | 路径 | 权限 | 说明 |
|---|---|---|---|---|
| 账单列表 | GET | `/api/v1/merchant/bills` | 财务查看权限 | 查询日账单 |
| 结算单列表 | GET | `/api/v1/merchant/settlements` | 财务查看权限 | 查询结算单 |
| 结算单详情 | GET | `/api/v1/merchant/settlements/{settlementId}` | 财务查看权限 | 查询结算明细 |
| 结算账户列表 | GET | `/api/v1/merchant/settlement-accounts` | 财务查看权限 | 查询收款账户 |
| 新增结算账户 | POST | `/api/v1/merchant/settlement-accounts` | 老板账号 | 新增后待审核 |
| 申请提现 | POST | `/api/v1/merchant/withdraw-orders` | 老板账号 | 发起提现 |

申请提现请求：

```json
{
  "accountId": "88001",
  "settlementId": "89001",
  "amount": "1000.00"
}
```

校验：

- 商家状态为 `ENABLED`。
- 结算账户审核通过。
- 可提现余额充足。
- 不存在阻断提现的异常账单或未处理对账差异。
- 写入 `idempotent_records` 和 `operation_logs`。

## 8. 平台后台 API

### 8.1 商家审核

| 接口 | 方法 | 路径 | 权限 | 说明 |
|---|---|---|---|---|
| 商家列表 | GET | `/api/v1/admin/merchants` | PLATFORM_AUDITOR | 查询商家 |
| 商家详情 | GET | `/api/v1/admin/merchants/{merchantId}` | PLATFORM_AUDITOR | 查看资料 |
| 审核商家 | POST | `/api/v1/admin/merchants/{merchantId}/audit` | PLATFORM_AUDITOR | 通过/驳回 |
| 冻结商家 | POST | `/api/v1/admin/merchants/{merchantId}/freeze` | PLATFORM_ADMIN | 冻结商家 |
| 解冻商家 | POST | `/api/v1/admin/merchants/{merchantId}/unfreeze` | PLATFORM_ADMIN | 解冻商家 |

审核请求：

```json
{
  "auditStatus": "APPROVED",
  "reason": "资料完整，审核通过"
}
```

冻结请求：

```json
{
  "reason": "存在售后争议，临时冻结提现和新增商品"
}
```

### 8.2 商品审核

| 接口 | 方法 | 路径 | 权限 | 说明 |
|---|---|---|---|---|
| 待审商品列表 | GET | `/api/v1/admin/products/audit-list` | PLATFORM_AUDITOR | 查询待审核商品 |
| 商品审核 | POST | `/api/v1/admin/products/{productId}/audit` | PLATFORM_AUDITOR | 通过/驳回 |

请求：

```json
{
  "auditStatus": "REJECTED",
  "reason": "商品图片不清晰"
}
```

### 8.3 平台订单与售后

| 接口 | 方法 | 路径 | 权限 | 说明 |
|---|---|---|---|---|
| 订单列表 | GET | `/api/v1/admin/orders` | PLATFORM_ADMIN | 查询全平台订单 |
| 订单详情 | GET | `/api/v1/admin/orders/{orderId}` | PLATFORM_ADMIN | 查看订单详情 |
| 售后列表 | GET | `/api/v1/admin/after-sales` | PLATFORM_ADMIN | 查询售后 |
| 售后介入 | POST | `/api/v1/admin/after-sales/{afterSaleId}/intervene` | PLATFORM_ADMIN | 平台裁定 |

### 8.4 异常池

| 接口 | 方法 | 路径 | 权限 | 说明 |
|---|---|---|---|---|
| 异常列表 | GET | `/api/v1/admin/exceptions` | PLATFORM_ADMIN | 查询异常池 |
| 异常详情 | GET | `/api/v1/admin/exceptions/{exceptionId}` | PLATFORM_ADMIN | 查看异常 |
| 标记处理中 | POST | `/api/v1/admin/exceptions/{exceptionId}/processing` | PLATFORM_ADMIN | 领取处理 |
| 关闭异常 | POST | `/api/v1/admin/exceptions/{exceptionId}/resolve` | PLATFORM_ADMIN | 填写处理结果 |

关闭异常请求：

```json
{
  "handleResult": "已核对渠道账单，补记支付流水并关闭异常"
}
```

### 8.5 结算与提现审核

| 接口 | 方法 | 路径 | 权限 | 说明 |
|---|---|---|---|---|
| 商家账单列表 | GET | `/api/v1/admin/merchant-bills` | PLATFORM_FINANCE | 查询账单 |
| 生成结算单 | POST | `/api/v1/admin/settlements/generate` | PLATFORM_FINANCE | 按周期生成 |
| 结算单列表 | GET | `/api/v1/admin/settlements` | PLATFORM_FINANCE | 查询结算单 |
| 审核结算单 | POST | `/api/v1/admin/settlements/{settlementId}/audit` | PLATFORM_FINANCE | 通过/驳回 |
| 提现列表 | GET | `/api/v1/admin/withdraw-orders` | PLATFORM_FINANCE | 查询提现 |
| 审核提现 | POST | `/api/v1/admin/withdraw-orders/{withdrawId}/audit` | PLATFORM_FINANCE | 通过/驳回 |
| 更新打款状态 | POST | `/api/v1/admin/withdraw-orders/{withdrawId}/pay-status` | PLATFORM_FINANCE | 成功/失败 |

生成结算单请求：

```json
{
  "merchantId": "80001",
  "startDate": "2026-05-01",
  "endDate": "2026-05-07"
}
```

审核结算单请求：

```json
{
  "auditStatus": "APPROVED",
  "reason": "金额核对无误"
}
```

提现审核请求：

```json
{
  "auditStatus": "APPROVED",
  "reason": "同意提现"
}
```

打款状态请求：

```json
{
  "status": "SUCCESS",
  "paidAt": "2026-05-08 16:00:00.000",
  "reason": "银行打款成功"
}
```

业务规则：

- 结算单审核前必须校验订单、支付、退款、账单金额一致。
- 存在未处理对账差异时，不允许审核通过。
- 提现审核通过后才允许进入打款状态。
- 所有动作写入 `operation_logs` 和 `account_flow_records`。

### 8.6 对账

| 接口 | 方法 | 路径 | 权限 | 说明 |
|---|---|---|---|---|
| 对账记录列表 | GET | `/api/v1/admin/reconciliations` | PLATFORM_FINANCE | 查询对账记录 |
| 上传渠道账单 | POST | `/api/v1/admin/reconciliations/channel-bills` | PLATFORM_FINANCE | 上传账单文件 |
| 发起对账 | POST | `/api/v1/admin/reconciliations/run` | PLATFORM_FINANCE | 按日期和渠道对账 |
| 处理差异 | POST | `/api/v1/admin/reconciliations/{reconcileId}/resolve` | PLATFORM_FINANCE | 填写处理结果 |

发起对账请求：

```json
{
  "channel": "WECHAT",
  "billDate": "2026-05-08",
  "bizType": "PAYMENT"
}
```

处理差异请求：

```json
{
  "handleResult": "渠道成功，平台缺单，已生成支付异常单跟进"
}
```

### 8.7 操作日志

`GET /api/v1/admin/operation-logs`

权限：PLATFORM_ADMIN。

查询参数：

| 参数 | 类型 | 必填 | 说明 |
|---|---|---:|---|
| requestId | string | 否 | 请求 ID |
| operatorType | string | 否 | 操作人类型 |
| operatorId | string | 否 | 操作人 ID |
| merchantId | string | 否 | 商家 ID |
| module | string | 否 | 模块 |
| action | string | 否 | 动作 |
| bizType | string | 否 | 业务类型 |
| bizNo | string | 否 | 业务单号 |
| startTime | string | 否 | 开始时间 |
| endTime | string | 否 | 结束时间 |
| pageNo | integer | 否 | 页码 |
| pageSize | integer | 否 | 页大小 |

## 9. 状态码与状态流转

### 9.1 订单状态

| 状态 | 说明 | 允许下一状态 |
|---|---|---|
| CREATED | 待支付 | PAID, CANCELED, CLOSED |
| PAID | 待发货 | SHIPPED, REFUNDING, CLOSED |
| SHIPPED | 待收货 | COMPLETED, REFUNDING |
| COMPLETED | 已完成 | REFUNDING |
| CANCELED | 已取消 | CLOSED |
| REFUNDING | 退款中 | COMPLETED, CLOSED |
| CLOSED | 已关闭 | 无 |

### 9.2 支付状态

| 状态 | 说明 |
|---|---|
| INIT | 已创建 |
| PAYING | 支付中 |
| SUCCESS | 支付成功 |
| FAILED | 支付失败 |
| CLOSED | 已关闭 |

### 9.3 售后状态

| 状态 | 说明 |
|---|---|
| APPLYING | 用户申请中 |
| MERCHANT_APPROVED | 商家同意 |
| MERCHANT_REJECTED | 商家拒绝 |
| USER_RETURNED | 用户已退货 |
| PLATFORM_INTERVENING | 平台介入 |
| CLOSED | 已关闭 |
| COMPLETED | 已完成 |

### 9.4 退款状态

| 状态 | 说明 |
|---|---|
| INIT | 已创建 |
| PROCESSING | 退款处理中 |
| SUCCESS | 退款成功 |
| FAILED | 退款失败 |

### 9.5 结算、提现、异常状态

| 对象 | 状态 |
|---|---|
| 结算单 | PENDING_AUDIT, APPROVED, REJECTED, PAID |
| 提现单 | PENDING_AUDIT, APPROVED, REJECTED, PAYING, SUCCESS, FAILED |
| 对账记录 | PENDING, PROCESSING, RESOLVED |
| 异常单 | PENDING, PROCESSING, RESOLVED, CLOSED |

## 10. 安全注意事项

### 10.1 认证与授权

- 所有非公开接口必须校验 Token。
- 商家接口必须同时校验登录用户、员工身份、`merchant_id` 归属和菜单权限。
- 平台后台接口必须校验平台角色与具体动作权限。
- 禁止仅依赖前端传入的 `merchantId`、`userId` 做权限判断，必须从登录态和服务端关系表推导。

### 10.2 幂等与重放防护

- 创建订单、创建支付单、支付回调、申请售后、创建退款、申请提现必须接入 `idempotent_records`。
- 幂等 Key 相同但请求摘要不同，返回 `IDEMPOTENT_CONFLICT`。
- 外部回调必须校验签名、时间戳和交易号，防止伪造和重放。

### 10.3 资金安全

- 支付金额必须等于订单应付金额。
- 退款金额不得超过可退金额。
- 结算金额必须可从订单、支付、退款、账单、结算明细追溯。
- 结算账户账号必须加密存储，接口只返回脱敏账号。
- 提现审核、打款状态维护必须写操作日志。

### 10.4 数据安全

- 手机号、收货地址、结算账户等敏感字段需要脱敏展示。
- 文件上传只允许白名单类型和大小限制。
- 商品详情 HTML 必须做 XSS 清洗。
- 后台查询导出需限制权限和频率，P0 默认不开放大批量导出。

### 10.5 风控与限流

- 登录、短信验证码、支付回调、退款、提现接口必须限流。
- 高频失败登录、异常支付回调、重复退款请求应进入风控日志。
- 商家冻结后限制新增商品、提现和关键资料修改，但已支付订单仍允许履约。

### 10.6 审计要求

- 审核、冻结、发货、售后处理、退款重试、结算审核、提现审核、打款状态维护必须写入 `operation_logs`。
- 订单状态变化必须写入 `order_status_logs`。
- 支付、退款、提现、结算、冻结、解冻必须写入 `account_flow_records`。
- 日志至少保留 180 天，账务和审计数据长期保留。

## 11. 数据库核心表结构

本文档基于 `database/p0_schema.sql` 自动生成，表名和字段与实际数据库一致。

通用字段约定：
- 所有表包含 `created_at`、`updated_at`（自动更新）、`deleted_at`（软删除）、`version`（乐观锁）
- 金额统一 `DECIMAL(18,2)`，状态统一 `VARCHAR(32)`，ID 统一 `BIGINT`

### 11.1 用户与地址

**`users` — 用户表**

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT | 主键 |
| mobile | VARCHAR(20) | 手机号，唯一索引 |
| password_hash | VARCHAR(255) | 密码哈希 |
| nickname | VARCHAR(64) | 昵称 |
| avatar_url | VARCHAR(500) | 头像 URL |
| status | VARCHAR(32) | ENABLED / DISABLED |
| last_login_at | DATETIME(3) | 最后登录时间 |
| remark | VARCHAR(500) | 备注 |

**`user_addresses` — 收货地址表**

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT | 主键 |
| user_id | BIGINT | 用户 ID，外键 |
| receiver_name | VARCHAR(64) | 收货人姓名 |
| receiver_mobile | VARCHAR(20) | 收货手机号 |
| province | VARCHAR(64) | 省 |
| city | VARCHAR(64) | 市 |
| district | VARCHAR(64) | 区县 |
| detail_address | VARCHAR(255) | 详细地址 |
| is_default | TINYINT | 是否默认地址 |
| status | VARCHAR(32) | ENABLED / DISABLED |
| remark | VARCHAR(500) | 备注 |

### 11.2 商品与类目

**`categories` — 类目表**

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT | 主键 |
| parent_id | BIGINT | 父级类目 ID，自引用外键 |
| category_name | VARCHAR(64) | 类目名称 |
| level | INT | 层级 |
| sort_no | INT | 排序 |
| status | VARCHAR(32) | ENABLED / DISABLED |
| remark | VARCHAR(500) | 备注 |

**`products` — 商品 SPU 表**

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT | 主键 |
| product_no | VARCHAR(64) | 商品编号，唯一索引 |
| merchant_id | BIGINT | 商家 ID，外键 |
| store_id | BIGINT | 店铺 ID，外键 |
| category_id | BIGINT | 类目 ID，外键 |
| product_name | VARCHAR(255) | 商品名称（FULLTEXT 索引） |
| main_image_url | VARCHAR(500) | 主图 URL |
| detail_html | MEDIUMTEXT | 商品详情 HTML（FULLTEXT 索引）|
| audit_status | VARCHAR(32) | PENDING / APPROVED / REJECTED |
| sale_status | VARCHAR(32) | ON_SALE / OFF_SALE |
| reject_reason | VARCHAR(500) | 驳回原因 |
| remark | VARCHAR(500) | 备注 |

**`product_skus` — 商品 SKU 表**

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT | 主键 |
| sku_no | VARCHAR(64) | SKU 编号，唯一索引 |
| product_id | BIGINT | 商品 ID，外键 |
| sku_name | VARCHAR(255) | SKU 名称 |
| sale_price | DECIMAL(18,2) | 售价 |
| original_price | DECIMAL(18,2) | 划线价 |
| sku_attrs | JSON | 规格属性 |
| status | VARCHAR(32) | ENABLED / DISABLED |
| remark | VARCHAR(500) | 备注 |
| 约束 | | CHECK (sale_price <= original_price) |

**`sku_inventories` — SKU 库存表**

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT | 主键 |
| sku_id | BIGINT | SKU ID，唯一索引/外键 |
| total_stock | INT | 总库存 |
| locked_stock | INT | 锁定库存（已下单未支付） |
| available_stock | INT | 可售库存 |
| status | VARCHAR(32) | ENABLED / DISABLED |
| remark | VARCHAR(500) | 备注 |
| 约束 | | CHECK (total_stock >= 0 AND locked_stock >= 0 AND available_stock >= 0) |

**`product_audit_records` — 商品审核记录表**

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT | 主键 |
| product_id | BIGINT | 商品 ID，外键 |
| merchant_id | BIGINT | 商家 ID，外键 |
| audit_status | VARCHAR(32) | APPROVED / REJECTED |
| audit_user_id | BIGINT | 审核人 ID，外键 |
| audit_reason | VARCHAR(500) | 审核意见 |
| audited_at | DATETIME(3) | 审核时间 |
| remark | VARCHAR(500) | 备注 |

### 11.3 购物车与订单

**`carts` — 购物车表**

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT | 主键 |
| user_id | BIGINT | 用户 ID，外键 |
| store_id | BIGINT | 店铺 ID，外键 |
| product_id | BIGINT | 商品 ID，外键 |
| sku_id | BIGINT | SKU ID，外键 |
| quantity | INT | 购买数量，> 0 |
| checked | TINYINT | 是否选中 |
| status | VARCHAR(32) | ENABLED / DISABLED |
| remark | VARCHAR(500) | 备注 |
| 索引 | | UNIQUE (user_id, sku_id) |

**`orders` — 订单主表**

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT | 主键 |
| order_no | VARCHAR(64) | 订单号，唯一索引 |
| user_id | BIGINT | 用户 ID，外键 |
| merchant_id | BIGINT | 商家 ID，外键 |
| store_id | BIGINT | 店铺 ID，外键 |
| order_status | VARCHAR(32) | CREATED / PAID / SHIPPED / COMPLETED / CANCELED / REFUNDING / CLOSED |
| pay_status | VARCHAR(32) | UNPAID / PAID / REFUNDED / PART_REFUNDED |
| total_amount | DECIMAL(18,2) | 商品总额 |
| freight_amount | DECIMAL(18,2) | 运费 |
| discount_amount | DECIMAL(18,2) | 优惠金额，P0 默认 0 |
| payable_amount | DECIMAL(18,2) | 应付金额 |
| paid_amount | DECIMAL(18,2) | 实付金额 |
| receiver_snapshot | JSON | 收货信息快照 |
| paid_at | DATETIME(3) | 支付时间 |
| shipped_at | DATETIME(3) | 发货时间 |
| completed_at | DATETIME(3) | 完成时间 |
| canceled_at | DATETIME(3) | 取消时间 |
| cancel_reason | VARCHAR(255) | 取消原因 |
| remark | VARCHAR(500) | 备注 |
| 索引 | | (order_status, created_at), (merchant_id, order_status, created_at) |

**`order_items` — 订单明细表**

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT | 主键 |
| order_id | BIGINT | 订单 ID，外键 |
| order_no | VARCHAR(64) | 订单号 |
| product_id | BIGINT | 商品 ID，外键 |
| sku_id | BIGINT | SKU ID，外键 |
| product_snapshot | JSON | 商品快照（名称/图片/属性/单价）|
| quantity | INT | 购买数量 |
| sale_price | DECIMAL(18,2) | 成交单价 |
| total_amount | DECIMAL(18,2) | 明细总额 |
| refund_status | VARCHAR(32) | NONE / REFUNDING / REFUNDED / PART_REFUNDED |
| refunded_amount | DECIMAL(18,2) | 已退款金额 |
| refundable_amount | DECIMAL(18,2) | 可退金额（受 CHECK 约束 ≤ refundable_amount）|
| status | VARCHAR(32) | ENABLED / DISABLED |
| remark | VARCHAR(500) | 备注 |

**`order_status_logs` — 订单状态日志表**

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT | 主键 |
| order_id | BIGINT | 订单 ID，外键 |
| order_no | VARCHAR(64) | 订单号 |
| from_status | VARCHAR(32) | 原状态 |
| to_status | VARCHAR(32) | 新状态 |
| operator_type | VARCHAR(32) | USER / MERCHANT / PLATFORM / SYSTEM |
| operator_id | BIGINT | 操作人 ID |
| reason | VARCHAR(500) | 原因 |
| request_id | VARCHAR(64) | 请求 ID |
| remark | VARCHAR(500) | 备注 |

**`order_shipments` — 订单发货物流表**

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT | 主键 |
| order_id | BIGINT | 订单 ID，唯一索引/外键 |
| order_no | VARCHAR(64) | 订单号 |
| merchant_id | BIGINT | 商家 ID，外键 |
| logistics_company | VARCHAR(64) | 物流公司 |
| tracking_no | VARCHAR(128) | 运单号 |
| shipped_by | BIGINT | 发货操作人 ID，外键 |
| shipped_at | DATETIME(3) | 发货时间 |
| status | VARCHAR(32) | SHIPPED / SIGNED |
| remark | VARCHAR(500) | 备注 |

### 11.4 支付与售后

**`payment_orders` — 支付单表**

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT | 主键 |
| payment_no | VARCHAR(64) | 支付单号，唯一索引 |
| order_id | BIGINT | 订单 ID，唯一索引/外键 |
| order_no | VARCHAR(64) | 订单号 |
| user_id | BIGINT | 用户 ID，外键 |
| channel | VARCHAR(32) | WECHAT / ALIPAY |
| pay_amount | DECIMAL(18,2) | 支付金额，> 0 |
| pay_status | VARCHAR(32) | INIT / PAYING / SUCCESS / FAILED / CLOSED |
| third_trade_no | VARCHAR(128) | 第三方交易号 |
| idempotent_key | VARCHAR(128) | 幂等 Key，唯一索引 |
| paid_at | DATETIME(3) | 支付成功时间 |
| callback_payload | JSON | 回调摘要 |
| remark | VARCHAR(500) | 备注 |
| 索引 | | (channel, pay_status) |

**`after_sales` — 售后单表**

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT | 主键 |
| after_sale_no | VARCHAR(64) | 售后单号，唯一索引 |
| order_id | BIGINT | 订单 ID，外键 |
| order_item_id | BIGINT | 订单项 ID，外键 |
| user_id | BIGINT | 用户 ID，外键 |
| merchant_id | BIGINT | 商家 ID，外键 |
| type | VARCHAR(32) | REFUND_ONLY / RETURN_REFUND |
| status | VARCHAR(32) | APPLYING / MERCHANT_APPROVED / MERCHANT_REJECTED / USER_RETURNED / PLATFORM_INTERVENING / CLOSED / COMPLETED |
| apply_amount | DECIMAL(18,2) | 申请退款金额 |
| approved_amount | DECIMAL(18,2) | 同意退款金额 |
| reason | VARCHAR(255) | 申请原因 |
| description | VARCHAR(1000) | 说明 |
| evidence_urls | JSON | 凭证图片 URL 数组 |
| merchant_reason | VARCHAR(500) | 商家处理意见 |
| platform_reason | VARCHAR(500) | 平台处理意见 |
| remark | VARCHAR(500) | 备注 |
| 约束 | | CHECK (approved_amount <= apply_amount) |

**`return_shipments` — 退货物流表**

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT | 主键 |
| after_sale_id | BIGINT | 售后单 ID，唯一索引/外键 |
| logistics_company | VARCHAR(64) | 物流公司 |
| tracking_no | VARCHAR(128) | 运单号 |
| shipped_at | DATETIME(3) | 用户退货时间 |
| received_at | DATETIME(3) | 商家收货时间 |
| status | VARCHAR(32) | SHIPPED / RECEIVED |
| remark | VARCHAR(500) | 备注 |

**`refund_orders` — 退款单表**

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT | 主键 |
| refund_no | VARCHAR(64) | 退款单号，唯一索引 |
| payment_id | BIGINT | 支付单 ID，外键 |
| payment_no | VARCHAR(64) | 支付单号 |
| order_id | BIGINT | 订单 ID，外键 |
| after_sale_id | BIGINT | 售后单 ID，外键 |
| refund_amount | DECIMAL(18,2) | 退款金额，> 0 |
| refund_status | VARCHAR(32) | INIT / PROCESSING / SUCCESS / FAILED |
| channel | VARCHAR(32) | 退款渠道 |
| third_refund_no | VARCHAR(128) | 第三方退款号 |
| idempotent_key | VARCHAR(128) | 幂等 Key，唯一索引 |
| fail_reason | VARCHAR(500) | 失败原因 |
| refunded_at | DATETIME(3) | 退款成功时间 |
| remark | VARCHAR(500) | 备注 |

### 11.5 商家与店铺

**`merchants` — 商家主体表**

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT | 主键 |
| merchant_no | VARCHAR(64) | 商家编号，唯一索引 |
| owner_user_id | BIGINT | 老板用户 ID，外键 |
| company_name | VARCHAR(128) | 主体名称 |
| license_no | VARCHAR(64) | 营业执照号，唯一索引 |
| contact_name | VARCHAR(64) | 联系人 |
| contact_mobile | VARCHAR(20) | 联系电话 |
| audit_status | VARCHAR(32) | PENDING / APPROVED / REJECTED |
| status | VARCHAR(32) | ENABLED / FROZEN / DISABLED |
| reject_reason | VARCHAR(500) | 驳回原因 |
| approved_at | DATETIME(3) | 审核通过时间 |
| remark | VARCHAR(500) | 备注 |

**`stores` — 店铺表**

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT | 主键 |
| merchant_id | BIGINT | 商家 ID，唯一索引/外键 |
| store_no | VARCHAR(64) | 店铺编号，唯一索引 |
| store_name | VARCHAR(128) | 店铺名称 |
| logo_url | VARCHAR(500) | Logo URL |
| contact_mobile | VARCHAR(20) | 店铺联系电话 |
| category_id | BIGINT | 主营类目 ID，外键 |
| status | VARCHAR(32) | ENABLED / FROZEN / CLOSED / DISABLED |
| remark | VARCHAR(500) | 备注 |

**`merchant_staffs` — 商家员工表**

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT | 主键 |
| merchant_id | BIGINT | 商家 ID，外键 |
| user_id | BIGINT | 用户 ID，外键 |
| staff_name | VARCHAR(64) | 员工姓名 |
| role_type | VARCHAR(32) | OWNER / STAFF |
| menu_permissions | JSON | 菜单权限编码列表 |
| status | VARCHAR(32) | ENABLED / DISABLED |
| last_login_at | DATETIME(3) | 最后登录时间 |
| remark | VARCHAR(500) | 备注 |
| 索引 | | UNIQUE (merchant_id, user_id) |

**`merchant_settlement_accounts` — 商家结算账户表**

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT | 主键 |
| merchant_id | BIGINT | 商家 ID，外键 |
| account_type | VARCHAR(32) | BANK_CARD / ALIPAY / WECHAT |
| account_name | VARCHAR(128) | 收款户名 |
| account_no_encrypted | VARCHAR(512) | 加密账号 |
| account_no_masked | VARCHAR(64) | 脱敏账号 |
| bank_name | VARCHAR(128) | 银行名称 |
| audit_status | VARCHAR(32) | PENDING / APPROVED / REJECTED |
| status | VARCHAR(32) | ENABLED / DISABLED |
| remark | VARCHAR(500) | 备注 |

### 11.6 账单与结算

**`merchant_bills` — 商家日账单表**

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT | 主键 |
| merchant_id | BIGINT | 商家 ID，外键 |
| bill_date | DATE | 账单日期 |
| order_amount | DECIMAL(18,2) | 订单收入 |
| freight_amount | DECIMAL(18,2) | 运费 |
| refund_amount | DECIMAL(18,2) | 退款金额 |
| commission_amount | DECIMAL(18,2) | 平台佣金 |
| adjustment_amount | DECIMAL(18,2) | 调整金额 |
| payable_amount | DECIMAL(18,2) | 应结金额 |
| status | VARCHAR(32) | GENERATED / CONFIRMED / SETTLED |
| remark | VARCHAR(500) | 备注 |
| 索引 | | UNIQUE (merchant_id, bill_date) |

**`settlement_orders` — 结算单表**

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT | 主键 |
| settlement_no | VARCHAR(64) | 结算单号，唯一索引 |
| merchant_id | BIGINT | 商家 ID，外键 |
| start_date | DATE | 结算开始日 |
| end_date | DATE | 结算结束日 |
| order_amount | DECIMAL(18,2) | 订单金额 |
| refund_amount | DECIMAL(18,2) | 退款冲账 |
| commission_amount | DECIMAL(18,2) | 平台佣金 |
| frozen_amount | DECIMAL(18,2) | 冻结金额 |
| payable_amount | DECIMAL(18,2) | 应付金额 |
| status | VARCHAR(32) | PENDING_AUDIT / APPROVED / REJECTED / PAID |
| audit_user_id | BIGINT | 审核人 ID，外键 |
| audit_reason | VARCHAR(500) | 审核意见 |
| approved_at | DATETIME(3) | 审核时间 |
| remark | VARCHAR(500) | 备注 |
| 索引 | | UNIQUE (merchant_id, start_date, end_date) |

**`settlement_order_items` — 结算明细表**

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT | 主键 |
| settlement_id | BIGINT | 结算单 ID，外键 |
| merchant_id | BIGINT | 商家 ID，外键 |
| order_id | BIGINT | 订单 ID，外键 |
| order_item_id | BIGINT | 订单项 ID，外键 |
| bill_id | BIGINT | 商家账单 ID，外键 |
| payable_amount | DECIMAL(18,2) | 明细应结金额 |
| commission_amount | DECIMAL(18,2) | 明细佣金 |
| refund_amount | DECIMAL(18,2) | 明细退款 |
| status | VARCHAR(32) | ENABLED / DISABLED |
| remark | VARCHAR(500) | 备注 |
| 索引 | | UNIQUE (settlement_id, order_item_id) |

**`withdraw_orders` — 提现单表**

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT | 主键 |
| withdraw_no | VARCHAR(64) | 提现单号，唯一索引 |
| merchant_id | BIGINT | 商家 ID，外键 |
| account_id | BIGINT | 结算账户 ID，外键 |
| settlement_id | BIGINT | 结算单 ID，外键 |
| amount | DECIMAL(18,2) | 提现金额，> 0 |
| status | VARCHAR(32) | PENDING_AUDIT / APPROVED / REJECTED / PAYING / SUCCESS / FAILED |
| audit_user_id | BIGINT | 审核人 ID，外键 |
| audit_reason | VARCHAR(500) | 审核意见 |
| fail_reason | VARCHAR(500) | 打款失败原因 |
| paid_at | DATETIME(3) | 打款成功时间 |
| idempotent_key | VARCHAR(128) | 幂等 Key，唯一索引 |
| remark | VARCHAR(500) | 备注 |

### 11.7 审计与异常

**`operation_logs` — 操作日志表**

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT | 主键 |
| request_id | VARCHAR(64) | 请求 ID |
| operator_type | VARCHAR(32) | USER / MERCHANT / PLATFORM / SYSTEM |
| operator_id | BIGINT | 操作人 ID |
| operator_name | VARCHAR(64) | 操作人名称 |
| merchant_id | BIGINT | 商家 ID，外键 |
| module_code | VARCHAR(64) | 模块编码 |
| action_code | VARCHAR(64) | 动作编码 |
| target_type | VARCHAR(64) | 对象类型 |
| target_id | VARCHAR(64) | 对象 ID |
| biz_type | VARCHAR(64) | 业务类型 |
| biz_no | VARCHAR(64) | 业务单号 |
| before_snapshot | JSON | 操作前状态 |
| after_snapshot | JSON | 操作后状态 |
| request_ip | VARCHAR(64) | 请求 IP |
| user_agent | VARCHAR(500) | User-Agent |
| result | VARCHAR(32) | SUCCESS / FAILED |
| fail_reason | VARCHAR(500) | 失败原因 |
| status | VARCHAR(32) | ENABLED |
| remark | VARCHAR(500) | 备注 |
| 索引 | | (module_code, action_code, created_at), (target_type, target_id) |

**`exception_orders` — 异常单表**

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT | 主键 |
| exception_no | VARCHAR(64) | 异常单号，唯一索引 |
| exception_type | VARCHAR(32) | ORDER / PAYMENT / REFUND / STOCK / RECONCILE |
| biz_no | VARCHAR(64) | 关联业务单号 |
| order_id | BIGINT | 订单 ID，外键 |
| merchant_id | BIGINT | 商家 ID，外键 |
| severity | VARCHAR(32) | LOW / MEDIUM / HIGH |
| status | VARCHAR(32) | PENDING / PROCESSING / RESOLVED / CLOSED |
| reason | VARCHAR(500) | 异常原因 |
| suggestion | VARCHAR(500) | 建议处理动作 |
| handle_result | VARCHAR(500) | 处理结果 |
| handled_by | BIGINT | 处理人 ID，外键 |
| handled_at | DATETIME(3) | 处理时间 |
| remark | VARCHAR(500) | 备注 |

### 11.8 幂等、账务与对账

**`idempotent_records` — 幂等记录表**

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT | 主键 |
| idempotency_key | VARCHAR(128) | 幂等 Key，唯一索引 |
| request_id | VARCHAR(64) | 请求 ID |
| request_hash | VARCHAR(128) | 请求参数哈希（用于校验参数一致性）|
| biz_type | VARCHAR(64) | 业务类型 |
| biz_id | VARCHAR(64) | 业务 ID |
| status | VARCHAR(32) | PROCESSING / SUCCESS / FAILED |
| response_json | JSON | 成功响应快照 |
| expired_at | DATETIME(3) | 过期时间 |

**`account_flow_records` — 账务流水表**

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT | 主键 |
| flow_no | VARCHAR(64) | 流水号，唯一索引 |
| merchant_id | BIGINT | 商家 ID，外键 |
| biz_type | VARCHAR(32) | ORDER / REFUND / COMMISSION / SETTLEMENT / WITHDRAW / FREEZE / ADJUST |
| biz_no | VARCHAR(64) | 关联业务单号 |
| direction | VARCHAR(32) | IN / OUT / FREEZE / UNFREEZE |
| amount | DECIMAL(18,2) | 发生金额 |
| balance_before | DECIMAL(18,2) | 变动前余额 |
| balance_after | DECIMAL(18,2) | 变动后余额 |
| frozen_after | DECIMAL(18,2) | 变动后冻结金额 |
| status | VARCHAR(32) | SUCCESS / FAILED |
| occurred_at | DATETIME(3) | 发生时间 |
| remark | VARCHAR(500) | 备注 |

**`reconciliation_records` — 对账记录表**

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT | 主键 |
| reconcile_no | VARCHAR(64) | 对账编号，唯一索引 |
| channel | VARCHAR(32) | 支付渠道 |
| bill_date | DATE | 对账日期 |
| biz_type | VARCHAR(32) | PAYMENT / REFUND |
| biz_no | VARCHAR(64) | 平台业务单号 |
| third_trade_no | VARCHAR(128) | 渠道交易号 |
| platform_amount | DECIMAL(18,2) | 平台金额 |
| channel_amount | DECIMAL(18,2) | 渠道金额 |
| diff_type | VARCHAR(32) | NONE / PLATFORM_MISSING / CHANNEL_MISSING / AMOUNT_DIFF |
| handle_status | VARCHAR(32) | PENDING / PROCESSING / RESOLVED |
| handle_result | VARCHAR(500) | 处理结果 |
| remark | VARCHAR(500) | 备注 |

### 11.9 系统配置

**`system_configs` — 系统配置表**

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT | 主键 |
| config_key | VARCHAR(128) | 配置键，唯一索引 |
| config_value | VARCHAR(2000) | 配置值 |
| config_desc | VARCHAR(500) | 配置说明 |
| status | VARCHAR(32) | ENABLED / DISABLED |
| remark | VARCHAR(500) | 备注 |

### 11.10 权限与角色

**`roles` — 角色表**

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT | 主键 |
| role_code | VARCHAR(64) | 角色编码，唯一索引 |
| role_name | VARCHAR(64) | 角色名称 |
| role_scope | VARCHAR(32) | USER / MERCHANT / PLATFORM |
| status | VARCHAR(32) | ENABLED / DISABLED |

**`permissions` — 权限表**

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT | 主键 |
| permission_code | VARCHAR(128) | 权限编码，唯一索引 |
| permission_name | VARCHAR(128) | 权限名称 |
| permission_scope | VARCHAR(32) | USER / MERCHANT / PLATFORM |
| resource_type | VARCHAR(32) | API / MENU / ACTION |
| status | VARCHAR(32) | ENABLED / DISABLED |

**`role_permissions` — 角色权限关系表**

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT | 主键 |
| role_id | BIGINT | 角色 ID，外键 |
| permission_id | BIGINT | 权限 ID，外键 |
| 索引 | | UNIQUE (role_id, permission_id) |

**`user_roles` — 主体角色关系表**

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT | 主键 |
| principal_type | VARCHAR(32) | USER / MERCHANT_STAFF / PLATFORM_ADMIN |
| principal_id | BIGINT | 主体 ID |
| role_id | BIGINT | 角色 ID，外键 |
| 索引 | | UNIQUE (principal_type, principal_id, role_id) |

**`platform_admins` — 平台后台账号表**

| 字段 | 类型 | 说明 |
|---|---|---|
| id | BIGINT | 主键 |
| username | VARCHAR(64) | 登录名，唯一索引 |
| password_hash | VARCHAR(255) | 密码哈希 |
| display_name | VARCHAR(64) | 展示名 |
| mobile | VARCHAR(20) | 手机号 |
| status | VARCHAR(32) | ENABLED / DISABLED |
| last_login_at | DATETIME(3) | 最后登录时间 |
| remark | VARCHAR(500) | 备注 |

## 12. RabbitMQ 事件定义

### 12.1 事件路由规范

| 交换机 | 类型 | 说明 |
|---|---|---|
| youxuan.order.exchange | TOPIC | 订单领域事件 |
| youxuan.payment.exchange | TOPIC | 支付领域事件 |
| youxuan.after-sale.exchange | TOPIC | 售后领域事件 |
| youxuan.merchant.exchange | TOPIC | 商家领域事件 |
| youxuan.finance.exchange | TOPIC | 财务领域事件 |
| youxuan.dlx.exchange | DIRECT | 死信交换机（延迟队列）|

### 12.2 订单事件

| 事件 | Routing Key | 说明 | 消费者 |
|---|---|---|---|
| OrderCreatedEvent | order.created | 订单创建 | 库存锁定、日志记录 |
| OrderPaidEvent | order.paid | 订单支付成功 | 通知商家、写入账务流水 |
| OrderShippedEvent | order.shipped | 商家已发货 | 通知用户、超时自动确认收货计时器 |
| OrderCompletedEvent | order.completed | 订单完成 | 更新店铺评分数据 |
| OrderCanceledEvent | order.canceled | 订单取消 | 释放库存、退款处理 |
| OrderRefundingEvent | order.refunding | 退款中 | 状态流转 |
| OrderClosedEvent | order.closed | 订单关闭 | 最终状态清理 |

**OrderCreatedEvent 消息体：**
```json
{
  "eventId": "evt-uuid",
  "eventType": "OrderCreatedEvent",
  "timestamp": "2026-05-11T10:00:00.000Z",
  "data": {
    "orderId": "50001",
    "orderNo": "O202605110001",
    "userId": "10001",
    "merchantId": "80001",
    "storeId": "90001",
    "payableAmount": "199.00",
    "orderItems": [
      { "skuId": "30001", "quantity": 2 }
    ]
  }
}
```

### 12.3 支付事件

| 事件 | Routing Key | 说明 | 消费者 |
|---|---|---|---|
| PaymentSuccessEvent | payment.success | 支付成功 | 更新订单支付状态、账务流水 |
| PaymentFailedEvent | payment.failed | 支付失败 | 订单转回待支付 |
| PaymentRefundEvent | payment.refund | 退款完成 | 更新退款状态 |
| PaymentAbnormalEvent | payment.abnormal | 支付异常 | 写入异常池 |

### 12.4 延时队列（死信队列实现）

| 队列 | 延迟时间 | 用途 |
|---|---|---|
| order.cancel.delay.queue | 30 分钟 | 超时未支付自动取消 |
| order.confirm.delay.queue | 7 天 | 发货后自动确认收货 |
| after-sale.timeout.delay.queue | 3 天 | 商家售后超时自动通过 |
| after-sale.return.timeout | 10 天 | 用户退货超时自动关闭 |

注：延时队列通过 RabbitMQ 死信交换机（DLX）实现。消息先发送到带有 `x-message-ttl` 的延迟队列，TTL 到期后自动路由到实际处理队列。

### 12.5 事件可靠性保证

- 事件通过 `spring-cloud-stream` 或 `RabbitTemplate` 发送，确认模式为 `PUBLISH_CONFIRM`
- 每个事件消息设置 `deliveryMode=PERSISTENT`
- 消费端采用手动 ACK 模式
- 失败重试：最多 3 次，超过则进入死信队列，由异常池兜底

## 13. Redis 缓存策略

### 13.1 缓存维度与失效时间

| 缓存 Key 前缀 | 存储内容 | TTL | 更新时机 |
|---|---|---|---|
| `category:tree` | 类目树（JSON） | 600s | 后台类目变更后手动刷新 |
| `product:detail:{id}` | 商品详情含 SKU | 300s | 商品审核通过、SKU 变更 |
| `product:saleable:{id}` | 商品可售性状态 | 60s | 上下架、库存变化 |
| `user:info:{id}` | 用户基本信息 | 1800s | 用户资料修改 |
| `sku:stock:{id}` | SKU 可用库存 | 30s | 下单、取消订单释放 |
| `token:access:{token}` | 用户登录会话 | 等于 Token TTL | 登录写入，退出/过期删除 |
| `idempotent:lock:{key}` | 幂等 Key 处理锁 | 120s | 写入时设置，处理完成后延长 |
| `rate:limit:{key}` | 接口限流计数器 | 滑动窗口 | 每次请求更新 |

### 13.2 缓存模式

- **读模式**：Cache-Aside，先查缓存，未命中再查 DB，回写缓存
- **写模式**：更新 DB 后删除缓存（del），而非直接更新缓存，避免并发写导致数据不一致
- **库存缓存**：下单时先校验 Redis 库存，再扣减 DB 库存，最终以 DB 为准。Redis 库存用于快速拦截超卖

### 13.3 缓存穿透防护

- 查询空值也缓存（TTL 较短，30s），防止频繁穿透
- 商品详情等热点 Key 使用布隆过滤器（Bloom Filter）前置校验

### 13.4 分布式锁

- 下单锁定库存、幂等处理等场景使用 Redis 分布式锁
- 锁 Key 规范：`lock:{bizType}:{bizId}`
- 锁自动过期时间：10s，支持看门狗（Watch Dog）续期

## 14. 文件上传 API

### 14.1 获取上传凭证

`GET /api/v1/upload/token`

权限：USER / MERCHANT / PLATFORM_ADMIN。

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| bizType | string | 是 | PRODUCT_IMAGE / QUALIFICATION / EVIDENCE / AVATAR |
| fileCount | integer | 否 | 预计上传文件数，默认 1，最大 10 |

响应：
```json
{
  "uploadToken": "upt-xxx",
  "uploadUrl": "https://cdn.youxuan.com/upload",
  "expiresIn": 3600,
  "allowedExtensions": ["jpg","jpeg","png","pdf"],
  "maxFileSize": 5242880
}
```

### 14.2 文件上传

`POST /api/v1/upload`

权限：根据关联业务校验。

请求：`multipart/form-data`

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| file | File | 是 | 上传文件 |
| bizType | string | 是 | 业务类型 |
| uploadToken | string | 是 | 上传凭证 |

响应：
```json
{
  "fileId": "file-xxx",
  "fileUrl": "https://cdn.youxuan.com/images/2026/05/abc.jpg",
  "fileSize": 102400,
  "fileType": "image/jpeg"
}
```

### 14.3 文件上传规范

| 业务类型 | 格式限制 | 大小限制 | 说明 |
|---|---|---|---|
| PRODUCT_IMAGE | jpg, jpeg, png | 5MB | 商品主图/详情图 |
| QUALIFICATION | jpg, jpeg, png, pdf | 10MB | 商家资质文件 |
| EVIDENCE | jpg, jpeg, png | 5MB | 售后凭证 |
| AVATAR | jpg, jpeg, png | 2MB | 用户/商家头像 |

注意：所有上传文件必须经过内容安全校验（图片鉴黄/鉴政），不合规文件直接拒绝并记录风控日志。

## 15. 定时任务

### 15.1 定时任务列表

| 任务名称 | 调度方式 | 执行频率 | 说明 |
|---|---|---|---|
| OrderAutoCancelTask | XXL-Job / @Scheduled | 每 5 分钟 | 查询超时 30 分钟未支付订单，自动取消并释放库存 |
| OrderAutoConfirmTask | XXL-Job / @Scheduled | 每天凌晨 2:00 | 查询发货超 7 天未确认收货订单，自动确认完成 |
| SettlementGenerateTask | XXL-Job / @Scheduled | 每天凌晨 3:00 | 按账期配置生成商家结算单 |
| BillGenerateTask | XXL-Job / @Scheduled | 每天凌晨 1:00 | 生成前一日商家日账单 |
| ReconciliationTask | XXL-Job / @Scheduled | 每天凌晨 4:00 | 自动发起前一日渠道对账 |
| AfterSaleTimeoutTask | XXL-Job / @Scheduled | 每 10 分钟 | 商家售后处理超时 3 天自动通过；用户退货超时 10 天自动关闭 |
| IdempotentCleanupTask | XXL-Job / @Scheduled | 每天凌晨 5:00 | 清理已过期的幂等记录 |

### 15.2 关键任务执行逻辑

**OrderAutoCancelTask：**
```sql
UPDATE orders SET order_status = 'CANCELED', updated_at = NOW()
WHERE order_status = 'CREATED' AND pay_status = 'UNPAID'
  AND created_at < DATE_SUB(NOW(), INTERVAL 30 MINUTE)
  AND deleted_at IS NULL;
```
- 同时恢复 SKU 库存（locked_stock 扣减，available_stock 增加）
- 写入 order_status_logs

**SettlementGenerateTask：**
- 按 merchant_id 分组，对已完成的日账单生成结算单
- 结算周期：T+7（按平台配置）
- 自动跳过已有结算单的日期范围
- 生成成功后发送通知事件

## 16. 依赖服务配置

### 16.1 Nacos 配置中心

| 配置项 | 默认值 | 说明 |
|---|---|---|
| spring.cloud.nacos.discovery.server-addr | 127.0.0.1:8848 | 注册中心地址 |
| spring.cloud.nacos.discovery.namespace | public | 命名空间 |
| spring.cloud.nacos.discovery.group | DEFAULT_GROUP | 分组 |
| spring.cloud.nacos.config.server-addr | 127.0.0.1:8848 | 配置中心地址 |
| spring.cloud.nacos.config.file-extension | yml | 配置文件格式 |

### 16.2 Sentinel 限流配置

| 接口 | QPS 阈值 | 隔离策略 | 降级处理 |
|---|---|---|---|
| `POST /api/v1/auth/mobile-login` | 10 | 线程池隔离 | 返回 RATE_LIMITED |
| `POST /api/v1/payment-callbacks/**` | 50 | 信号量隔离 | 返回 429 |
| `POST /api/v1/orders` | 100 | 信号量隔离 | 返回 RATE_LIMITED |
| `POST /api/v1/after-sales` | 50 | 信号量隔离 | 返回 RATE_LIMITED |
| `POST /api/v1/merchant/withdraw-orders` | 20 | 线程池隔离 | 返回 RATE_LIMITED |
| `GET /api/v1/products/**` | 500 | 信号量隔离 | 返回兜底缓存数据 |
| `GET /api/v1/categories/tree` | 1000 | 信号量隔离 | 返回兜底缓存数据 |

### 16.3 分布式事务说明

P0 采用最终一致性方案，不引入 Seata：

| 场景 | 方案 | 说明 |
|---|---|---|
| 下单 + 锁定库存 | 本地事务 + Redis 库存预扣 + 消息补偿 | DB 扣库存失败时通过异常池补偿 |
| 支付回调 + 更新订单 | 本地事务 + 幂等表 | 幂等防重放 + 异常池兜底 |
| 退款 + 更新状态 | 本地事务 + 消息驱动 + 异常池 | 退款失败自动进入异常池 |
| 结算单生成 | 定时任务 + 幂等 | 每天执行，支持失败重跑 |
