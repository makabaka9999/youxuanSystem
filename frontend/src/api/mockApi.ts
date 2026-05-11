import type {
  AfterSale,
  CartItem,
  ExceptionRecord,
  Merchant,
  Metric,
  OperationLog,
  Order,
  Product,
  Settlement
} from "../types";

const delay = <T,>(data: T) => new Promise<T>((resolve) => window.setTimeout(() => resolve(data), 160));

export const products: Product[] = [
  {
    id: "10001",
    name: "有机高山蓝莓礼盒",
    storeName: "青岚果园旗舰店",
    category: "生鲜水果",
    price: "89.00",
    stock: 318,
    sales: 2341,
    status: "ON_SALE",
    image: "https://images.unsplash.com/photo-1498557850523-fd3d118b962e?auto=format&fit=crop&w=900&q=80"
  },
  {
    id: "10002",
    name: "低温烘焙坚果组合",
    storeName: "禾谷食研社",
    category: "休闲食品",
    price: "59.90",
    stock: 96,
    sales: 1520,
    status: "ON_SALE",
    image: "https://images.unsplash.com/photo-1608797178974-15b35a64ede9?auto=format&fit=crop&w=900&q=80"
  },
  {
    id: "10003",
    name: "手工陶瓷马克杯",
    storeName: "陶里日用",
    category: "家居日用",
    price: "42.00",
    stock: 44,
    sales: 738,
    status: "AUDITING",
    image: "https://images.unsplash.com/photo-1514228742587-6b1558fcca3d?auto=format&fit=crop&w=900&q=80"
  },
  {
    id: "10004",
    name: "山茶花精华洗护套装",
    storeName: "棠研个护",
    category: "美妆个护",
    price: "129.00",
    stock: 0,
    sales: 926,
    status: "OFF_SALE",
    image: "https://images.unsplash.com/photo-1620916566398-39f1143ab7be?auto=format&fit=crop&w=900&q=80"
  }
];

export const cartItems: CartItem[] = [
  { id: "c1", productName: "有机高山蓝莓礼盒", skuName: "500g 双盒装", storeName: "青岚果园旗舰店", price: "89.00", quantity: 2, selected: true },
  { id: "c2", productName: "低温烘焙坚果组合", skuName: "每日坚果 30 包", storeName: "禾谷食研社", price: "59.90", quantity: 1, selected: true },
  { id: "c3", productName: "手工陶瓷马克杯", skuName: "雾白 350ml", storeName: "陶里日用", price: "42.00", quantity: 1, selected: false }
];

export const orders: Order[] = [
  { id: "o1", orderNo: "SO202605080001", storeName: "青岚果园旗舰店", userName: "赵女士", amount: "178.00", itemCount: 2, status: "PAID", paidAt: "2026-05-08 10:22:18", createdAt: "2026-05-08 10:20:03" },
  { id: "o2", orderNo: "SO202605070086", storeName: "禾谷食研社", userName: "林先生", amount: "59.90", itemCount: 1, status: "SHIPPED", paidAt: "2026-05-07 16:14:09", createdAt: "2026-05-07 16:10:44", shipment: "顺丰速运 SF1234567890" },
  { id: "o3", orderNo: "SO202605060125", storeName: "陶里日用", userName: "陈先生", amount: "42.00", itemCount: 1, status: "PENDING_PAYMENT", createdAt: "2026-05-08 11:37:12" },
  { id: "o4", orderNo: "SO202605050099", storeName: "棠研个护", userName: "周女士", amount: "129.00", itemCount: 1, status: "EXCEPTION", paidAt: "2026-05-05 09:07:11", createdAt: "2026-05-05 09:05:48" }
];

export const afterSales: AfterSale[] = [
  { id: "as1", afterSaleNo: "AS202605080003", orderNo: "SO202605070086", storeName: "禾谷食研社", reason: "包装破损，申请退货退款", amount: "59.90", status: "PENDING_MERCHANT", deadline: "2026-05-09 18:00:00" },
  { id: "as2", afterSaleNo: "AS202605070011", orderNo: "SO202605050099", storeName: "棠研个护", reason: "支付异常后退款失败", amount: "129.00", status: "PLATFORM_INTERVENING", deadline: "2026-05-08 20:00:00" }
];

export const merchants: Merchant[] = [
  { id: "m1", name: "青岚果园旗舰店", companyName: "杭州青岚农业有限公司", contact: "李青 138****2301", category: "生鲜水果", auditStatus: "PENDING", status: "DISABLED", submittedAt: "2026-05-08 09:48:12" },
  { id: "m2", name: "禾谷食研社", companyName: "上海禾谷食品有限公司", contact: "王禾 139****8172", category: "休闲食品", auditStatus: "APPROVED", status: "ENABLED", submittedAt: "2026-05-04 14:21:59" },
  { id: "m3", name: "棠研个护", companyName: "广州棠研生物科技有限公司", contact: "郑棠 137****3308", category: "美妆个护", auditStatus: "APPROVED", status: "FROZEN", submittedAt: "2026-04-28 11:11:33" }
];

export const settlements: Settlement[] = [
  { id: "s1", settlementNo: "ST202605080001", merchantName: "禾谷食研社", period: "2026-04-30 至 2026-05-06", grossAmount: "48230.90", commission: "2411.55", refundDeduction: "630.00", payableAmount: "45189.35", status: "PENDING_AUDIT" },
  { id: "s2", settlementNo: "ST202605070002", merchantName: "青岚果园旗舰店", period: "2026-04-29 至 2026-05-05", grossAmount: "65812.00", commission: "3290.60", refundDeduction: "1298.00", payableAmount: "61223.40", status: "APPROVED" }
];

export const exceptions: ExceptionRecord[] = [
  { id: "e1", type: "ORDER", title: "支付成功但订单已取消", relatedNo: "SO202605050099", amount: "129.00", status: "PENDING", createdAt: "2026-05-08 08:31:21" },
  { id: "e2", type: "PAYMENT", title: "渠道回调重复且金额不一致", relatedNo: "PO202605080017", amount: "178.00", status: "PROCESSING", createdAt: "2026-05-08 10:34:52" },
  { id: "e3", type: "RECONCILIATION", title: "渠道退款金额与平台退款单差异", relatedNo: "RC202605070004", amount: "20.00", status: "PENDING", createdAt: "2026-05-07 23:12:45" }
];

export const operationLogs: OperationLog[] = [
  { id: "log1", operator: "平台审核员-韩", module: "商家审核", action: "审核通过", target: "禾谷食研社", result: "SUCCESS", createdAt: "2026-05-08 09:40:19" },
  { id: "log2", operator: "平台财务-杜", module: "提现审核", action: "审核驳回", target: "WD202605080003", result: "SUCCESS", createdAt: "2026-05-08 11:03:28" },
  { id: "log3", operator: "系统任务", module: "对账", action: "生成差异", target: "RC202605070004", result: "SUCCESS", createdAt: "2026-05-07 23:12:45" }
];

export const api = {
  getUserHome: () =>
    delay({
      metrics: [
        { label: "待支付订单", value: "1", hint: "15 分钟后自动关闭", tone: "warning" },
        { label: "待收货", value: "1", hint: "1 个包裹运输中", tone: "purple" },
        { label: "售后处理中", value: "2", hint: "平台介入 1 单", tone: "danger" }
      ] satisfies Metric[],
      products,
      cartItems,
      orders,
      afterSales
    }),
  getMerchantHome: () =>
    delay({
      metrics: [
        { label: "待发货订单", value: "18", hint: "最早超时 2 小时后", tone: "warning" },
        { label: "售后待处理", value: "5", hint: "2 单即将超时", tone: "danger" },
        { label: "可提现余额", value: "45,189.35", hint: "冻结 3,280.00", tone: "success" },
        { label: "审核中商品", value: "7", hint: "平均 4 小时内完成", tone: "info" }
      ] satisfies Metric[],
      products,
      orders,
      afterSales,
      settlements
    }),
  getAdminHome: () =>
    delay({
      metrics: [
        { label: "商家待审核", value: "12", hint: "资质过期提醒 3 条", tone: "warning" },
        { label: "商品待审核", value: "86", hint: "生鲜类目占 34%", tone: "info" },
        { label: "异常池待处理", value: "9", hint: "支付异常 3 单", tone: "danger" },
        { label: "待审核结算", value: "2", hint: "合计 106,412.75", tone: "purple" }
      ] satisfies Metric[],
      merchants,
      products,
      orders,
      afterSales,
      settlements,
      exceptions,
      operationLogs
    })
};
