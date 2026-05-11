import type { AfterSaleStatus, OrderStatus, StatusTone } from "./types";

export const orderStatusMap: Record<OrderStatus, { label: string; tone: StatusTone }> = {
  PENDING_PAYMENT: { label: "待支付", tone: "warning" },
  PAID: { label: "待发货", tone: "info" },
  SHIPPED: { label: "待收货", tone: "purple" },
  COMPLETED: { label: "已完成", tone: "success" },
  CLOSED: { label: "已关闭", tone: "neutral" },
  EXCEPTION: { label: "异常", tone: "danger" }
};

export const afterSaleStatusMap: Record<AfterSaleStatus, { label: string; tone: StatusTone }> = {
  PENDING_MERCHANT: { label: "待商家处理", tone: "warning" },
  MERCHANT_APPROVED_RETURN: { label: "待退货", tone: "info" },
  RETURNED: { label: "已退回", tone: "purple" },
  REFUNDING: { label: "退款中", tone: "warning" },
  REFUNDED: { label: "已退款", tone: "success" },
  REJECTED: { label: "已拒绝", tone: "danger" },
  PLATFORM_INTERVENING: { label: "平台介入", tone: "danger" }
};

export const productStatusMap = {
  ON_SALE: { label: "在售", tone: "success" as StatusTone },
  OFF_SALE: { label: "下架", tone: "neutral" as StatusTone },
  AUDITING: { label: "审核中", tone: "warning" as StatusTone },
  REJECTED: { label: "已驳回", tone: "danger" as StatusTone }
};

export const merchantAuditMap = {
  PENDING: { label: "待审核", tone: "warning" as StatusTone },
  APPROVED: { label: "已通过", tone: "success" as StatusTone },
  REJECTED: { label: "已驳回", tone: "danger" as StatusTone }
};

export const merchantStatusMap = {
  ENABLED: { label: "正常", tone: "success" as StatusTone },
  FROZEN: { label: "冻结", tone: "danger" as StatusTone },
  DISABLED: { label: "停用", tone: "neutral" as StatusTone }
};

export const settlementStatusMap = {
  PENDING_AUDIT: { label: "待审核", tone: "warning" as StatusTone },
  APPROVED: { label: "已通过", tone: "success" as StatusTone },
  REJECTED: { label: "已驳回", tone: "danger" as StatusTone },
  PAID: { label: "已打款", tone: "purple" as StatusTone }
};

export function makeRequestId() {
  return `req-${crypto.randomUUID()}`;
}

export function makeIdempotencyKey(action: string) {
  return `${action}-${crypto.randomUUID()}`;
}
