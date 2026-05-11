# 多商户电商平台 P0 前端页面与组件结构设计

## 1. 设计目标

本文档基于 `docs/p0_backend_api.md` 设计 P0 前端页面结构和组件结构，覆盖用户端 H5、商家端 Web、平台管理后台 Web。P0 前端目标是支撑真实上线的交易闭环：用户下单支付、商家履约售后、平台审核风控、财务结算提现。

前端实现原则：

- 三端工程可以共用基础组件、API SDK、认证模块和状态字典，但路由、权限、布局独立。
- 所有写操作必须生成并传递 `X-Request-Id`，关键写操作额外传递 `X-Idempotency-Key`。
- BIGINT ID 在前端统一按字符串处理，金额统一按字符串展示和提交，避免精度丢失。
- 页面状态以接口返回为准，前端只做状态展示、按钮可用性控制和二次确认，不在本地推断最终业务结果。
- 高风险操作必须有确认弹窗、结果反馈、操作日志可追溯入口。

## 2. 推荐工程结构

```text
frontend/
  apps/
    user-h5/
      src/
        pages/
        routes/
        layouts/
        stores/
    merchant-web/
      src/
        pages/
        routes/
        layouts/
        stores/
    admin-web/
      src/
        pages/
        routes/
        layouts/
        stores/
  packages/
    api-client/
      src/
        http.ts
        auth.ts
        user.ts
        product.ts
        cart.ts
        order.ts
        payment.ts
        afterSale.ts
        merchant.ts
        admin.ts
        finance.ts
        reconciliation.ts
    ui/
      src/
        components/
        feedback/
        data-display/
        data-entry/
        layout/
    domain/
      src/
        enums/
        permissions/
        formatters/
        validators/
        idempotency.ts
    config/
      src/
        env.ts
        routes.ts
        featureFlags.ts
```

## 3. 公共模块设计

### 3.1 API Client

| 模块 | 职责 | 关键逻辑 |
|---|---|---|
| `http.ts` | HTTP 基础封装 | 注入 `Authorization`、`X-Request-Id`、统一解析 `code/message/data`、处理 401/403/429/500 |
| `auth.ts` | 登录态管理 | Token 存储、刷新策略预留、退出登录、登录后角色分流 |
| `idempotency.ts` | 幂等 Key 管理 | 下单、支付、售后、提现、审核等动作按按钮点击生成一次性 Key，成功或明确失败后释放 |
| `permissions` | 权限判断 | 根据角色、菜单权限、资源归属控制路由和按钮 |
| `enums` | 状态字典 | 订单、支付、售后、退款、结算、提现、异常单状态统一映射文案与颜色 |
| `formatters` | 展示格式化 | 金额、时间、手机号脱敏、订单号、物流单号 |

### 3.2 公共 UI 组件

| 组件 | 使用位置 | 说明 |
|---|---|---|
| `AppLayout` | 三端 | 顶部栏、侧边栏、内容区、移动端安全区 |
| `AuthGuard` | 三端 | 未登录跳转登录页，权限不足展示 403 |
| `PermissionGate` | 商家端、平台后台 | 按角色和菜单权限控制按钮/区域 |
| `DataTable` | 商家端、平台后台 | 分页、排序、筛选、空状态、批量操作预留 |
| `SearchForm` | 商家端、平台后台 | 列表筛选项统一收起/展开 |
| `StatusTag` | 三端 | 根据状态字典展示颜色和文案 |
| `AmountText` | 三端 | 金额统一两位小数和币种展示 |
| `ConfirmAction` | 三端 | 高风险操作二次确认 |
| `ImageUploader` | 用户端、商家端、平台后台 | 凭证、Logo、商品图上传，限制格式和大小 |
| `Timeline` | 订单、售后、审核、异常池 | 展示状态流转和操作记录 |
| `AuditDrawer` | 平台后台 | 审核通过/拒绝、填写原因、提交结果 |
| `ExceptionDrawer` | 平台后台 | 异常详情、关联订单/支付/退款、处理动作 |

### 3.3 前端权限模型

| 端 | 登录角色 | 路由控制 | 按钮控制 |
|---|---|---|---|
| 用户端 | `USER` | 只能访问个人资料、购物车、订单、售后 | 取消订单、确认收货、申请售后按订单状态控制 |
| 商家端 | `MERCHANT_OWNER`、`MERCHANT_STAFF` | 校验商家登录态和菜单权限 | 老板账号全量，员工按菜单权限控制商品、订单、售后、财务 |
| 平台后台 | `PLATFORM_ADMIN`、`PLATFORM_FINANCE`、`PLATFORM_AUDITOR` | 校验平台角色 | 审核、冻结、结算、提现、异常处理按动作权限控制 |

前端权限只用于体验和误操作拦截，最终权限以服务端校验为准。

## 4. 用户端 H5

### 4.1 路由结构

```text
/login
/profile
/categories
/products
/products/:productId
/cart
/checkout
/payment/result
/orders
/orders/:orderId
/orders/:orderId/after-sales/apply
/after-sales/:afterSaleId
```

### 4.2 页面与组件

| 页面 | 核心组件 | 数据来源 | 交互逻辑 |
|---|---|---|---|
| 登录/注册页 | `MobileLoginForm`、`SmsCodeInput`、`AgreementCheck` | `POST /api/v1/auth/mobile-login` | 校验手机号和验证码；登录成功保存 Token，拉取当前用户信息后跳转来源页 |
| 个人资料页 | `ProfileCard`、`AddressList`、`AddressEditor` | `GET /api/v1/users/me`、地址接口 | 修改基础资料；新增/编辑/删除/设默认地址；401 跳登录 |
| 分类页 | `CategoryTree`、`CategoryProductPreview` | `GET /api/v1/categories`、商品列表接口 | 点击类目刷新商品列表；保留类目筛选参数 |
| 商品列表页 | `ProductSearchBar`、`FilterBar`、`ProductGrid`、`PaginationLoader` | `GET /api/v1/products` | 支持关键词、类目、排序；滚动加载；商品下架或售罄展示不可购买 |
| 商品详情页 | `ProductGallery`、`SkuSelector`、`PriceStockPanel`、`AddCartBar` | `GET /api/v1/products/{productId}` | 选择 SKU 后展示价格库存；加入购物车；立即购买进入确认订单 |
| 购物车页 | `StoreCartGroup`、`CartItemStepper`、`CartSettlementBar` | `GET/POST/PATCH/DELETE /api/v1/cart-items` | 按店铺分组；修改数量实时校验库存；结算时按店铺拆单进入确认页 |
| 订单确认页 | `AddressSelector`、`OrderStoreGroup`、`FreightPanel`、`SubmitOrderBar` | 地址、购物车、`POST /api/v1/orders` | 提交前校验地址和商品；生成 `X-Request-Id` 与幂等 Key；成功后跳支付 |
| 支付结果页 | `PaymentStatusResult`、`OrderEntryActions` | `POST /api/v1/payments`、订单详情 | 发起支付后轮询订单/支付状态；成功跳订单详情，失败允许重新支付或返回订单 |
| 订单列表页 | `OrderStatusTabs`、`OrderCardList`、`OrderActionBar` | `GET /api/v1/orders` | 按状态筛选；待支付可取消/支付，待收货可确认，符合规则可申请售后 |
| 订单详情页 | `OrderStatusHeader`、`ShipmentInfo`、`OrderItemList`、`PaymentSummary`、`OrderTimeline` | `GET /api/v1/orders/{orderId}` | 展示物流、金额、状态日志；根据状态展示取消、支付、确认收货、售后入口 |
| 售后申请页 | `AfterSaleTypeSelector`、`RefundItemSelector`、`ReasonForm`、`VoucherUploader` | 订单详情、`POST /api/v1/after-sales` | 待发货仅退款，已发货退货退款；P0 只按订单项部分退款；提交使用幂等 Key |
| 售后详情页 | `AfterSaleStatusHeader`、`EvidenceList`、`ReturnShipmentForm`、`RefundProgress`、`AfterSaleTimeline` | `GET /api/v1/after-sales/{id}`、退货物流接口 | 商家同意退货后展示填写物流；平台介入时展示证据链和处理记录 |

### 4.3 用户端关键交互

- 下单：购物车选择商品后进入确认页，提交订单成功返回一个或多个订单；跨店铺订单在支付前需要展示拆单结果和合计金额。
- 支付：创建支付单后跳转支付渠道或展示支付二维码；前端不直接判定支付成功，必须以订单详情/支付状态查询结果为准。
- 取消订单：仅待支付可取消；取消成功后刷新订单详情和购物车库存提示。
- 确认收货：高风险确认弹窗；成功后订单进入已完成，并提示售后冻结期。
- 售后：售后申请提交后进入售后详情；如果 `STATE_CONFLICT`，重新拉取订单和售后状态并提示用户当前不可申请。

## 5. 商家端 Web

### 5.1 路由结构

```text
/merchant/login
/merchant/onboarding
/merchant/dashboard
/merchant/store/profile
/merchant/products
/merchant/products/new
/merchant/products/:productId/edit
/merchant/orders
/merchant/orders/:orderId
/merchant/orders/:orderId/ship
/merchant/after-sales
/merchant/after-sales/:afterSaleId
/merchant/finance/bills
/merchant/finance/settlements/:settlementId
/merchant/finance/withdraw
/merchant/staffs
```

### 5.2 页面与组件

| 页面 | 核心组件 | 数据来源 | 交互逻辑 |
|---|---|---|---|
| 商家入驻页 | `MerchantApplyForm`、`QualificationUploader`、`ApplyStatusResult` | `POST /api/v1/merchant/applications` | 提交主体资质、联系人、经营类目；提交后进入审核中状态；驳回后可修改再提交 |
| 商家工作台 | `MetricCards`、`TodoList`、`RecentOrdersTable`、`FinanceSnapshot` | `GET /api/v1/merchant/dashboard` | 展示待发货、售后待处理、审核失败商品、可提现余额；点击待办进入对应列表 |
| 店铺资料页 | `StoreProfileForm`、`LogoUploader`、`CategorySelector` | 店铺详情/更新接口 | 修改店铺名称、Logo、联系方式；冻结商家禁止关键资料变更时置灰 |
| 商品列表页 | `ProductSearchForm`、`ProductTable`、`BatchActionBar` | `GET /api/v1/merchant/products` | 按上下架、审核状态筛选；支持编辑、上架、下架；审核中商品限制编辑关键销售字段 |
| 商品编辑页 | `ProductBaseForm`、`SkuEditor`、`InventoryEditor`、`ImageUploader`、`SubmitAuditBar` | 商品详情、创建/更新商品接口 | 保存草稿或提交审核；价格和库存字段校验；提交后进入平台审核 |
| 订单列表页 | `OrderSearchForm`、`MerchantOrderTable`、`OrderStatusTabs` | `GET /api/v1/merchant/orders` | 按待发货、已发货、已完成、退款中、已关闭筛选；待发货展示发货入口 |
| 订单详情页 | `OrderBuyerInfo`、`OrderItemTable`、`ShipmentPanel`、`PaymentSummary`、`OrderTimeline` | `GET /api/v1/merchant/orders/{id}` | 查看订单、物流、售后状态；已支付且待发货可进入发货页；退款中展示售后入口 |
| 发货页 | `ShipmentForm`、`LogisticsCompanySelect`、`TrackingNoInput` | `POST /api/v1/merchant/orders/{id}/ship` | 校验物流公司和单号；提交写入操作人和时间；成功返回订单详情 |
| 售后列表页 | `AfterSaleSearchForm`、`AfterSaleTable`、`StatusTabs` | `GET /api/v1/merchant/after-sales` | 筛选待处理、退货中、退款中、平台介入；待处理突出超时倒计时 |
| 售后详情页 | `AfterSaleEvidencePanel`、`MerchantDecisionForm`、`ReturnInfoPanel`、`AfterSaleTimeline` | 售后详情、`POST /api/v1/merchant/after-sales/{id}/handle` | 同意退款、同意退货退款、拒绝售后；拒绝必须填写原因；提交后刷新详情 |
| 账单列表页 | `BillSearchForm`、`BillTable`、`AmountSummary` | `GET /api/v1/merchant/bills` | 按日期、状态筛选；查看日账单金额、佣金、退款冲账、冻结金额 |
| 结算单详情页 | `SettlementSummary`、`SettlementItemTable`、`FlowRecordList` | `GET /api/v1/merchant/settlements/{id}` | 展示结算周期、明细订单、扣款项、状态；异常结算展示原因 |
| 提现申请页 | `WithdrawBalanceCard`、`WithdrawForm`、`WithdrawHistoryTable` | 结算账户、`POST /api/v1/merchant/withdraw-orders` | 提现前校验可提现余额、商家状态、异常账单；提交使用幂等 Key |
| 员工账号管理页 | `StaffTable`、`StaffEditorDrawer`、`MenuPermissionTree` | 员工列表、创建/更新员工接口 | 老板账号可增删改员工；员工权限按菜单勾选；禁用员工后强制下线预留 |

### 5.3 商家端关键交互

- 入驻审核：未审核通过时只能访问入驻状态页，不能进入商品、订单、财务模块。
- 冻结处理：商家冻结后，新增商品、提交审核、提现按钮禁用；已支付订单仍允许发货和售后处理。
- 商品审核：商品提交审核后前端展示审核中，不允许上架；驳回展示原因并允许修改后重新提交。
- 发货：发货提交必须防重复点击；若订单状态变更导致 `STATE_CONFLICT`，重新拉取订单详情。
- 售后处理：商家拒绝售后必须填写明确原因；超时倒计时由服务端返回截止时间，前端只展示和提醒。
- 财务：金额展示要区分可提现余额、冻结金额、待结算金额；提现成功不是到账成功，需展示审核和打款状态。

## 6. 平台管理后台 Web

### 6.1 路由结构

```text
/admin/login
/admin/dashboard
/admin/merchants/audits
/admin/merchants/:merchantId
/admin/products/audits
/admin/orders
/admin/after-sales/interventions
/admin/exceptions/orders
/admin/exceptions/payments
/admin/exceptions/refunds
/admin/reconciliation/differences
/admin/finance/bills
/admin/finance/settlements
/admin/finance/settlements/:settlementId/audit
/admin/finance/withdraws
/admin/logs/operations
/admin/system/config
```

### 6.2 页面与组件

| 页面 | 核心组件 | 数据来源 | 交互逻辑 |
|---|---|---|---|
| 平台首页看板 | `AdminMetricCards`、`RiskTodoPanel`、`FinanceTodoPanel`、`TrendCharts` | `GET /api/v1/admin/dashboard` | 展示待审核商家/商品、异常池数量、待结算/提现；点击卡片进入对应列表 |
| 商家审核列表 | `MerchantAuditSearchForm`、`MerchantAuditTable`、`AuditDrawer` | `GET /api/v1/admin/merchant-applications`、审核接口 | 审核通过/拒绝；拒绝必填原因；提交记录操作人、IP、前后状态 |
| 商家详情页 | `MerchantProfilePanel`、`QualificationPanel`、`StorePanel`、`MerchantRiskPanel`、`OperationTimeline` | `GET /api/v1/admin/merchants/{id}`、冻结接口 | 查看资质、店铺、订单和财务摘要；冻结/解冻需二次确认和原因 |
| 商品审核列表 | `ProductAuditSearchForm`、`ProductAuditTable`、`ProductPreviewDrawer` | 商品审核列表、审核接口 | 查看商品详情、SKU、图片；通过后可售，拒绝必须填写原因 |
| 订单管理页 | `AdminOrderSearchForm`、`AdminOrderTable`、`OrderDetailDrawer` | `GET /api/v1/admin/orders` | 按订单号、用户、商家、状态筛选；只读为主，异常订单跳异常池处理 |
| 售后介入列表 | `InterventionSearchForm`、`InterventionTable`、`EvidenceChainPanel`、`PlatformDecisionForm` | 售后介入列表、平台处理接口 | 查看用户凭证、商家说明、物流；平台裁决必须填写处理说明 |
| 异常订单池 | `ExceptionSearchForm`、`ExceptionTable`、`ExceptionDetailDrawer`、`ExceptionActionBar` | `GET /api/v1/admin/exception-orders`、处理接口 | 支付成功订单异常、库存异常、超时竞态异常；支持触发退款、标记处理、转人工 |
| 支付异常池 | `PaymentExceptionTable`、`PaymentOrderPanel`、`CallbackLogPanel` | 支付异常列表、支付单详情 | 查看支付单、渠道流水、回调报文摘要；支持重试同步或人工确认 |
| 退款异常池 | `RefundExceptionTable`、`RefundRetryDrawer` | 退款异常列表、退款重试接口 | 退款失败可重试；人工标记必须填写渠道凭证和原因 |
| 对账差异池 | `ReconciliationSearchForm`、`DiffTable`、`DiffResolveDrawer` | `GET /api/v1/admin/reconciliation-records`、处理接口 | 展示渠道金额、平台金额、差异金额；处理后关联账务流水或异常单 |
| 商家账单列表 | `MerchantBillSearchForm`、`MerchantBillTable`、`BillDetailDrawer` | 商家账单列表/详情 | 按商家、日期、状态查询；查看佣金、退款冲账、冻结金额 |
| 结算单审核页 | `SettlementSearchForm`、`SettlementTable`、`SettlementAuditDrawer` | 结算单列表、审核接口 | 财务审核结算金额；审核通过进入可提现/待打款流程；拒绝填写原因 |
| 提现审核页 | `WithdrawSearchForm`、`WithdrawTable`、`WithdrawAuditDrawer`、`PaymentProofForm` | 提现列表、审核/打款状态接口 | 审核提现申请；维护打款中、打款成功、打款失败；打款成功需填写凭证 |
| 操作日志页 | `OperationLogSearchForm`、`OperationLogTable`、`OperationDiffViewer` | `GET /api/v1/admin/operation-logs` | 按操作人、模块、动作、对象查询；查看操作前后状态差异 |
| 系统配置页 | `ConfigTabs`、`SettlementConfigForm`、`AfterSaleConfigForm`、`CommissionConfigTable` | 系统配置接口 | 配置账期 T+7、售后冻结期、佣金比例；修改需权限和操作日志 |

### 6.3 平台后台关键交互

- 审核队列：审核操作必须在抽屉或详情页完成，列表按钮只能打开审核面板，避免误操作。
- 冻结商家：冻结后明确提示影响范围：限制新增商品和提现，不影响已支付订单履约。
- 异常池：异常处理动作要有前置校验和二次确认；处理成功后状态更新并保留处理记录。
- 对账差异：差异未处理时，需要阻断对应商家提现或后续结算审核提示。
- 财务审核：结算和提现页面必须展示金额构成、关联账单、关联流水，不允许只展示一个总金额。
- 操作日志：所有审核、冻结、退款、结算、提现动作完成后可从当前页面跳转到对应日志筛选结果。

## 7. 状态与按钮控制

### 7.1 订单按钮

| 订单状态 | 用户端按钮 | 商家端按钮 | 平台后台按钮 |
|---|---|---|---|
| `PENDING_PAYMENT` | 去支付、取消订单 | 查看 | 查看 |
| `PAID` | 申请仅退款 | 发货、查看售后 | 查看、异常处理入口 |
| `SHIPPED` | 确认收货、申请退货退款 | 查看物流、查看售后 | 查看 |
| `COMPLETED` | 申请售后 | 查看 | 查看 |
| `CLOSED` | 查看 | 查看 | 查看 |
| `EXCEPTION` | 查看、联系客服提示 | 查看异常提示 | 处理异常 |

### 7.2 售后按钮

| 售后状态 | 用户端按钮 | 商家端按钮 | 平台后台按钮 |
|---|---|---|---|
| `PENDING_MERCHANT` | 查看、补充凭证 | 同意、拒绝 | 查看 |
| `MERCHANT_APPROVED_RETURN` | 填写退货物流 | 查看 | 查看 |
| `RETURNED` | 查看 | 确认收货并退款 | 查看 |
| `REFUNDING` | 查看 | 查看 | 查看退款状态 |
| `REFUNDED` | 查看 | 查看 | 查看 |
| `REJECTED` | 申请平台介入 | 查看 | 查看 |
| `PLATFORM_INTERVENING` | 补充凭证 | 补充说明 | 平台裁决 |

### 7.3 财务按钮

| 对象 | 状态 | 可用动作 |
|---|---|---|
| 商家账单 | `GENERATED` | 查看明细 |
| 结算单 | `PENDING_AUDIT` | 平台财务审核 |
| 结算单 | `APPROVED` | 商家查看，可进入提现前置校验 |
| 提现单 | `PENDING_AUDIT` | 平台审核通过/拒绝 |
| 提现单 | `PAYING` | 平台维护打款结果 |
| 提现单 | `PAID` | 商家查看到账结果 |
| 对账记录 | `DIFF_PENDING` | 平台处理差异 |

## 8. 数据获取与缓存策略

| 数据类型 | 获取方式 | 缓存策略 | 刷新时机 |
|---|---|---|---|
| 登录用户信息 | 登录后和应用初始化拉取 | 内存 + 本地 Token | Token 变更、401、手动刷新 |
| 分类列表 | 用户端首页/分类页拉取 | 短期缓存 | 后台类目变更后自然过期 |
| 商品列表 | 查询接口分页 | 按筛选条件缓存当前页 | 筛选变化、上下架后 |
| 购物车 | 进入购物车拉取 | 不做长期缓存 | 增删改后立即刷新 |
| 订单详情 | 详情页拉取 | 当前页面缓存 | 支付、取消、发货、售后后刷新 |
| 售后详情 | 详情页拉取 | 当前页面缓存 | 提交处理、上传物流、平台介入后刷新 |
| 财务数据 | 财务页面拉取 | 不跨页面缓存 | 审核、提现、对账处理后刷新 |
| 操作日志 | 查询接口分页 | 不缓存 | 查询条件变化 |

## 9. 异常与反馈规范

| 错误码 | 前端处理 |
|---|---|
| `AUTH_REQUIRED`、`TOKEN_EXPIRED` | 清理登录态，跳转登录页，保留来源地址 |
| `PERMISSION_DENIED` | 展示无权限页或禁用按钮后提示 |
| `IDEMPOTENT_PROCESSING` | 按钮保持 loading，提示请求处理中，允许稍后刷新 |
| `IDEMPOTENT_CONFLICT` | 停止提交，提示重复请求参数不一致，刷新页面状态 |
| `STATE_CONFLICT` | 重新拉取详情并提示状态已变化 |
| `ORDER_STOCK_NOT_ENOUGH` | 回到购物车或确认页，标记库存不足商品 |
| `ORDER_PRICE_CHANGED` | 展示价格变更提示，要求用户重新确认 |
| `MERCHANT_FROZEN` | 商家端展示冻结状态和允许履约范围 |
| `RECONCILE_DIFF_EXISTS` | 财务页面提示存在未处理差异，阻断提现或审核动作 |
| `RATE_LIMITED` | 禁用提交按钮短时间倒计时 |

## 10. 页面验收清单

- 用户端可以完成登录、浏览、加购、下单、支付结果查看、确认收货、售后申请与进度查看。
- 商家端可以完成入驻、商品提交审核、订单发货、售后处理、账单查看、提现申请、员工权限管理。
- 平台后台可以完成商家审核、商品审核、异常池处理、售后介入、结算审核、提现审核、对账差异处理、操作日志查询。
- 所有写接口都有 loading、防重复点击、`X-Request-Id`；下单、支付、售后、提现、审核等关键动作有幂等 Key。
- 所有列表页支持分页、筛选、空状态、错误重试。
- 所有高风险动作有二次确认、成功/失败反馈和操作日志入口。
- 金额、ID、时间、状态文案在三端展示一致。
