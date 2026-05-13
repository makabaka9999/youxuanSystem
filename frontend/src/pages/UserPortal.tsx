/**
 * 用户端门户页面（User Portal / H5 风格）
 *
 * 模拟用户端 H5 交易闭环的核心交互：
 * - 商品发现（商品卡片展示）
 * - 购物车管理（选择、数量调整、删除）
 * - 订单列表管理
 * - 售后进度跟进
 * - 用户端交易链路概览
 */
import { useEffect, useMemo, useState } from "react";
import { CreditCard, Minus, PackageCheck, Plus, RotateCcw, ShoppingBag, ShoppingCart, Trash2, UserRound } from "lucide-react";
import { afterSaleStatusMap, orderStatusMap, productStatusMap } from "../domain";
import { AmountText, Card, DataTable, MetricGrid, PrimaryButton, SectionHeader, StatusTag } from "../components";
import type { AfterSale, CartItem, Metric, Order, Product } from "../types";

/** 用户端门户页面组件属性 */
type UserPortalProps = {
  metrics: Metric[];
  products: Product[];
  cartItems: CartItem[];
  orders: Order[];
  afterSales: AfterSale[];
  onNavigate?: (target: string) => void;
};

/** 用户端门户页面组件 */
export function UserPortal({ metrics, products, cartItems, orders, afterSales, onNavigate }: UserPortalProps) {
  // 购物车数据（本地可编辑状态）
  const [editableCartItems, setEditableCartItems] = useState<CartItem[]>(cartItems);

  // 外部数据变化时同步到本地状态
  useEffect(() => {
    setEditableCartItems(cartItems);
  }, [cartItems]);

  // 计算已选商品的总金额
  const selectedTotal = useMemo(
    () =>
      editableCartItems
        .filter((item) => item.selected)
        .reduce((sum, item) => sum + Number(item.price) * item.quantity, 0)
        .toFixed(2),
    [editableCartItems]
  );

  // 计算已选商品的总件数
  const selectedCount = useMemo(
    () => editableCartItems.filter((item) => item.selected).reduce((sum, item) => sum + item.quantity, 0),
    [editableCartItems]
  );

  /** 购物车商品数量增减：数量归零则移除该商品 */
  const changeQuantity = (id: string, nextQuantity: number) => {
    setEditableCartItems((items) => {
      if (nextQuantity <= 0) {
        return items.filter((item) => item.id !== id);
      }
      return items.map((item) => (item.id === id ? { ...item, quantity: nextQuantity } : item));
    });
  };

  /** 切换商品的选中/取消状态 */
  const toggleSelected = (id: string) => {
    setEditableCartItems((items) => items.map((item) => (item.id === id ? { ...item, selected: !item.selected } : item)));
  };

  /** 从购物车中删除指定商品 */
  const removeCartItem = (id: string) => {
    setEditableCartItems((items) => items.filter((item) => item.id !== id));
  };

  return (
    <div className="portal-page user-page">
      {/* Hero 区域：用户端简介与待处理概要 */}
      <section id="user-home" className="hero user-hero">
        <div className="hero-copy">
          <span className="eyebrow">用户端 H5</span>
          <h1>从浏览到售后，一条链路完成交易闭环</h1>
          <p>商品、购物车、拆单结算、支付结果、物流和售后进度都在同一工作流里处理。</p>
          <div className="hero-actions">
            <button className="primary-button" type="button" onClick={() => onNavigate?.("products")}>
              <ShoppingBag size={16} />
              浏览商品
            </button>
            <button className="secondary-button" type="button" onClick={() => onNavigate?.("after-sales")}>
              <RotateCcw size={16} />
              查看售后
            </button>
          </div>
        </div>
        {/* 手机壳模拟 UI */}
        <div className="phone-shell">
          <div className="phone-top" />
          <div className="phone-card">
            <div className="phone-title">待处理</div>
            <div className="phone-row" style={{ cursor: "pointer" }} onClick={() => onNavigate?.("cart")}>
              <ShoppingCart size={18} />
              购物车已选 <strong style={{ color: "#fff", marginLeft: 4 }}>{selectedCount}</strong> 件
            </div>
            <div className="phone-row" style={{ cursor: "pointer" }} onClick={() => onNavigate?.("orders")}>
              <PackageCheck size={18} />
              1 个包裹运输中
            </div>
            <div className="phone-row" style={{ cursor: "pointer" }} onClick={() => onNavigate?.("after-sales")}>
              <RotateCcw size={18} />
              2 个售后正在处理
            </div>
          </div>
        </div>
      </section>

      {/* 关键指标卡片 */}
      <MetricGrid metrics={metrics} />

      <section className="content-grid two-col">
        {/* 商品发现区域 */}
        <Card id="user-products">
          <SectionHeader title="商品发现" description="来源：GET /api/v1/products" action="全部商品" />
          <div className="product-grid">
            {products.map((product) => (
              <article className="product-card" key={product.id}>
                <img src={product.image} alt={product.name} loading="lazy" />
                <div className="product-info">
                  <div className="product-name">{product.name}</div>
                  <div className="muted">{product.storeName}</div>
                  <div className="product-meta">
                    <AmountText value={product.price} />
                    <StatusTag {...productStatusMap[product.status]} />
                  </div>
                </div>
              </article>
            ))}
          </div>
        </Card>

        {/* 购物车区域：支持选择、数量增减、删除和拆单结算 */}
        <Card id="user-cart">
          <SectionHeader title="购物车拆单" description="来源：GET /api/v1/cart-items" action="去结算" />
          <div className="cart-list">
            {editableCartItems.map((item) => (
              <div className="cart-row" key={item.id}>
                {/* 选择按钮 */}
                <button
                  className={`check-dot ${item.selected ? "checked" : ""}`}
                  type="button"
                  aria-label={item.selected ? "取消选择商品" : "选择商品"}
                  onClick={() => toggleSelected(item.id)}
                />
                <div>
                  <strong>{item.productName}</strong>
                  <p>{item.storeName} · {item.skuName}</p>
                </div>
                <div className="cart-price">
                  <AmountText value={item.price} />
                  <div className="quantity-stepper" aria-label={`${item.productName} 数量`}>
                    <button type="button" aria-label="减少数量" onClick={() => changeQuantity(item.id, item.quantity - 1)}>
                      <Minus size={14} />
                    </button>
                    <span>{item.quantity}</span>
                    <button type="button" aria-label="增加数量" onClick={() => changeQuantity(item.id, item.quantity + 1)}>
                      <Plus size={14} />
                    </button>
                    <button className="remove-cart-button" type="button" aria-label="删除商品" onClick={() => removeCartItem(item.id)}>
                      <Trash2 size={14} />
                    </button>
                  </div>
                </div>
              </div>
            ))}
            {editableCartItems.length === 0 ? <div className="empty-cart">购物车暂无商品</div> : null}
          </div>
          {/* 结算栏：显示已选数量和总金额 */}
          <div className="settle-bar">
            <span>已选 <strong>{selectedCount}</strong> 件，按店铺拆分订单</span>
            <AmountText value={selectedTotal} />
          </div>
        </Card>
      </section>

      <section className="content-grid two-col">
        {/* 订单列表 */}
        <Card id="user-orders">
          <SectionHeader title="我的订单" description="来源：GET /api/v1/orders" action="订单列表" />
          <DataTable
            columns={["订单号", "店铺", "金额", "状态", "操作"]}
            rows={orders.map((order) => [
              <span className="mono">{order.orderNo}</span>,
              order.storeName,
              <AmountText value={order.amount} />,
              <StatusTag {...orderStatusMap[order.status]} />,
              <ActionByOrder status={order.status} />
            ])}
          />
        </Card>

        {/* 售后进度时间线 */}
        <Card id="user-after-sales">
          <SectionHeader title="售后进度" description="来源：GET /api/v1/after-sales/{id}" action="售后中心" />
          <div className="timeline-list">
            {afterSales.map((item) => (
              <div className="timeline-item" key={item.id}>
                <div className="timeline-dot" />
                <div>
                  <div className="timeline-title">
                    <span>{item.afterSaleNo}</span>
                    <StatusTag {...afterSaleStatusMap[item.status]} />
                  </div>
                  <p>{item.reason}</p>
                  <small>订单 {item.orderNo} · 截止 {item.deadline}</small>
                </div>
              </div>
            ))}
          </div>
        </Card>
      </section>

      {/* 用户端交易链路概览 */}
      <Card id="user-profile" className="flow-panel">
        <SectionHeader title="用户端交互链路" description="页面按钮只控制体验，最终状态以服务端响应为准" />
        <div className="flow-steps">
          {[
            ["注册登录", UserRound],
            ["浏览加购", ShoppingBag],
            ["提交订单", ShoppingCart],
            ["在线支付", CreditCard],
            ["确认收货", PackageCheck],
            ["售后退款", RotateCcw]
          ].map(([label, Icon]) => {
            const FlowIcon = Icon as typeof UserRound;
            return (
              <div className="flow-step" key={label as string}>
                <FlowIcon size={20} />
                <span>{label as string}</span>
              </div>
            );
          })}
        </div>
      </Card>
    </div>
  );
}

/** 根据订单状态显示对应的操作按钮 */
function ActionByOrder({ status }: { status: Order["status"] }) {
  if (status === "PENDING_PAYMENT") return <button className="link-button" type="button">去支付</button>;
  if (status === "PAID") return <button className="link-button" type="button">申请退款</button>;
  if (status === "SHIPPED") return <button className="link-button" type="button">确认收货</button>;
  if (status === "EXCEPTION") return <button className="link-button danger-text" type="button">查看异常</button>;
  return <button className="link-button" type="button">查看</button>;
}
