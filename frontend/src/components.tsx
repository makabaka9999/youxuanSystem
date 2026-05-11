/**
 * 共享 UI 组件库
 *
 * 包含整个前端通用的展示型组件：
 * - 状态标签（StatusTag）
 * - 指标卡片网格（MetricGrid）
 * - 区块头（SectionHeader）
 * - 面板容器（Card）
 * - 工具栏与搜索框（Toolbar / SearchInput）
 * - 按钮：主要 / 次要 / 危险（PrimaryButton / SecondaryButton / DangerButton）
 * - 空状态（EmptyState）
 * - 加载与骨架屏（LoadingScreen / SkeletonCard / SkeletonMetric / SkeletonText）
 * - 数据表格（DataTable）
 * - 金额文本（AmountText）
 * - 徽标（Badge）
 * - 确认弹窗（ConfirmAction）
 * - Toast 消息系统（toast / ToastContainer）
 */
import { useCallback, useEffect, useRef, useState } from "react";
import { CheckCircle2, ChevronRight, Loader2, XCircle, AlertCircle, Info, X, Menu, LogOut, ShoppingCart } from "lucide-react";
import type { Metric, StatusTone } from "./types";

/* ── Status Tag ── */
/** 状态标签组件：根据 tone 显示不同颜色的状态文字标签 */
export function StatusTag({ label, tone = "neutral" }: { label: string; tone?: StatusTone }) {
  return <span className={`status status-${tone}`}>{label}</span>;
}

/* ── Metric Grid ── */
/** 指标卡片网格：展示一组关键业务指标（如待支付订单数、可提现金额等） */
export function MetricGrid({ metrics }: { metrics: Metric[] }) {
  return (
    <section className="metric-grid">
      {metrics.map((metric) => (
        <article className={`metric-card metric-${metric.tone}`} key={metric.label}>
          <div className="metric-label">{metric.label}</div>
          <div className="metric-value">{metric.value}</div>
          <div className="metric-hint">{metric.hint}</div>
        </article>
      ))}
    </section>
  );
}

/* ── Section Header ── */
/** 区块标题组件：包含标题、描述和可选的右侧操作按钮（如"查看全部"） */
export function SectionHeader({
  title,
  description,
  action,
  onAction
}: {
  title: string;
  description?: string;
  action?: string;
  onAction?: () => void;
}) {
  return (
    <div className="section-header">
      <div>
        <h2>{title}</h2>
        {description ? <p>{description}</p> : null}
      </div>
      {action ? (
        <button className="ghost-button" type="button" onClick={onAction}>
          {action}
          <ChevronRight size={16} />
        </button>
      ) : null}
    </div>
  );
}

/* ── Card ── */
/** 通用面板容器：带圆角和背景的区块包裹器 */
export function Card({ children, className = "" }: { children: React.ReactNode; className?: string }) {
  return <section className={`panel ${className}`}>{children}</section>;
}

/* ── Toolbar ── */
/** 工具栏容器：用于放置搜索框、筛选按钮等操作控件 */
export function Toolbar({ children }: { children: React.ReactNode }) {
  return <div className="toolbar">{children}</div>;
}

/* ── Search Input ── */
/** 搜索输入框：受控组件，通过 onChange 回调返回输入值 */
export function SearchInput({ placeholder, value, onChange }: {
  placeholder: string;
  value?: string;
  onChange?: (value: string) => void;
}) {
  return (
    <input
      className="search-input"
      placeholder={placeholder}
      value={value}
      onChange={(e) => onChange?.(e.target.value)}
    />
  );
}

/* ── Buttons ── */
/** 按钮组件通用属性 */
interface ButtonProps {
  /** 按钮文本或图标 */
  children: React.ReactNode;
  /** 点击回调 */
  onClick?: () => void;
  /** 是否禁用 */
  disabled?: boolean;
  /** 是否显示加载中状态 */
  loading?: boolean;
  /** 额外的 CSS 类名 */
  className?: string;
  /** 按钮类型 */
  type?: "button" | "submit";
}

/** 主要按钮：带成功图标和加载旋转动画 */
export function PrimaryButton({ children, onClick, disabled, loading, className = "" }: ButtonProps) {
  return (
    <button
      className={`primary-button ${loading ? "loading" : ""} ${className}`}
      type="button"
      onClick={onClick}
      disabled={disabled || loading}
    >
      {loading ? <Loader2 size={16} className="spin" /> : <CheckCircle2 size={16} />}
      {children}
    </button>
  );
}

/** 次要按钮：朴素风格的轻量按钮 */
export function SecondaryButton({ children, onClick, disabled, className = "" }: ButtonProps) {
  return (
    <button
      className={`secondary-button compact ${className}`}
      type="button"
      onClick={onClick}
      disabled={disabled}
    >
      {children}
    </button>
  );
}

/** 危险操作按钮：红色警示风格，用于删除、驳回等高风险操作 */
export function DangerButton({ children, onClick, disabled, loading, className = "" }: ButtonProps) {
  return (
    <button
      className={`danger-button ${loading ? "loading" : ""} ${className}`}
      type="button"
      onClick={onClick}
      disabled={disabled || loading}
    >
      {loading ? <Loader2 size={16} className="spin" /> : null}
      {children}
    </button>
  );
}

/* ── Empty State ── */
/** 空状态占位：当列表/表格无数据时显示的提示信息 */
export function EmptyState({ text, icon }: { text: string; icon?: React.ReactNode }) {
  return (
    <div className="empty-cart">
      {icon ? <div style={{ marginBottom: 8, opacity: 0.4 }}>{icon}</div> : null}
      {text}
    </div>
  );
}

/* ── Loading Screen ── */
/** 全屏加载界面：带旋转动画和提示文字 */
export function LoadingScreen({ text = "正在加载业务数据" }: { text?: string }) {
  return (
    <div className="loading-screen">
      <div className="spin" />
      <span>{text}</span>
    </div>
  );
}

/* ── Skeleton ── */
/** 骨架屏-卡片占位 */
export function SkeletonCard() {
  return <div className="skeleton skeleton-card" />;
}

/** 骨架屏-指标卡片占位 */
export function SkeletonMetric() {
  return <div className="skeleton skeleton-metric" />;
}

/** 骨架屏-多行文字占位 */
export function SkeletonText({ lines = 3 }: { lines?: number }) {
  return (
    <div>
      <div className="skeleton skeleton-title" />
      {Array.from({ length: lines }).map((_, i) => (
        <div key={i} className="skeleton skeleton-text" />
      ))}
    </div>
  );
}

/* ── Data Table ── */
/** 数据表格组件属性 */
interface DataTableProps {
  /** 表头列名数组 */
  columns: string[];
  /** 表格行数据，每行为 ReactNode 数组 */
  rows: Array<Array<React.ReactNode>>;
  /** 是否加载中 */
  loading?: boolean;
  /** 空数据时的提示文字 */
  emptyText?: string;
  /** 当前页码 */
  page?: number;
  /** 数据总条数 */
  total?: number;
  /** 每页显示条数 */
  pageSize?: number;
  /** 翻页回调 */
  onPageChange?: (page: number) => void;
}

/** 通用数据表格组件：支持列配置、加载态、空态和分页 */
export function DataTable({ columns, rows, loading, emptyText = "暂无数据", page, total, pageSize = 10, onPageChange }: DataTableProps) {
  // 加载态：显示表头 + 骨架行
  if (loading) {
    return (
      <div className="table-wrap">
        <table>
          <thead><tr>{columns.map((c) => <th key={c}>{c}</th>)}</tr></thead>
        </table>
        <div style={{ padding: 24, display: "grid", gap: 12 }}>
          {Array.from({ length: 3 }).map((_, i) => (
            <div key={i} className="skeleton skeleton-text" style={{ height: 32 }} />
          ))}
        </div>
      </div>
    );
  }

  // 空态：显示空状态提示
  if (!rows.length) {
    return <EmptyState text={emptyText} />;
  }

  const totalPages = total ? Math.ceil(total / pageSize) : 0;

  return (
    <div>
      <div className="table-wrap">
        <table>
          <thead>
            <tr>
              {columns.map((column) => (
                <th key={column}>{column}</th>
              ))}
            </tr>
          </thead>
          <tbody>
            {rows.map((row, index) => (
              <tr key={index}>
                {row.map((cell, cellIndex) => (
                  <td key={cellIndex}>{cell}</td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      {/* 分页器：仅当总页数大于 1 时显示 */}
      {totalPages > 1 && onPageChange ? (
        <div className="pagination">
          <button disabled={page === 1} onClick={() => onPageChange(page! - 1)} className="page-btn">上一页</button>
          <span className="page-info">{page} / {totalPages} · 共 {total} 条</span>
          <button disabled={page === totalPages} onClick={() => onPageChange(page! + 1)} className="page-btn">下一页</button>
        </div>
      ) : null}
    </div>
  );
}

/* ── Amount Text ── */
/** 金额展示组件：支持自定义货币符号、文字大小和颜色主题 */
export function AmountText({ value, currency = "¥", size = "default", color = "normal" }: {
  value: string | number;
  currency?: string;
  size?: "small" | "default" | "large";
  color?: "normal" | "danger" | "success" | "warning";
}) {
  const sizeMap = { small: 14, default: 18, large: 24 };
  const colorMap = { normal: "var(--color-text)", danger: "var(--color-danger)", success: "var(--color-success)", warning: "var(--color-warning)" };
  return (
    <span style={{
      fontFamily: "var(--font-heading)",
      fontWeight: 700,
      fontSize: sizeMap[size],
      color: colorMap[color],
      letterSpacing: "-0.02em"
    }}>
      {currency}{Number(value).toFixed(2)}
    </span>
  );
}

/* ── Badge ── */
/** 通知徽标组件：显示未读数量，超过 99 显示 "99+" */
export function Badge({ count, size = "small" }: { count: number; size?: "small" | "default" }) {
  if (count <= 0) return null;
  return (
    <span className="notification-badge" style={size === "default" ? { minWidth: 20, height: 20, fontSize: 11 } : undefined}>
      {count > 99 ? "99+" : count}
    </span>
  );
}

/* ── Confirm Action Modal ── */
/** 确认操作弹窗：用于高风险操作前的二次确认，支持加载态和危险样式 */
export function ConfirmAction({ open, title, description, confirmText = "确定", cancelText = "取消", danger, loading, onConfirm, onCancel }: {
  open: boolean;
  title?: string;
  description: string;
  confirmText?: string;
  cancelText?: string;
  danger?: boolean;
  loading?: boolean;
  onConfirm: () => void;
  onCancel: () => void;
}) {
  if (!open) return null;

  return (
    // 点击遮罩层可关闭弹窗
    <div className="modal-overlay" onClick={onCancel}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <h3>{title || "确认操作"}</h3>
        <p>{description}</p>
        <div className="modal-actions">
          <button className="modal-cancel" onClick={onCancel} disabled={loading}>{cancelText}</button>
          <button className={`modal-confirm ${danger ? "danger" : ""}`} onClick={onConfirm} disabled={loading}>
            {loading ? <Loader2 size={16} className="spin" style={{ verticalAlign: "middle", marginRight: 6 }} /> : null}
            {confirmText}
          </button>
        </div>
      </div>
    </div>
  );
}

/* ── Toast System ── */
/** Toast 消息类型：成功 / 错误 / 警告 / 信息 */
type ToastType = "success" | "error" | "warning" | "info";

/** Toast 消息项 */
interface ToastItem {
  id: number;
  type: ToastType;
  message: string;
}

// Toast ID 自增计数器
let toastId = 0;
// Toast 监听器列表（跨组件通信）
let toastListeners: Array<(toasts: ToastItem[]) => void> = [];

/** 全局 Toast 触发函数：在任意位置调用以显示提示消息 */
export function toast(message: string, type: ToastType = "info") {
  const id = ++toastId;
  const item: ToastItem = { id, type, message };
  toastListeners.forEach(fn => fn([item]));
}

/** Toast 消息容器组件：监听 toast 调用并自动渲染/移除消息 */
export function ToastContainer() {
  const [items, setItems] = useState<ToastItem[]>([]);

  // 注册监听器，接收新的 toast 消息
  useEffect(() => {
    const listener = (newItems: ToastItem[]) => {
      setItems(prev => [...prev, ...newItems]);
    };
    toastListeners.push(listener);
    return () => { toastListeners = toastListeners.filter(fn => fn !== listener); };
  }, []);

  /** 移除指定 ID 的 toast */
  const remove = useCallback((id: number) => {
    setItems(prev => prev.filter(item => item.id !== id));
  }, []);

  // 自动移除：3 秒后移除最早的一条 toast
  useEffect(() => {
    if (items.length === 0) return;
    const timer = setTimeout(() => remove(items[0].id), 3000);
    return () => clearTimeout(timer);
  }, [items, remove]);

  if (items.length === 0) return null;

  // 每种类型对应的图标
  const icons = {
    success: <CheckCircle2 size={18} className="toast-icon-success" />,
    error: <XCircle size={18} className="toast-icon-error" />,
    warning: <AlertCircle size={18} className="toast-icon-warning" />,
    info: <Info size={18} className="toast-icon-info" />
  };

  return (
    <div className="toast-container">
      {items.map(item => (
        <div key={item.id} className={`toast toast-${item.type}`}>
          {icons[item.type]}
          <span style={{ flex: 1 }}>{item.message}</span>
          {/* 手动关闭按钮 */}
          <button onClick={() => remove(item.id)} style={{ background: "none", border: "none", color: "var(--color-text-muted)", cursor: "pointer", padding: 4 }}>
            <X size={14} />
          </button>
        </div>
      ))}
    </div>
  );
}
