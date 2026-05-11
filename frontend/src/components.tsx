import type { LucideIcon } from "lucide-react";
import { Activity, AlertTriangle, ArrowRight, CheckCircle2, ChevronRight, CreditCard, Loader2, PackageCheck, RefreshCcw, ShoppingBag, ShoppingCart, UserRound } from "lucide-react";
import type { Metric, StatusTone } from "./types";

const iconMap: Record<string, LucideIcon> = {
  Activity, AlertTriangle, CheckCircle2, CreditCard, PackageCheck,
  RefreshCcw, ShoppingBag, ShoppingCart, UserRound
};

export function StatusTag({ label, tone = "neutral" }: { label: string; tone?: StatusTone }) {
  return <span className={`status status-${tone}`}>{label}</span>;
}

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

export function SectionHeader({
  title,
  description,
  action
}: {
  title: string;
  description?: string;
  action?: string;
}) {
  return (
    <div className="section-header">
      <div>
        <h2>{title}</h2>
        {description ? <p>{description}</p> : null}
      </div>
      {action ? (
        <button className="ghost-button" type="button">
          {action}
          <ChevronRight size={16} />
        </button>
      ) : null}
    </div>
  );
}

export function Card({ children, className = "" }: { children: React.ReactNode; className?: string }) {
  return <section className={`panel ${className}`}>{children}</section>;
}

export function Toolbar({ children }: { children: React.ReactNode }) {
  return <div className="toolbar">{children}</div>;
}

export function SearchInput({ placeholder }: { placeholder: string }) {
  return <input className="search-input" placeholder={placeholder} />;
}

export function PrimaryButton({ children }: { children: React.ReactNode }) {
  return (
    <button className="primary-button" type="button">
      <CheckCircle2 size={16} />
      {children}
    </button>
  );
}

export function DangerButton({ children }: { children: React.ReactNode }) {
  return (
    <button className="danger-button" type="button">
      {children}
    </button>
  );
}

export function EmptyState({ text }: { text: string }) {
  return <div className="empty-cart">{text}</div>;
}

export function LoadingScreen() {
  return (
    <div className="loading-screen">
      <div className="spin" />
      <span>正在加载业务数据</span>
    </div>
  );
}

export function DataTable({
  columns,
  rows
}: {
  columns: string[];
  rows: Array<Array<React.ReactNode>>;
}) {
  return (
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
  );
}
