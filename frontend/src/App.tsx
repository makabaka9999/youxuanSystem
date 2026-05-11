import { useEffect, useMemo, useState } from "react";
import {
  BadgeCheck,
  Banknote,
  ClipboardList,
  Home,
  LayoutDashboard,
  PackageSearch,
  ReceiptText,
  RotateCcw,
  Settings,
  ShieldCheck,
  ShoppingCart,
  Store,
  Truck,
  UserRound
} from "lucide-react";
import { fetchBackendHealth, type BackendHealth } from "./api/backendClient";
import { api } from "./api/mockApi";
import { LoadingScreen } from "./components";
import { AdminPortal } from "./pages/AdminPortal";
import { MerchantPortal } from "./pages/MerchantPortal";
import { UserPortal } from "./pages/UserPortal";
import type { NavItem, Portal } from "./types";

type AppData = {
  user: Awaited<ReturnType<typeof api.getUserHome>>;
  merchant: Awaited<ReturnType<typeof api.getMerchantHome>>;
  admin: Awaited<ReturnType<typeof api.getAdminHome>>;
};

const portalMeta = {
  user: {
    title: "用户端",
    subtitle: "H5 交易闭环",
    role: "USER",
    nav: [
      { id: "home", label: "首页", icon: Home },
      { id: "products", label: "商品", icon: PackageSearch },
      { id: "cart", label: "购物车", icon: ShoppingCart },
      { id: "orders", label: "订单", icon: ClipboardList },
      { id: "after-sales", label: "售后", icon: RotateCcw },
      { id: "profile", label: "资料", icon: UserRound }
    ] satisfies NavItem[]
  },
  merchant: {
    title: "商家端",
    subtitle: "店铺经营工作台",
    role: "MERCHANT_OWNER",
    nav: [
      { id: "dashboard", label: "工作台", icon: LayoutDashboard },
      { id: "store", label: "店铺", icon: Store },
      { id: "products", label: "商品", icon: PackageSearch },
      { id: "orders", label: "订单", icon: Truck },
      { id: "after-sales", label: "售后", icon: RotateCcw },
      { id: "finance", label: "财务", icon: Banknote },
      { id: "staffs", label: "员工", icon: ShieldCheck }
    ] satisfies NavItem[]
  },
  admin: {
    title: "平台后台",
    subtitle: "审核风控与财务运营",
    role: "PLATFORM_ADMIN",
    nav: [
      { id: "dashboard", label: "看板", icon: LayoutDashboard },
      { id: "merchant-audit", label: "商家审核", icon: BadgeCheck },
      { id: "product-audit", label: "商品审核", icon: PackageSearch },
      { id: "exceptions", label: "异常池", icon: RotateCcw },
      { id: "settlement", label: "结算提现", icon: ReceiptText },
      { id: "logs", label: "操作日志", icon: ClipboardList },
      { id: "settings", label: "配置", icon: Settings }
    ] satisfies NavItem[]
  }
};

export function App() {
  const [portal, setPortal] = useState<Portal>("admin");
  const [data, setData] = useState<AppData | null>(null);
  const [backendHealth, setBackendHealth] = useState<BackendHealth>({ connected: false, status: "CHECKING" });

  useEffect(() => {
    Promise.all([api.getUserHome(), api.getMerchantHome(), api.getAdminHome()]).then(([user, merchant, admin]) => {
      setData({ user, merchant, admin });
    });
    fetchBackendHealth().then(setBackendHealth);
  }, []);

  const active = portalMeta[portal];
  const nav = useMemo(() => active.nav, [active.nav]);

  if (!data) return <LoadingScreen />;

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="brand">
          <div className="brand-mark">优</div>
          <div>
            <strong>优选 P0</strong>
            <span>多商户电商平台</span>
          </div>
        </div>

        <div className="portal-switcher" role="tablist" aria-label="切换端">
          {(["user", "merchant", "admin"] as Portal[]).map((item) => (
            <button
              className={portal === item ? "active" : ""}
              key={item}
              type="button"
              onClick={() => setPortal(item)}
            >
              {portalMeta[item].title}
            </button>
          ))}
        </div>

        <nav className="side-nav">
          {nav.map((item) => {
            const Icon = item.icon;
            return (
              <button className={item.id === nav[0].id ? "active" : ""} key={item.id} type="button">
                <Icon size={18} />
                {item.label}
              </button>
            );
          })}
        </nav>
      </aside>

      <main className="main-shell">
        <header className="topbar">
          <div>
            <span className="eyebrow">{active.subtitle}</span>
            <h1>{active.title}</h1>
          </div>
          <div className="topbar-actions">
            <div className="env-pill">Mock API · /api/v1</div>
            <div className={`env-pill ${backendHealth.connected ? "backend-online" : "backend-offline"}`}>
              后端 {backendHealth.connected ? "已连接" : "未连接"} · {backendHealth.status}
            </div>
            <div className="role-pill">{active.role}</div>
          </div>
        </header>

        {portal === "user" ? (
          <UserPortal
            metrics={data.user.metrics}
            products={data.user.products}
            cartItems={data.user.cartItems}
            orders={data.user.orders}
            afterSales={data.user.afterSales}
          />
        ) : null}

        {portal === "merchant" ? (
          <MerchantPortal
            metrics={data.merchant.metrics}
            products={data.merchant.products}
            orders={data.merchant.orders}
            afterSales={data.merchant.afterSales}
            settlements={data.merchant.settlements}
          />
        ) : null}

        {portal === "admin" ? (
          <AdminPortal
            metrics={data.admin.metrics}
            merchants={data.admin.merchants}
            products={data.admin.products}
            orders={data.admin.orders}
            afterSales={data.admin.afterSales}
            settlements={data.admin.settlements}
            exceptions={data.admin.exceptions}
            operationLogs={data.admin.operationLogs}
          />
        ) : null}
      </main>
    </div>
  );
}
