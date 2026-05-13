/**
 * 应用根组件，负责：
 * - 认证状态管理（登录/登出）
 * - 三端门户切换（用户端/商家端/平台后台）
 * - 侧边栏导航与页面路由
 * - 后端健康状态检测
 */
import { useCallback, useEffect, useState } from "react";
import {
  BadgeCheck,
  Banknote,
  ClipboardList,
  Home,
  LayoutDashboard,
  LogOut,
  Menu,
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
import { api } from "./api/backendApi";
import { LoadingScreen, ToastContainer } from "./components";
import { AdminPortal } from "./pages/AdminPortal";
import { LoginPage } from "./pages/LoginPage";
import { MerchantPortal } from "./pages/MerchantPortal";
import { UserPortal } from "./pages/UserPortal";
import type { AuthState, NavItem, Portal } from "./types";

// localStorage 中存储认证信息的键名
const STORAGE_KEY = "youxuan_auth";

/** 从 localStorage 中加载已保存的认证状态，实现页面刷新后保持登录态 */
function loadAuth(): AuthState | null {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) return null;
    const parsed = JSON.parse(raw) as AuthState;
    if (parsed && parsed.authenticated && parsed.accessToken) {
      return parsed;
    }
    return null;
  } catch {
    return null;
  }
}

/** 根据认证信息中的主体类型推断应跳转的门户端 */
function portalFromAuth(auth: AuthState): Portal {
  const type = auth.currentPrincipal?.principalType;
  if (type === "USER") return "user";
  if (type === "MERCHANT_STAFF") return "merchant";
  return "admin";
}

/** 根据商家员工角色类型过滤导航项 */
function filterMerchantNav(roleType: string | undefined) {
  const allNav = portalMeta.merchant.nav;
  if (roleType === "ADMIN" || roleType === "OWNER") return allNav;
  if (roleType === "CUSTOMER_SERVICE") {
    return allNav.filter(n => ["dashboard", "after-sales"].includes(n.id));
  }
  // OPERATOR 及默认：工作台、商品、订单、售后
  return allNav.filter(n => ["dashboard", "products", "orders", "after-sales"].includes(n.id));
}

/** 三端门户元数据配置：标题、副标题、角色及侧边栏导航项列表 */
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
    role: "MERCHANT_STAFF",
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

const featureTargets: Record<Portal, Record<string, string>> = {
  user: {
    home: "user-home",
    products: "user-products",
    cart: "user-cart",
    orders: "user-orders",
    "after-sales": "user-after-sales",
    profile: "user-profile"
  },
  merchant: {
    dashboard: "merchant-dashboard",
    store: "merchant-store",
    products: "merchant-products",
    orders: "merchant-orders",
    "after-sales": "merchant-after-sales",
    finance: "merchant-finance",
    staffs: "merchant-staffs"
  },
  admin: {
    dashboard: "admin-dashboard",
    "merchant-audit": "admin-merchant-audit",
    "product-audit": "admin-product-audit",
    exceptions: "admin-exceptions",
    settlement: "admin-settlement",
    logs: "admin-logs",
    settings: "admin-settings"
  }
};

function scrollToFeature(targetId: string) {
  window.requestAnimationFrame(() => {
    document.getElementById(targetId)?.scrollIntoView({ behavior: "smooth", block: "start" });
  });
}

/** 应用根组件 */
export function App() {
  // 认证状态：从 localStorage 恢复或为 null（未登录）
  const [auth, setAuth] = useState<AuthState | null>(loadAuth);
  // 根据认证信息决定当前门户端（不可手动切换）
  const portal: Portal = auth ? portalFromAuth(auth) : "admin";
  // 当前选中的导航项 ID（根据门户类型取第一个导航项）
  const [activeNav, setActiveNav] = useState<string>(
    portalMeta[auth ? portalFromAuth(auth) : "admin"].nav[0].id
  );
  // 当前门户的首页数据
  const [pageData, setPageData] = useState<any>(null);
  // 移动端侧边栏展开状态
  const [sidebarOpen, setSidebarOpen] = useState(false);
  // 后端服务健康检查状态
  const [backendHealth, setBackendHealth] = useState<BackendHealth>({ connected: false, status: "CHECKING" });

  /** 认证后加载对应门户的数据，同时检测后端健康状态 */
  useEffect(() => {
    if (!auth) return;
    const loaders: Record<Portal, () => Promise<any>> = {
      user: api.getUserHome,
      merchant: api.getMerchantHome,
      admin: api.getAdminHome,
    };
    loaders[portal]().then(setPageData);
    fetchBackendHealth().then(setBackendHealth);
  }, [auth, portal]);

  /** 登录成功回调：保存认证信息并跳转到对应门户 */
  const handleLoginSuccess = useCallback((newAuth: AuthState) => {
    setAuth(newAuth);
    setActiveNav(portalMeta[portalFromAuth(newAuth)].nav[0].id);
  }, []);

  /** 登出处理：清除 localStorage 中的认证信息并重置状态 */
  const handleLogout = useCallback(() => {
    localStorage.removeItem(STORAGE_KEY);
    setAuth(null);
    setPageData(null);
  }, []);

  const navigateToFeature = useCallback((itemId: string) => {
    setActiveNav(itemId);
    setSidebarOpen(false);
    scrollToFeature(featureTargets[portal][itemId] ?? featureTargets[portal][portalMeta[portal].nav[0].id]);
  }, [portal]);

  if (!auth) {
    return <LoginPage onLoginSuccess={handleLoginSuccess} />;
  }

  // 根据角色过滤导航
  const active = portalMeta[portal];
  const nav = portal === "merchant"
    ? filterMerchantNav(auth.currentPrincipal?.roleType)
    : active.nav;

  // 正在加载数据时显示 loading 骨架屏
  if (!pageData) return <LoadingScreen />;

  return (
    <>
      {/* 无障碍跳转链接：跳过导航直达主要内容 */}
      <a href="#main-content" className="skip-nav">跳转到主要内容</a>
      <ToastContainer />

      {/* 移动端菜单切换按钮 */}
      <button className="mobile-menu-toggle" type="button" onClick={() => setSidebarOpen(o => !o)} aria-label="切换菜单">
        <Menu size={18} />
        菜单
      </button>

      <div className="app-shell">
        {/* 左侧边栏：品牌标识、当前门户导航菜单、用户信息 */}
        <aside className={`sidebar ${sidebarOpen ? "sidebar-open" : ""}`}>
          <div className="brand">
            <div className="brand-mark">优</div>
            <div>
              <strong>优选</strong>
              <span>多商户电商平台</span>
            </div>
          </div>

          {/* 侧边栏主导航菜单 */}
          <nav className="side-nav" role="navigation" aria-label="主导航">
            {nav.map((item) => {
              const Icon = item.icon;
              return (
                <button
                  className={item.id === activeNav ? "active" : ""}
                  key={item.id}
                  type="button"
                  onClick={() => navigateToFeature(item.id)}
                >
                  <Icon size={18} />
                  {item.label}
                </button>
              );
            })}
          </nav>

          <div className="sidebar-spacer" />

          {/* 侧边栏底部用户信息与登出按钮 */}
          <div className="sidebar-user">
            <div className="sidebar-user-avatar">
              {auth.currentPrincipal?.displayName?.charAt(0) || "?"}
            </div>
            <div className="sidebar-user-info">
              <span className="sidebar-user-name">{auth.currentPrincipal?.displayName || "用户"}</span>
              <span className="sidebar-user-role">{active.role}</span>
            </div>
            <button
              type="button"
              className="sidebar-logout"
              aria-label="退出登录"
              onClick={handleLogout}
              title="退出登录"
            >
              <LogOut size={16} />
            </button>
          </div>
        </aside>

        {/* 主内容区域：顶部栏 + 对应门户页面 */}
        <main className="main-shell" id="main-content">
          {/* 顶部信息栏：门户标题、环境标识、后端连接状态、角色标识 */}
          <header className="topbar">
            <div>
              <span className="eyebrow">{active.subtitle}</span>
              <h1>{active.title}</h1>
            </div>
            <div className="topbar-actions">
              <div className="env-pill">Mock API · /api/v1</div>
              {/* 后端连接状态指示器 */}
              <div className={`env-pill ${backendHealth.connected ? "backend-online" : "backend-offline"}`}>
                后端 {backendHealth.connected ? "已连接" : "未连接"} · {backendHealth.status}
              </div>
              <div className="role-pill">{active.role}</div>
            </div>
          </header>

          {/* 根据当前门户端渲染对应的页面组件 */}
          {portal === "user" ? (
            <UserPortal
              metrics={pageData.metrics}
              products={pageData.products}
              cartItems={pageData.cartItems}
              orders={pageData.orders}
              afterSales={pageData.afterSales}
              onNavigate={navigateToFeature}
            />
          ) : null}

          {portal === "merchant" ? (
            <MerchantPortal
              metrics={pageData.metrics}
              products={pageData.products}
              orders={pageData.orders}
              afterSales={pageData.afterSales}
              settlements={pageData.settlements}
              onNavigate={navigateToFeature}
              visibleSections={nav.map(n => n.id)}
            />
          ) : null}

          {portal === "admin" ? (
            <AdminPortal
              metrics={pageData.metrics}
              merchants={pageData.merchants}
              products={pageData.products}
              orders={pageData.orders}
              afterSales={pageData.afterSales}
              settlements={pageData.settlements}
              exceptions={pageData.exceptions}
              operationLogs={pageData.operationLogs}
              onNavigate={navigateToFeature}
            />
          ) : null}
        </main>
      </div>
    </>
  );
}
