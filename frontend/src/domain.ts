/**
 * 业务状态映射与工具函数
 *
 * 将后端返回的枚举值映射为前端展示用的中文文案和颜色色调，
 * 并提供通用工具函数（如生成 requestId 和幂等键）。
 */
import type { AfterSaleStatus, OrderStatus, StatusTone } from "./types";

/** 订单状态映射：key 为后端状态值，value 为显示文案和颜色色调 */
export const orderStatusMap: Record<OrderStatus, { label: string; tone: StatusTone }> = {
  PENDING_PAYMENT: { label: "待支付", tone: "warning" },
  PAID: { label: "待发货", tone: "info" },
  SHIPPED: { label: "待收货", tone: "purple" },
  COMPLETED: { label: "已完成", tone: "success" },
  CLOSED: { label: "已关闭", tone: "neutral" },
  EXCEPTION: { label: "异常", tone: "danger" }
};

/** 售后状态映射：key 为后端状态值，value 为显示文案和颜色色调 */
export const afterSaleStatusMap: Record<AfterSaleStatus, { label: string; tone: StatusTone }> = {
  PENDING_MERCHANT: { label: "待商家处理", tone: "warning" },
  MERCHANT_APPROVED_RETURN: { label: "待退货", tone: "info" },
  RETURNED: { label: "已退回", tone: "purple" },
  REFUNDING: { label: "退款中", tone: "warning" },
  REFUNDED: { label: "已退款", tone: "success" },
  REJECTED: { label: "已拒绝", tone: "danger" },
  PLATFORM_INTERVENING: { label: "平台介入", tone: "danger" }
};

/** 商品状态映射：在售 / 下架 / 审核中 / 已通过 / 已驳回 */
export const productStatusMap = {
  ON_SALE: { label: "在售", tone: "success" as StatusTone },
  OFF_SALE: { label: "下架", tone: "neutral" as StatusTone },
  AUDITING: { label: "审核中", tone: "warning" as StatusTone },
  APPROVED: { label: "已通过", tone: "info" as StatusTone },
  REJECTED: { label: "已驳回", tone: "danger" as StatusTone }
};

/** 商家入驻审核状态映射 */
export const merchantAuditMap = {
  PENDING: { label: "待审核", tone: "warning" as StatusTone },
  APPROVED: { label: "已通过", tone: "success" as StatusTone },
  REJECTED: { label: "已驳回", tone: "danger" as StatusTone }
};

/** 商家经营状态映射：正常 / 冻结 / 停用 */
export const merchantStatusMap = {
  ENABLED: { label: "正常", tone: "success" as StatusTone },
  FROZEN: { label: "冻结", tone: "danger" as StatusTone },
  DISABLED: { label: "停用", tone: "neutral" as StatusTone }
};

/** 结算单状态映射：待审核 / 已通过 / 已驳回 / 已打款 */
export const settlementStatusMap = {
  PENDING_AUDIT: { label: "待审核", tone: "warning" as StatusTone },
  APPROVED: { label: "已通过", tone: "success" as StatusTone },
  REJECTED: { label: "已驳回", tone: "danger" as StatusTone },
  PAID: { label: "已打款", tone: "purple" as StatusTone }
};

/** 生成全局唯一的请求追踪 ID（格式：req-<UUID>） */
export function makeRequestId() {
  return `req-${crypto.randomUUID()}`;
}

/** 生成幂等键：用于关键写操作的防重复提交（格式：<action>-<UUID>） */
export function makeIdempotencyKey(action: string) {
  return `${action}-${crypto.randomUUID()}`;
}
