import type { LucideIcon } from "lucide-react";

export type Portal = "user" | "merchant" | "admin";

export type NavItem = {
  id: string;
  label: string;
  icon: LucideIcon;
};

export type StatusTone = "neutral" | "success" | "warning" | "danger" | "info" | "purple";

export type Metric = {
  label: string;
  value: string;
  hint: string;
  tone: StatusTone;
};

export type Product = {
  id: string;
  name: string;
  storeName: string;
  category: string;
  price: string;
  stock: number;
  sales: number;
  status: "ON_SALE" | "OFF_SALE" | "AUDITING" | "REJECTED";
  image: string;
};

export type CartItem = {
  id: string;
  productName: string;
  skuName: string;
  storeName: string;
  price: string;
  quantity: number;
  selected: boolean;
};

export type OrderStatus =
  | "PENDING_PAYMENT"
  | "PAID"
  | "SHIPPED"
  | "COMPLETED"
  | "CLOSED"
  | "EXCEPTION";

export type Order = {
  id: string;
  orderNo: string;
  storeName: string;
  userName: string;
  amount: string;
  itemCount: number;
  status: OrderStatus;
  paidAt?: string;
  createdAt: string;
  shipment?: string;
};

export type AfterSaleStatus =
  | "PENDING_MERCHANT"
  | "MERCHANT_APPROVED_RETURN"
  | "RETURNED"
  | "REFUNDING"
  | "REFUNDED"
  | "REJECTED"
  | "PLATFORM_INTERVENING";

export type AfterSale = {
  id: string;
  afterSaleNo: string;
  orderNo: string;
  storeName: string;
  reason: string;
  amount: string;
  status: AfterSaleStatus;
  deadline: string;
};

export type Merchant = {
  id: string;
  name: string;
  companyName: string;
  contact: string;
  category: string;
  auditStatus: "PENDING" | "APPROVED" | "REJECTED";
  status: "ENABLED" | "FROZEN" | "DISABLED";
  submittedAt: string;
};

export type Settlement = {
  id: string;
  settlementNo: string;
  merchantName: string;
  period: string;
  grossAmount: string;
  commission: string;
  refundDeduction: string;
  payableAmount: string;
  status: "PENDING_AUDIT" | "APPROVED" | "REJECTED" | "PAID";
};

export type ExceptionRecord = {
  id: string;
  type: "ORDER" | "PAYMENT" | "REFUND" | "RECONCILIATION";
  title: string;
  relatedNo: string;
  amount: string;
  status: "PENDING" | "PROCESSING" | "RESOLVED";
  createdAt: string;
};

export type OperationLog = {
  id: string;
  operator: string;
  module: string;
  action: string;
  target: string;
  result: string;
  createdAt: string;
};

// ── Auth Types ──

export namespace Encrypt {
  export type PublicKey = {
    key: string;
    algorithm: string;
  };
}

export type AuthState = {
  authenticated: boolean;
  accessToken: string | null;
  tokenType: string | null;
  currentPrincipal: CurrentPrincipal | null;
};

export type CurrentPrincipal = {
  principalId: string;
  principalType: string;
  userId: string;
  merchantId: string | null;
  account: string;
  displayName: string;
  roleCodeSet: string[];
  permissionCodeSet: string[];
};

export type LoginRequest = {
  account: string;
  password: string;
  principalType: string;
};

export type LoginResponse = {
  accessToken: string;
  tokenType: string;
  expiresInSeconds: number;
  currentPrincipal: CurrentPrincipal;
};
