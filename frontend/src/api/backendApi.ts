/**
 * 后端 API 调用模块
 *
 * 提供三个门户的首页数据获取函数。
 * 优先调用真实后端接口，异常时自动降级为 mock 数据，
 * 确保系统在任何情况下都不报错。
 */
import type { AfterSale, CartItem, ExceptionRecord, Merchant, Metric, OperationLog, Order, Product, Settlement, Staff } from "../types";
import { afterSaleStatusMap, orderStatusMap } from "../domain";

// 后端 API 基础路径
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "/api/v1";

/** 从 localStorage 获取 JWT Token */
function getToken(): string | null {
  try {
    const raw = localStorage.getItem("youxuan_auth");
    if (!raw) return null;
    return JSON.parse(raw).accessToken;
  } catch {
    return null;
  }
}

/** 通用请求头 */
function headers(): Record<string, string> {
  const h: Record<string, string> = {
    "Content-Type": "application/json",
    "X-Request-Id": crypto.randomUUID()
  };
  const token = getToken();
  if (token) h["Authorization"] = "Bearer " + token;
  return h;
}

/**
 * 安全的 JSON 请求：调用后端接口，失败时返回 null 而非抛异常。
 */
async function safeFetch<T>(url: string): Promise<T | null> {
  const controller = new AbortController();
  const timeout = window.setTimeout(() => controller.abort(), 2500);
  try {
    const resp = await fetch(url, { headers: headers(), signal: controller.signal });
    if (!resp.ok) return null;
    const body = await resp.json();
    if (body.code !== "SUCCESS") return null;
    return body.data as T;
  } catch {
    return null;
  } finally {
    window.clearTimeout(timeout);
  }
}

/**
 * 安全的列表请求：返回数组，失败时返回空数组。
 */
async function safeList<T>(url: string): Promise<T[]> {
  const data = await safeFetch<{ list?: T[]; items?: T[] }>(url);
  if (!data) return [];
  return data.list || data.items || [];
}

// ── 商品相关 ──

/** 获取可售商品列表 */
async function fetchProducts(): Promise<Product[]> {
  const data = await safeFetch<{ list: any[]; total: number }>(
    `${API_BASE_URL}/products?pageNo=1&pageSize=50`
  );
  if (!data || !data.list) return [];
  return data.list.map((p: any) => ({
    id: String(p.id || ""),
    name: p.productName || "",
    storeName: "优选自营旗舰店",
    category: "",
    price: String(p.salePrice || "0"),
    stock: p.stock || 0,
    sales: p.sales || 0,
    status: p.saleStatus === "ON_SALE" ? "ON_SALE" : "OFF_SALE",
    image: p.mainImageUrl || ""
  }));
}

// ── 购物车相关 ──

/** 获取当前用户的购物车数据 */
async function fetchCartItems(): Promise<CartItem[]> {
  const list = await safeList<any>(`${API_BASE_URL}/carts`);
  return list.map((item: any) => ({
    id: String(item.id || ""),
    productName: item.productName || item.skuName || "",
    skuName: item.skuName || "",
    storeName: "优选自营旗舰店",
    price: String(item.salePrice || "0"),
    quantity: item.quantity || 1,
    selected: item.checked !== 0
  }));
}

// ── 订单相关 ──

/** 获取当前用户的订单列表 */
async function fetchOrders(): Promise<Order[]> {
  const data = await safeFetch<{ list: any[] }>(`${API_BASE_URL}/orders?pageNo=1&pageSize=20`);
  if (!data || !data.list) return [];
  return data.list.map((o: any) => {
    const statusMap: Record<string, Order["status"]> = {
      CREATED: "PENDING_PAYMENT", PAID: "PAID", SHIPPED: "SHIPPED",
      COMPLETED: "COMPLETED", CANCELED: "CLOSED", CLOSED: "CLOSED"
    };
    return {
      id: String(o.id || ""),
      orderNo: o.orderNo || "",
      storeName: "优选自营旗舰店",
      userName: "",
      amount: String(o.payableAmount || "0"),
      itemCount: 0,
      status: statusMap[o.orderStatus] || "PENDING_PAYMENT",
      paidAt: o.paidAt || undefined,
      createdAt: o.createdAt || ""
    };
  });
}

// ── 售后相关 ──

/** 获取售后列表 */
async function fetchAfterSales(): Promise<AfterSale[]> {
  const list = await safeList<any>(`${API_BASE_URL}/after-sales`);
  return list.map((a: any) => {
    const statusMap: Record<string, AfterSale["status"]> = {
      APPLYING: "PENDING_MERCHANT", MERCHANT_APPROVED: "MERCHANT_APPROVED_RETURN",
      MERCHANT_REJECTED: "REJECTED", USER_RETURNED: "RETURNED",
      PLATFORM_INTERVENING: "PLATFORM_INTERVENING",
      COMPLETED: "REFUNDED", CLOSED: "REFUNDED"
    };
    return {
      id: String(a.id || ""),
      afterSaleNo: a.afterSaleNo || "",
      orderNo: a.orderNo || "",
      storeName: "优选自营旗舰店",
      reason: a.reason || "",
      amount: String(a.applyAmount || "0"),
      status: statusMap[a.status] || "PENDING_MERCHANT",
      deadline: ""
    };
  });
}

// ── 商家相关 ──

/** 获取商家列表（后台用） */
async function fetchMerchants(): Promise<Merchant[]> {
  const list = await safeList<any>(`${API_BASE_URL}/admin/merchants`);
  return list.map((m: any) => ({
    id: String(m.id || ""),
    name: m.storeName || m.companyName || "",
    companyName: m.companyName || "",
    contact: m.contactMobile || "",
    category: "",
    auditStatus: m.auditStatus || "PENDING",
    status: m.status || "DISABLED",
    submittedAt: m.createdAt || ""
  }));
}

// ── 结算相关 ──

/** 获取结算单列表 */
async function fetchSettlements(): Promise<Settlement[]> {
  const list = await safeList<any>(`${API_BASE_URL}/admin/settlements`);
  return list.map((s: any) => ({
    id: String(s.id || ""),
    settlementNo: s.settlementNo || "",
    merchantName: s.merchantName || "",
    period: (s.startDate || "") + " 至 " + (s.endDate || ""),
    grossAmount: String(s.orderAmount || "0"),
    commission: String(s.commissionAmount || "0"),
    refundDeduction: String(s.refundAmount || "0"),
    payableAmount: String(s.payableAmount || "0"),
    status: s.status || "PENDING_AUDIT"
  }));
}

// ── 员工相关 ──

/** 获取商家员工列表，支持按姓名搜索 */
async function fetchStaffList(keyword?: string): Promise<Staff[]> {
  const params = keyword ? `?keyword=${encodeURIComponent(keyword)}` : "";
  const controller = new AbortController();
  const timeout = window.setTimeout(() => controller.abort(), 2500);
  try {
    const resp = await fetch(`${API_BASE_URL}/merchant/staffs${params}`, { headers: headers(), signal: controller.signal });
    if (!resp.ok) return [];
    const body = await resp.json();
    if (body.code !== "SUCCESS") return [];
    return (body.data || []) as Staff[];
  } catch {
    return [];
  } finally {
    window.clearTimeout(timeout);
  }
}

/** 根据手机号查找用户 */
async function lookupUser(mobile: string): Promise<{ id: string; mobile: string; nickname: string } | null> {
  const data = await safeFetch<{ id: string; mobile: string; nickname: string }>(
    `${API_BASE_URL}/merchant/staffs/lookup?mobile=${encodeURIComponent(mobile)}`
  );
  return data;
}

/** 创建员工 */
async function createStaff(request: { mobile: string; staffName: string; roleType: string }): Promise<Staff> {
  const resp = await fetch(`${API_BASE_URL}/merchant/staffs`, {
    method: "POST",
    headers: headers(),
    body: JSON.stringify(request),
  });
  const body = await resp.json();
  if (body.code !== "SUCCESS") {
    throw new Error(body.message || "添加员工失败");
  }
  return body.data as Staff;
}

/** 切换员工启用/禁用状态 */
async function toggleStaffStatus(staffId: string): Promise<void> {
  const resp = await fetch(`${API_BASE_URL}/merchant/staffs/${staffId}/status`, {
    method: "PUT",
    headers: headers(),
  });
  const body = await resp.json();
  if (body.code !== "SUCCESS") {
    throw new Error(body.message || "操作失败");
  }
}

// ── 异常相关 ──

/** 获取异常池列表 */
async function fetchExceptions(): Promise<ExceptionRecord[]> {
  const list = await safeList<any>(`${API_BASE_URL}/admin/exceptions`);
  return list.map((e: any) => ({
    id: String(e.id || ""),
    type: e.exceptionType || "ORDER",
    title: e.reason || e.exceptionNo || "",
    relatedNo: e.bizNo || "",
    amount: "0",
    status: e.status || "PENDING",
    createdAt: e.createdAt || ""
  }));
}

// ── 操作日志 ──

/** 获取操作日志 */
async function fetchOperationLogs(): Promise<OperationLog[]> {
  const data = await safeFetch<{ list: any[] }>(`${API_BASE_URL}/admin/operation-logs?pageNo=1&pageSize=20`);
  if (!data || !data.list) return [];
  return data.list.map((l: any) => ({
    id: String(l.id || ""),
    operator: l.operatorName || "",
    module: l.moduleCode || "",
    action: l.actionCode || "",
    target: l.targetId || "",
    result: l.result || "SUCCESS",
    createdAt: l.createdAt || ""
  }));
}

// ── Mock Fallback 数据（后端接口不可用时使用）──

const mockMetrics: Record<string, Metric[]> = {
  user: [
    { label: "待支付订单", value: "1", hint: "15 分钟后自动关闭", tone: "warning" },
    { label: "待收货", value: "1", hint: "1 个包裹运输中", tone: "purple" },
    { label: "售后处理中", value: "2", hint: "平台介入 1 单", tone: "danger" }
  ],
  merchant: [
    { label: "待发货订单", value: "18", hint: "最早超时 2 小时后", tone: "warning" },
    { label: "售后待处理", value: "5", hint: "2 单即将超时", tone: "danger" },
    { label: "可提现余额", value: "45,189.35", hint: "冻结 3,280.00", tone: "success" },
    { label: "审核中商品", value: "7", hint: "平均 4 小时内完成", tone: "info" }
  ],
  admin: [
    { label: "商家待审核", value: "12", hint: "资质过期提醒 3 条", tone: "warning" },
    { label: "商品待审核", value: "86", hint: "生鲜类目占 34%", tone: "info" },
    { label: "异常池待处理", value: "9", hint: "支付异常 3 单", tone: "danger" },
    { label: "待审核结算", value: "2", hint: "合计 106,412.75", tone: "purple" }
  ]
};

// ── 导出三个门户的首页数据获取函数 ──

export const api = {
  /** 获取用户端首页数据 */
  async getUserHome() {
    const [products, cartItems, orders, afterSales] = await Promise.all([
      fetchProducts().catch(() => [] as Product[]),
      fetchCartItems().catch(() => [] as CartItem[]),
      fetchOrders().catch(() => [] as Order[]),
      fetchAfterSales().catch(() => [] as AfterSale[])
    ]);
    return {
      metrics: products.length > 0
        ? [
            { label: "待支付", value: String(orders.filter(o => o.status === "PENDING_PAYMENT").length), hint: "进行中", tone: "warning" as const },
            { label: "待收货", value: String(orders.filter(o => o.status === "SHIPPED").length), hint: "运输中", tone: "purple" as const },
            { label: "售后", value: String(afterSales.length), hint: "进行中", tone: "danger" as const }
          ]
        : mockMetrics.user,
      products: products.length > 0 ? products : [],
      cartItems: cartItems.length > 0 ? cartItems : [],
      orders: orders.length > 0 ? orders : [],
      afterSales: afterSales.length > 0 ? afterSales : []
    };
  },

  /** 获取商家端首页数据 */
  async getMerchantHome() {
    const [orders, afterSales, settlements] = await Promise.all([
      fetchOrders().catch(() => [] as Order[]),
      fetchAfterSales().catch(() => [] as AfterSale[]),
      fetchSettlements().catch(() => [] as Settlement[])
    ]);
    return {
      metrics: orders.length > 0
        ? [
            { label: "待发货", value: String(orders.filter(o => o.status === "PAID").length), hint: "最早超时 2 小时后", tone: "warning" as const },
            { label: "售后待处理", value: String(afterSales.length), hint: "进行中", tone: "danger" as const },
            { label: "结算中", value: String(settlements.filter(s => s.status === "PENDING_AUDIT").length), hint: "待审核", tone: "info" as const }
          ]
        : mockMetrics.merchant,
      products: [],
      orders,
      afterSales,
      settlements
    };
  },

  /** 获取员工列表 */
  async fetchStaffList(keyword?: string): Promise<Staff[]> {
    return fetchStaffList(keyword);
  },

  /** 根据手机号查找用户 */
  async lookupUser(mobile: string): Promise<{ id: string; mobile: string; nickname: string } | null> {
    return lookupUser(mobile);
  },

  /** 创建员工 */
  async createStaff(request: { mobile: string; staffName: string; roleType: string }): Promise<Staff> {
    return createStaff(request);
  },

  /** 切换员工状态 */
  async toggleStaffStatus(staffId: string): Promise<void> {
    return toggleStaffStatus(staffId);
  },

  /** 获取平台后台首页数据 */
  async getAdminHome() {
    const [merchants, products, orders, afterSales, settlements, exceptions, operationLogs] = await Promise.all([
      fetchMerchants().catch(() => [] as Merchant[]),
      fetchProducts().catch(() => [] as Product[]),
      fetchOrders().catch(() => [] as Order[]),
      fetchAfterSales().catch(() => [] as AfterSale[]),
      fetchSettlements().catch(() => [] as Settlement[]),
      fetchExceptions().catch(() => [] as ExceptionRecord[]),
      fetchOperationLogs().catch(() => [] as OperationLog[])
    ]);
    return {
      metrics: merchants.length > 0
        ? [
            { label: "商家待审核", value: String(merchants.filter(m => m.auditStatus === "PENDING").length), hint: "待处理", tone: "warning" as const },
            { label: "异常待处理", value: String(exceptions.filter(e => e.status === "PENDING").length), hint: "需关注", tone: "danger" as const },
            { label: "结算待审核", value: String(settlements.filter(s => s.status === "PENDING_AUDIT").length), hint: "待审核", tone: "purple" as const }
          ]
        : mockMetrics.admin,
      merchants,
      products,
      orders,
      afterSales,
      settlements,
      exceptions,
      operationLogs
    };
  }
};
