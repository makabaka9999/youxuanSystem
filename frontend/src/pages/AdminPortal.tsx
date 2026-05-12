/**
 * 平台后台门户页面（Admin Portal / Web 风格）
 *
 * 提供平台运营的核心管理功能：
 * - 商家入驻审核队列
 * - 商品审核队列
 * - 异常处理池（支付、退款、对账差异）
 * - 结算与提现审核
 * - 订单与售后监控
 * - 操作审计日志
 */
import { AlertTriangle, BadgeCheck, FileClock, Landmark, ListChecks, ScrollText, ShieldAlert } from "lucide-react";
import {
  afterSaleStatusMap,
  merchantAuditMap,
  merchantStatusMap,
  orderStatusMap,
  productStatusMap,
  settlementStatusMap
} from "../domain";
import { AmountText, Card, DataTable, MetricGrid, SectionHeader, StatusTag, Toolbar, SearchInput } from "../components";
import type { AfterSale, ExceptionRecord, Merchant, Metric, OperationLog, Order, Product, Settlement } from "../types";

/** 平台后台门户页面组件属性 */
type AdminPortalProps = {
  metrics: Metric[];
  merchants: Merchant[];
  products: Product[];
  orders: Order[];
  afterSales: AfterSale[];
  settlements: Settlement[];
  exceptions: ExceptionRecord[];
  operationLogs: OperationLog[];
  onNavigate?: (target: string) => void;
};

/** 平台后台门户页面组件 */
export function AdminPortal({
  metrics,
  merchants,
  products,
  orders,
  afterSales,
  settlements,
  exceptions,
  operationLogs,
  onNavigate
}: AdminPortalProps) {
  return (
    <div className="portal-page admin-page">
      {/* Hero 区域：平台后台简介与风险概览看板 */}
      <section id="admin-dashboard" className="hero admin-hero">
        <div className="hero-copy">
          <span className="eyebrow">平台管理后台</span>
          <h1>审核、异常、财务和审计都能被平台看见</h1>
          <p>后台以队列和池子驱动，保障支付异常、退款失败、对账差异不会被静默吞掉。</p>
          <div className="hero-actions">
            <button className="primary-button" type="button" onClick={() => onNavigate?.("settlement")}>
              <BadgeCheck size={16} />
              审核结算单
            </button>
            <button className="danger-button" type="button" onClick={() => onNavigate?.("exceptions")}>
              <ShieldAlert size={16} />
              处理异常池
            </button>
          </div>
        </div>
        {/* 风险看板：关键异常指标速览 */}
        <div className="risk-board">
          <div className="risk-row">
            <AlertTriangle size={18} />
            <span>支付成功但订单取消</span>
            <strong>3</strong>
          </div>
          <div className="risk-row">
            <ShieldAlert size={18} />
            <span>退款失败待重试</span>
            <strong>2</strong>
          </div>
          <div className="risk-row">
            <Landmark size={18} />
            <span>对账差异待处理</span>
            <strong>4</strong>
          </div>
        </div>
      </section>

      <MetricGrid metrics={metrics} />

      <section className="content-grid two-col">
        {/* 商家入驻审核队列 */}
        <Card id="admin-merchant-audit">
          <SectionHeader title="商家审核队列" description="来源：GET /api/v1/admin/merchant-applications" action="审核列表" />
          <Toolbar>
            <SearchInput placeholder="搜索商家 / 主体 / 联系人" />
            <button className="secondary-button compact" type="button">待审核</button>
          </Toolbar>
          <DataTable
            columns={["店铺", "主体", "类目", "审核", "状态", "操作"]}
            rows={merchants.map((merchant) => [
              merchant.name,
              merchant.companyName,
              merchant.category,
              <StatusTag {...merchantAuditMap[merchant.auditStatus]} />,
              <StatusTag {...merchantStatusMap[merchant.status]} />,
              <button className="link-button">{merchant.auditStatus === "PENDING" ? "审核" : "详情"}</button>
            ])}
          />
        </Card>

        {/* 商品审核队列 */}
        <Card id="admin-product-audit">
          <SectionHeader title="商品审核队列" description="来源：GET /api/v1/admin/product-audits" action="商品审核" />
          <DataTable
            columns={["商品", "商家", "类目", "库存", "状态", "操作"]}
            rows={products.map((product) => [
              <div className="table-product"><img src={product.image} alt="" /><span>{product.name}</span></div>,
              product.storeName,
              product.category,
              product.stock,
              <StatusTag {...productStatusMap[product.status]} />,
              <button className="link-button">{product.status === "AUDITING" ? "审核" : "查看"}</button>
            ])}
          />
        </Card>
      </section>

      <section className="content-grid two-col">
        {/* 异常处理池：展示各类待处理/处理中/已解决的异常记录 */}
        <Card id="admin-exceptions">
          <SectionHeader title="异常处理池" description="来源：GET /api/v1/admin/exception-orders" action="全部异常" />
          <div className="exception-list">
            {exceptions.map((item) => (
              <article className="exception-card" key={item.id}>
                <div className="exception-icon"><AlertTriangle size={18} /></div>
                <div>
                  <strong>{item.title}</strong>
                  <p>{item.type} · {item.relatedNo} · <AmountText value={item.amount} /></p>
                  <small>{item.createdAt}</small>
                </div>
                {/* 异常状态标签：待处理(红) / 处理中(黄) / 已解决(绿) */}
                <StatusTag label={item.status === "RESOLVED" ? "已处理" : item.status === "PROCESSING" ? "处理中" : "待处理"} tone={item.status === "RESOLVED" ? "success" : item.status === "PROCESSING" ? "warning" : "danger"} />
              </article>
            ))}
          </div>
        </Card>

        {/* 结算与提现审核 */}
        <Card id="admin-settlement">
          <SectionHeader title="结算与提现审核" description="来源：GET /api/v1/admin/settlement-orders" action="财务审核" />
          <DataTable
            columns={["结算单", "商家", "周期", "应结金额", "状态", "操作"]}
            rows={settlements.map((settlement) => [
              <span className="mono">{settlement.settlementNo}</span>,
              settlement.merchantName,
              settlement.period,
              <AmountText value={settlement.payableAmount} />,
              <StatusTag {...settlementStatusMap[settlement.status]} />,
              <button className="link-button">{settlement.status === "PENDING_AUDIT" ? "审核" : "详情"}</button>
            ])}
          />
        </Card>
      </section>

      <section className="content-grid two-col">
        {/* 订单与售后监控：平台视角的订单列表和售后介入入口 */}
        <Card id="admin-monitoring">
          <SectionHeader title="订单与售后监控" description="来源：平台订单、售后介入接口" action="运营监控" />
          <DataTable
            columns={["业务单号", "对象", "金额", "状态", "入口"]}
            rows={[
              // 前 3 条待处理的订单
              ...orders.slice(0, 3).map((order) => [
                <span className="mono">{order.orderNo}</span>,
                order.storeName,
                <AmountText value={order.amount} />,
                <StatusTag {...orderStatusMap[order.status]} />,
                <button className="link-button">订单详情</button>
              ]),
              // 所有售后单（平台介入入口）
              ...afterSales.map((item) => [
                <span className="mono">{item.afterSaleNo}</span>,
                item.storeName,
                <AmountText value={item.amount} />,
                <StatusTag {...afterSaleStatusMap[item.status]} />,
                <button className="link-button">介入处理</button>
              ])
            ]}
          />
        </Card>

        {/* 操作审计日志时间线 */}
        <Card id="admin-logs">
          <SectionHeader title="操作审计" description="来源：GET /api/v1/admin/operation-logs" action="日志查询" />
          <div className="audit-list">
            {operationLogs.map((log) => (
              <div className="audit-row" key={log.id}>
                <ScrollText size={16} />
                <div>
                  <strong>{log.module} · {log.action}</strong>
                  <p>{log.operator} 处理 {log.target}</p>
                </div>
                <small>{log.createdAt}</small>
              </div>
            ))}
          </div>
        </Card>
      </section>

      {/* 后台核心队列能力概览 */}
      <Card id="admin-settings" className="flow-panel">
        <SectionHeader title="后台核心队列" description="所有高风险动作记录操作人、IP、前后状态和 requestId" />
        <div className="capability-grid admin-capability">
          {[
            ["审核队列", BadgeCheck, "商家入驻、商品上架、资质过期"],
            ["异常订单池", AlertTriangle, "支付成功订单异常、库存异常、竞态异常"],
            ["退款异常池", ShieldAlert, "退款失败重试、人工确认、渠道凭证"],
            ["对账差异池", FileClock, "渠道金额、平台金额、差异金额处理"],
            ["结算提现", Landmark, "结算审核、提现审核、打款状态维护"],
            ["操作审计", ListChecks, "审核、冻结、退款、结算、提现日志"]
          ].map(([title, Icon, desc]) => {
            const CapabilityIcon = Icon as typeof BadgeCheck;
            return (
              <div className="capability-card" key={title as string}>
                <CapabilityIcon size={20} />
                <strong>{title as string}</strong>
                <p>{desc as string}</p>
              </div>
            );
          })}
        </div>
      </Card>
    </div>
  );
}
