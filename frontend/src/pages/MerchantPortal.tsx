/**
 * 商家端门户页面（Merchant Portal / Web 风格）
 *
 * 提供商家店铺经营的一站式工作台：
 * - 商品管理（上下架、审核状态查看）
 * - 订单履约（发货处理）
 * - 售后处理（超时提醒）
 * - 账单与结算提现
 * - 员工权限概览
 */
import { Banknote, Boxes, ClipboardCheck, PackageOpen, ShieldCheck, Truck, UsersRound } from "lucide-react";
import { afterSaleStatusMap, orderStatusMap, productStatusMap, settlementStatusMap } from "../domain";
import { AmountText, Card, DataTable, MetricGrid, SectionHeader, StatusTag, Toolbar, SearchInput } from "../components";
import type { AfterSale, Metric, Order, Product, Settlement } from "../types";

/** 商家端门户页面组件属性 */
type MerchantPortalProps = {
  metrics: Metric[];
  products: Product[];
  orders: Order[];
  afterSales: AfterSale[];
  settlements: Settlement[];
};

/** 商家端门户页面组件 */
export function MerchantPortal({ metrics, products, orders, afterSales, settlements }: MerchantPortalProps) {
  return (
    <div className="portal-page merchant-page">
      {/* Hero 区域：商家端简介与经营概要 */}
      <section className="hero merchant-hero">
        <div className="hero-copy">
          <span className="eyebrow">商家端 Web</span>
          <h1>商品、履约、售后、资金都围绕店铺工作台展开</h1>
          <p>冻结状态下仍保留已支付订单履约能力，财务动作和员工权限独立管控。</p>
          <div className="hero-actions">
            <button className="primary-button" type="button">
              <Boxes size={16} />
              发布商品
            </button>
            <button className="secondary-button" type="button">
              <Truck size={16} />
              处理发货
            </button>
          </div>
        </div>
        {/* 商家经营概要卡片 */}
        <div className="merchant-summary">
          <div>
            <span>店铺状态</span>
            <strong>正常经营</strong>
          </div>
          <div>
            <span>可提现余额</span>
            <strong>¥45,189.35</strong>
          </div>
          <div>
            <span>员工账号</span>
            <strong>老板 + 6 员工</strong>
          </div>
        </div>
      </section>

      <MetricGrid metrics={metrics} />

      <section className="content-grid two-col">
        {/* 商品管理：搜索、筛选、表格展示 */}
        <Card>
          <SectionHeader title="商品管理" description="来源：GET /api/v1/merchant/products" action="商品列表" />
          <Toolbar>
            <SearchInput placeholder="搜索商品名称 / 类目" />
            <button className="secondary-button compact" type="button">上架状态</button>
            <button className="secondary-button compact" type="button">审核状态</button>
          </Toolbar>
          <DataTable
            columns={["商品", "类目", "库存", "销量", "状态", "操作"]}
            rows={products.map((product) => [
              <div className="table-product"><img src={product.image} alt="" /><span>{product.name}</span></div>,
              product.category,
              product.stock,
              product.sales,
              <StatusTag {...productStatusMap[product.status]} />,
              <button className="link-button">{product.status === "AUDITING" ? "查看审核" : "编辑"}</button>
            ])}
          />
        </Card>

        {/* 订单履约：商家发货操作 */}
        <Card>
          <SectionHeader title="订单履约" description="来源：GET /api/v1/merchant/orders" action="订单管理" />
          <DataTable
            columns={["订单号", "买家", "金额", "状态", "动作"]}
            rows={orders.map((order) => [
              <span className="mono">{order.orderNo}</span>,
              order.userName,
              <AmountText value={order.amount} />,
              <StatusTag {...orderStatusMap[order.status]} />,
              <MerchantOrderAction status={order.status} />
            ])}
          />
        </Card>
      </section>

      <section className="content-grid two-col">
        {/* 售后处理任务卡片列表 */}
        <Card>
          <SectionHeader title="售后处理" description="来源：GET /api/v1/merchant/after-sales" action="售后列表" />
          <div className="task-list">
            {afterSales.map((item) => (
              <article className="task-card" key={item.id}>
                <div>
                  <strong>{item.afterSaleNo}</strong>
                  <p>{item.reason}</p>
                  <small>截止 {item.deadline}</small>
                </div>
                <div className="task-actions">
                  <StatusTag {...afterSaleStatusMap[item.status]} />
                  <button className="link-button">处理</button>
                </div>
              </article>
            ))}
          </div>
        </Card>

        {/* 账单与结算提现 */}
        <Card>
          <SectionHeader title="账单与提现" description="来源：GET /api/v1/merchant/settlements" action="财务中心" />
          <DataTable
            columns={["结算单", "周期", "应结金额", "状态", "操作"]}
            rows={settlements.map((settlement) => [
              <span className="mono">{settlement.settlementNo}</span>,
              settlement.period,
              <AmountText value={settlement.payableAmount} />,
              <StatusTag {...settlementStatusMap[settlement.status]} />,
              <button className="link-button">{settlement.status === "APPROVED" ? "申请提现" : "查看"}</button>
            ])}
          />
        </Card>
      </section>

      {/* 商家端能力组件概览 */}
      <Card className="flow-panel">
        <SectionHeader title="商家端组件能力" description="老板账号全量，员工账号按菜单权限控制" />
        <div className="capability-grid">
          {[
            ["入驻审核", ShieldCheck, "提交资质、查看驳回原因、重新提交"],
            ["商品维护", Boxes, "SKU、价格、库存、上下架、审核状态"],
            ["订单发货", Truck, "物流公司、运单号、操作人和时间"],
            ["售后处理", ClipboardCheck, "同意、拒绝、退货退款、超时提醒"],
            ["资金提现", Banknote, "账单、结算单、可提现和冻结金额"],
            ["员工权限", UsersRound, "老板账号和员工账号两级菜单权限"]
          ].map(([title, Icon, desc]) => {
            const CapabilityIcon = Icon as typeof ShieldCheck;
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

/** 根据订单状态显示商家端对应的操作按钮（发货 / 查看异常 / 详情） */
function MerchantOrderAction({ status }: { status: Order["status"] }) {
  if (status === "PAID") {
    return (
      <button className="link-button">
        <PackageOpen size={14} />
        发货
      </button>
    );
  }
  if (status === "EXCEPTION") return <button className="link-button danger-text">查看异常</button>;
  return <button className="link-button">详情</button>;
}
