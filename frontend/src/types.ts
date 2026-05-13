/**
 * 优选电商平台前端类型定义
 *
 * 包含所有核心业务类型和 API 请求/响应类型。
 * BIGINT 类型的 ID 字段统一使用 string 以避免 JS 精度丢失。
 * 金额字段统一使用 string 类型。
 */
import type { LucideIcon } from "lucide-react";

/** 门户类型：用户端 / 商家端 / 平台后台 */
export type Portal = "user" | "merchant" | "admin";

/** 侧边栏导航项：包含唯一标识、显示文字和图标 */
export type NavItem = {
  /** 导航项唯一标识 */
  id: string;
  /** 显示文字 */
  label: string;
  /** 图标组件 */
  icon: LucideIcon;
};

/** 状态标签色调：用于 StatusTag 组件的颜色方案 */
export type StatusTone = "neutral" | "success" | "warning" | "danger" | "info" | "purple";

/** 指标卡片：在 MetricGrid 中展示的单一业务指标 */
export type Metric = {
  /** 指标名称（如"待支付订单"） */
  label: string;
  /** 指标数值（如"18"） */
  value: string;
  /** 指标提示（如"最早超时 2 小时后"） */
  hint: string;
  /** 色调：决定卡片颜色 */
  tone: StatusTone;
};

/** 商品信息 */
export type Product = {
  /** 商品 ID */
  id: string;
  /** 商品名称 */
  name: string;
  /** 店铺名称 */
  storeName: string;
  /** 商品类目 */
  category: string;
  /** 商品价格 */
  price: string;
  /** 库存数量 */
  stock: number;
  /** 销量 */
  sales: number;
  /** 商品状态：在售 / 下架 / 审核中 / 已驳回 */
  status: "ON_SALE" | "OFF_SALE" | "AUDITING" | "REJECTED";
  /** 商品图片 URL */
  image: string;
};

/** 购物车商品项 */
export type CartItem = {
  /** 购物车项 ID */
  id: string;
  /** 商品名称 */
  productName: string;
  /** SKU 规格描述 */
  skuName: string;
  /** 店铺名称 */
  storeName: string;
  /** 单价 */
  price: string;
  /** 购买数量 */
  quantity: number;
  /** 是否被选中（结算时） */
  selected: boolean;
};

/** 订单状态枚举 */
export type OrderStatus =
  | "PENDING_PAYMENT"
  | "PAID"
  | "SHIPPED"
  | "COMPLETED"
  | "CLOSED"
  | "EXCEPTION";

/** 订单信息 */
export type Order = {
  /** 订单 ID */
  id: string;
  /** 订单编号（业务可读编号） */
  orderNo: string;
  /** 店铺名称 */
  storeName: string;
  /** 买家姓名 */
  userName: string;
  /** 订单总金额 */
  amount: string;
  /** 商品数量 */
  itemCount: number;
  /** 订单状态 */
  status: OrderStatus;
  /** 支付时间（可选） */
  paidAt?: string;
  /** 下单时间 */
  createdAt: string;
  /** 物流信息（可选） */
  shipment?: string;
};

/** 售后状态枚举 */
export type AfterSaleStatus =
  | "PENDING_MERCHANT"
  | "MERCHANT_APPROVED_RETURN"
  | "RETURNED"
  | "REFUNDING"
  | "REFUNDED"
  | "REJECTED"
  | "PLATFORM_INTERVENING";

/** 售后记录 */
export type AfterSale = {
  /** 售后单 ID */
  id: string;
  /** 售后单编号 */
  afterSaleNo: string;
  /** 关联订单编号 */
  orderNo: string;
  /** 店铺名称 */
  storeName: string;
  /** 售后原因 */
  reason: string;
  /** 售后金额 */
  amount: string;
  /** 售后状态 */
  status: AfterSaleStatus;
  /** 处理截止时间 */
  deadline: string;
};

/** 商家信息 */
export type Merchant = {
  /** 商家 ID */
  id: string;
  /** 店铺名称 */
  name: string;
  /** 企业主体名称 */
  companyName: string;
  /** 联系方式 */
  contact: string;
  /** 经营类目 */
  category: string;
  /** 入驻审核状态：待审核 / 已通过 / 已驳回 */
  auditStatus: "PENDING" | "APPROVED" | "REJECTED";
  /** 店铺经营状态：正常 / 冻结 / 停用 */
  status: "ENABLED" | "FROZEN" | "DISABLED";
  /** 提交时间 */
  submittedAt: string;
};

/** 结算单信息 */
export type Settlement = {
  /** 结算单 ID */
  id: string;
  /** 结算单编号 */
  settlementNo: string;
  /** 商家名称 */
  merchantName: string;
  /** 结算周期描述 */
  period: string;
  /** 结算总额 */
  grossAmount: string;
  /** 平台佣金 */
  commission: string;
  /** 退款扣减 */
  refundDeduction: string;
  /** 应结金额 */
  payableAmount: string;
  /** 结算状态：待审核 / 已通过 / 已驳回 / 已打款 */
  status: "PENDING_AUDIT" | "APPROVED" | "REJECTED" | "PAID";
};

/** 异常记录 */
export type ExceptionRecord = {
  /** 异常记录 ID */
  id: string;
  /** 异常类型：订单 / 支付 / 退款 / 对账 */
  type: "ORDER" | "PAYMENT" | "REFUND" | "RECONCILIATION";
  /** 异常标题 */
  title: string;
  /** 关联业务单号 */
  relatedNo: string;
  /** 异常金额 */
  amount: string;
  /** 处理状态：待处理 / 处理中 / 已解决 */
  status: "PENDING" | "PROCESSING" | "RESOLVED";
  /** 创建时间 */
  createdAt: string;
};

/** 操作日志 */
export type OperationLog = {
  /** 日志 ID */
  id: string;
  /** 操作人 */
  operator: string;
  /** 操作模块 */
  module: string;
  /** 操作动作 */
  action: string;
  /** 操作对象 */
  target: string;
  /** 操作结果 */
  result: string;
  /** 操作时间 */
  createdAt: string;
};

/** 商家员工 */
export type Staff = {
  /** 员工 ID */
  id: string;
  /** 关联用户 ID */
  userId: string;
  /** 商家 ID */
  merchantId: string;
  /** 员工姓名 */
  staffName: string;
  /** 角色类型 */
  roleType: string;
  /** 状态 */
  status: "ENABLED" | "DISABLED";
  /** 备注 */
  remark: string;
  /** 创建时间 */
  createdAt: string;
};

// ── Auth Types ──

/** 加密相关类型命名空间 */
export namespace Encrypt {
  /** RSA 公钥信息 */
  export type PublicKey = {
    /** Base64 编码的公钥字符串 */
    key: string;
    /** 加密算法名称 */
    algorithm: string;
  };
}

/** 认证状态：应用全局的登录态信息 */
export type AuthState = {
  /** 是否已认证 */
  authenticated: boolean;
  /** 访问令牌（JWT Token） */
  accessToken: string | null;
  /** 令牌类型（如 Bearer） */
  tokenType: string | null;
  /** 当前登录主体信息 */
  currentPrincipal: CurrentPrincipal | null;
};

/** 当前登录主体：包含用户身份和权限信息 */
export type CurrentPrincipal = {
  /** 主体 ID */
  principalId: string;
  /** 主体类型（USER / MERCHANT_OWNER / PLATFORM_ADMIN） */
  principalType: string;
  /** 用户 ID */
  userId: string;
  /** 商家 ID（用户端为 null） */
  merchantId: string | null;
  /** 账号名 */
  account: string;
  /** 显示名称 */
  displayName: string;
  /** 角色编码集合 */
  roleCodeSet: string[];
  /** 权限编码集合 */
  permissionCodeSet: string[];
};

/** 登录请求参数 */
export type LoginRequest = {
  /** 账号 */
  account: string;
  /** 密码（RSA 加密后的密文） */
  password: string;
  /** 登录主体类型 */
  principalType: string;
};

/** 登录响应结果 */
export type LoginResponse = {
  /** 访问令牌 */
  accessToken: string;
  /** 令牌类型 */
  tokenType: string;
  /** 令牌过期时间（秒） */
  expiresInSeconds: number;
  /** 当前登录主体信息 */
  currentPrincipal: CurrentPrincipal;
};
