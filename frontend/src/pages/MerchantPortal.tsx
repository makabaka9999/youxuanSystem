import { Banknote, Boxes, ClipboardCheck, PackageOpen, ShieldCheck, Truck, UsersRound } from "lucide-react";
import { afterSaleStatusMap, orderStatusMap, productStatusMap, settlementStatusMap } from "../domain";
import { Card, DataTable, MetricGrid, PrimaryButton, SectionHeader, StatusTag, Toolbar, SearchInput } from "../components";
import type { AfterSale, Metric, Order, Product, Settlement } from "../types";

type MerchantPortalProps = {
  metrics: Metric[];
  products: Product[];
  orders: Order[];
  afterSales: AfterSale[];
  settlements: Settlement[];
};

export function MerchantPortal({ metrics, products, orders, afterSales, settlements }: MerchantPortalProps) {
  return (
    <div className="portal-page merchant-page">
      <section className="hero merchant-hero">
        <div className="hero-copy">
          <span className="eyebrow">商家端 Web</span>
          <h1>商品、履约、售后、资金都围绕店铺工作台展开</h1>
          <p>冻结状态下仍保留已支付订单履约能力，财务动作和员工权限独立管控。</p>
          <div className="hero-actions">
            <PrimaryButton>发布商品</PrimaryButton>
            <button className="secondary-button" type="button">处理发货</button>
          </div>
        </div>
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

        <Card>
          <SectionHeader title="订单履约" description="来源：GET /api/v1/merchant/orders" action="订单管理" />
          <DataTable
            columns={["订单号", "买家", "金额", "状态", "动作"]}
            rows={orders.map((order) => [
              <span className="mono">{order.orderNo}</span>,
              order.userName,
              `¥${order.amount}`,
              <StatusTag {...orderStatusMap[order.status]} />,
              <MerchantOrderAction status={order.status} />
            ])}
          />
        </Card>
      </section>

      <section className="content-grid two-col">
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

        <Card>
          <SectionHeader title="账单与提现" description="来源：GET /api/v1/merchant/settlements" action="财务中心" />
          <DataTable
            columns={["结算单", "周期", "应结金额", "状态", "操作"]}
            rows={settlements.map((settlement) => [
              <span className="mono">{settlement.settlementNo}</span>,
              settlement.period,
              `¥${settlement.payableAmount}`,
              <StatusTag {...settlementStatusMap[settlement.status]} />,
              <button className="link-button">{settlement.status === "APPROVED" ? "申请提现" : "查看"}</button>
            ])}
          />
        </Card>
      </section>

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
