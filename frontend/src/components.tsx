import { useCallback, useEffect, useRef, useState } from "react";
import { CheckCircle2, ChevronRight, Loader2, XCircle, AlertCircle, Info, X, Menu, LogOut, ShoppingCart } from "lucide-react";
import type { Metric, StatusTone } from "./types";

/* ── Status Tag ── */
export function StatusTag({ label, tone = "neutral" }: { label: string; tone?: StatusTone }) {
  return <span className={`status status-${tone}`}>{label}</span>;
}

/* ── Metric Grid ── */
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
export function Card({ children, className = "" }: { children: React.ReactNode; className?: string }) {
  return <section className={`panel ${className}`}>{children}</section>;
}

/* ── Toolbar ── */
export function Toolbar({ children }: { children: React.ReactNode }) {
  return <div className="toolbar">{children}</div>;
}

/* ── Search Input ── */
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
interface ButtonProps {
  children: React.ReactNode;
  onClick?: () => void;
  disabled?: boolean;
  loading?: boolean;
  className?: string;
  type?: "button" | "submit";
}

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
export function EmptyState({ text, icon }: { text: string; icon?: React.ReactNode }) {
  return (
    <div className="empty-cart">
      {icon ? <div style={{ marginBottom: 8, opacity: 0.4 }}>{icon}</div> : null}
      {text}
    </div>
  );
}

/* ── Loading Screen ── */
export function LoadingScreen({ text = "正在加载业务数据" }: { text?: string }) {
  return (
    <div className="loading-screen">
      <div className="spin" />
      <span>{text}</span>
    </div>
  );
}

/* ── Skeleton ── */
export function SkeletonCard() {
  return <div className="skeleton skeleton-card" />;
}

export function SkeletonMetric() {
  return <div className="skeleton skeleton-metric" />;
}

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
interface DataTableProps {
  columns: string[];
  rows: Array<Array<React.ReactNode>>;
  loading?: boolean;
  emptyText?: string;
  page?: number;
  total?: number;
  pageSize?: number;
  onPageChange?: (page: number) => void;
}

export function DataTable({ columns, rows, loading, emptyText = "暂无数据", page, total, pageSize = 10, onPageChange }: DataTableProps) {
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
export function Badge({ count, size = "small" }: { count: number; size?: "small" | "default" }) {
  if (count <= 0) return null;
  return (
    <span className="notification-badge" style={size === "default" ? { minWidth: 20, height: 20, fontSize: 11 } : undefined}>
      {count > 99 ? "99+" : count}
    </span>
  );
}

/* ── Confirm Action Modal ── */
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
type ToastType = "success" | "error" | "warning" | "info";

interface ToastItem {
  id: number;
  type: ToastType;
  message: string;
}

let toastId = 0;
let toastListeners: Array<(toasts: ToastItem[]) => void> = [];

export function toast(message: string, type: ToastType = "info") {
  const id = ++toastId;
  const item: ToastItem = { id, type, message };
  toastListeners.forEach(fn => fn([item]));
}

export function ToastContainer() {
  const [items, setItems] = useState<ToastItem[]>([]);

  useEffect(() => {
    const listener = (newItems: ToastItem[]) => {
      setItems(prev => [...prev, ...newItems]);
    };
    toastListeners.push(listener);
    return () => { toastListeners = toastListeners.filter(fn => fn !== listener); };
  }, []);

  const remove = useCallback((id: number) => {
    setItems(prev => prev.filter(item => item.id !== id));
  }, []);

  useEffect(() => {
    if (items.length === 0) return;
    const timer = setTimeout(() => remove(items[0].id), 3000);
    return () => clearTimeout(timer);
  }, [items, remove]);

  if (items.length === 0) return null;

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
          <button onClick={() => remove(item.id)} style={{ background: "none", border: "none", color: "var(--color-text-muted)", cursor: "pointer", padding: 4 }}>
            <X size={14} />
          </button>
        </div>
      ))}
    </div>
  );
}
