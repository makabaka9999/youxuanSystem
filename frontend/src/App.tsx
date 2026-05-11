/**
 * 应用根组件，负责：
 * - 认证状态管理（登录/登出）
 * - 三端门户切换（用户端/商家端/平台后台）
 * - 侧边栏导航与页面路由
 * - 后端健康状态检测
 */
import { useCallback, useEffect, useMemo, useState } from "react";
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

/** 应用全局数据：三个门户的首页数据聚合 */
type AppData = {
  /** 用户端数据 */
  user: Awaited<ReturnType<typeof api.getUserHome>>;
  /** 商家端数据 */
  merchant: Awaited<ReturnType<typeof api.getMerchantHome>>;
  /** 平台后台数据 */
  admin: Awaited<ReturnType<typeof api.getAdminHome>>;
};

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
  if (type === "MERCHANT_OWNER") return "merchant";
  return "admin";
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

/** 应用根组件 */
export function App() {
  // 认证状态：从 localStorage 恢复或为 null（未登录）
  const [auth, setAuth] = useState<AuthState | null>(loadAuth);
  // 当前门户端：用户端 / 商家端 / 平台后台
  const [portal, setPortal] = useState<Portal>(auth ? portalFromAuth(auth) : "admin");
  // 当前选中的导航项 ID
  const [activeNav, setActiveNav] = useState<string>("home");
  // 三个门户的首页聚合数据
  const [data, setData] = useState<AppData | null>(null);
  // 移动端侧边栏展开状态
  const [sidebarOpen, setSidebarOpen] = useState(false);
  // 后端服务健康检查状态
  const [backendHealth, setBackendHealth] = useState<BackendHealth>({ connected: false, status: "CHECKING" });

  /** 认证后加载三个门户的首页数据，同时检测后端健康状态 */
  useEffect(() => {
    if (!auth) return;
    Promise.all([api.getUserHome(), api.getMerchantHome(), api.getAdminHome()]).then(([user, merchant, admin]) => {
      setData({ user, merchant, admin });
    });
    fetchBackendHealth().then(setBackendHealth);
  }, [auth]);

  /** 登录成功回调：保存认证信息并跳转到对应门户 */
  const handleLoginSuccess = useCallback((newAuth: AuthState) => {
    setAuth(newAuth);
    const p = portalFromAuth(newAuth);
    setPortal(p);
    setActiveNav(portalMeta[p].nav[0].id);
  }, []);

  /** 登出处理：清除 localStorage 中的认证信息并重置状态 */
  const handleLogout = useCallback(() => {
    localStorage.removeItem(STORAGE_KEY);
    setAuth(null);
    setData(null);
  }, []);

  /** 切换门户端（用户端/商家端/平台后台），重置导航到首页 */
  const switchPortal = useCallback((p: Portal) => {
    setPortal(p);
    setActiveNav(portalMeta[p].nav[0].id);
  }, []);

  // 未登录时显示登录页面
  if (!auth) {
    return <LoginPage onLoginSuccess={handleLoginSuccess} />;
  }

  // 正在加载数据时显示 loading 骨架屏
  if (!data) return <LoadingScreen />;

  const active = portalMeta[portal];
  const nav = useMemo(() => active.nav, [active.nav]);

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
        {/* 左侧边栏：品牌标识、门户切换器、导航菜单、用户信息 */}
        <aside className={`sidebar ${sidebarOpen ? "sidebar-open" : ""}`}>
          <div className="brand">
            <div className="brand-mark">优</div>
            <div>
              <strong>优选</strong>
              <span>多商户电商平台</span>
            </div>
          </div>

          {/* 门户切换标签组 */}
          <div className="portal-switcher" role="tablist" aria-label="切换端">
            {(["user", "merchant", "admin"] as Portal[]).map((item) => (
              <button
                className={portal === item ? "active" : ""}
                key={item}
                type="button"
                onClick={() => { switchPortal(item); setSidebarOpen(false); }}
              >
                {portalMeta[item].title}
                <span style={{ marginLeft: 'auto', fontSize: 11, opacity: 0.4 }}>{portalMeta[item].role.slice(0, 4)}</span>
              </button>
            ))}
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
                  onClick={() => { setActiveNav(item.id); setSidebarOpen(false); }}
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
    </>
  );
}
