/**
 * 商家端门户页面（Merchant Portal / Web 风格）
 *
 * 提供商家店铺经营的一站式工作台：
 * - 商品管理（上下架、审核状态查看）
 * - 订单履约（发货处理）
 * - 售后处理（超时提醒）
 * - 账单与结算提现
 * - 员工管理（RBAC 角色关联）
 */
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import {
  Banknote,
  Boxes,
  ClipboardCheck,
  PackageOpen,
  ShieldCheck,
  Truck,
  UserPlus,
  UsersRound,
  X,
  Search,
  Loader2,
  Check,
  Plus,
} from "lucide-react";
import { afterSaleStatusMap, orderStatusMap, productStatusMap, settlementStatusMap } from "../domain";
import { AmountText, Card, DataTable, MetricGrid, SectionHeader, StatusTag, Toolbar, SearchInput } from "../components";
import type { AfterSale, Metric, Order, Product, Settlement, Staff } from "../types";
import { api } from "../api/backendApi";

/** 商家端门户页面组件属性 */
type MerchantPortalProps = {
  metrics: Metric[];
  products: Product[];
  orders: Order[];
  afterSales: AfterSale[];
  settlements: Settlement[];
  onNavigate?: (target: string) => void;
  /** 可见功能区域 ID 列表，来自角色权限控制 */
  visibleSections?: string[];
};

/** 商家端门户页面组件 */
export function MerchantPortal({ metrics, products, orders, afterSales, settlements, onNavigate, visibleSections }: MerchantPortalProps) {
  const [displayProducts, setDisplayProducts] = useState<Product[]>(products);
  const [displayMetrics, setDisplayMetrics] = useState<Metric[]>(metrics);
  const [searchKeyword, setSearchKeyword] = useState("");
  const [productPage, setProductPage] = useState(1);
  const [productTotal, setProductTotal] = useState(0);
  const [productLoading, setProductLoading] = useState(false);
  const productPageSize = 20;
  const withdrawableAmount = useMemo(
    () => displayMetrics.find((m) => m.label === "可提现余额")?.value ?? "0.00",
    [displayMetrics]
  );
  // ── 员工管理状态 ──
  const [staffList, setStaffList] = useState<Staff[]>([]);
  const [staffLoading, setStaffLoading] = useState(false);
  const [staffSearch, setStaffSearch] = useState("");
  const [staffPage, setStaffPage] = useState(1);
  const pageSize = 8;
  const [showAddModal, setShowAddModal] = useState(false);
  const [addMobile, setAddMobile] = useState("");
  const [addStaffName, setAddStaffName] = useState("");
  const [addRoleTypes, setAddRoleTypes] = useState<string[]>(["OPERATOR"]);
  const [lookupResult, setLookupResult] = useState<{ id: string; mobile: string; nickname: string } | null>(null);
  const [lookupLoading, setLookupLoading] = useState(false);
  const [lookupError, setLookupError] = useState("");
  const [submitLoading, setSubmitLoading] = useState(false);
  const [staffError, setStaffError] = useState("");
  // ── 编辑员工状态 ──
  const [editStaff, setEditStaff] = useState<Staff | null>(null);
  const [editStaffName, setEditStaffName] = useState("");
  const [editRoleTypes, setEditRoleTypes] = useState<string[]>([]);

  // ── 发布商品状态 ──
  const [showProductModal, setShowProductModal] = useState(false);
  const [prodName, setProdName] = useState("");
  const [prodParentId, setProdParentId] = useState<number>(0);
  const [prodCategoryId, setProdCategoryId] = useState<number>(0);
  const [prodPrice, setProdPrice] = useState("");
  const [prodStock, setProdStock] = useState("");
  const [prodImage, setProdImage] = useState("");
  const [prodImagePreview, setProdImagePreview] = useState("");
  const [prodDesc, setProdDesc] = useState("");
  const [prodCategoryTree, setProdCategoryTree] = useState<any[]>([]);
  const [prodCategoryLoading, setProdCategoryLoading] = useState(false);
  const [prodCategoryError, setProdCategoryError] = useState("");
  const [prodSubmitting, setProdSubmitting] = useState(false);
  const [prodError, setProdError] = useState("");
  const [prodImageUploading, setProdImageUploading] = useState(false);
  const prodImagePreviewRef = useRef<string | null>(null);

  /** 从后端加载商品列表（支持搜索和分页） */
  const loadProducts = useCallback(async (keyword: string, page: number) => {
    setProductLoading(true);
    try {
      const result = await api.fetchMerchantProducts(keyword || undefined, page, productPageSize);
      setDisplayProducts(result.items);
      setProductTotal(result.total);
      setProductPage(page);
    } catch {
      setDisplayProducts([]);
      setProductTotal(0);
    } finally {
      setProductLoading(false);
    }
  }, []);

  /** 按名称搜索 */
  const handleProductSearch = useCallback(() => {
    loadProducts(searchKeyword, 1);
  }, [loadProducts, searchKeyword]);

  /** 翻页 */
  const handleProductPageChange = useCallback((page: number) => {
    loadProducts(searchKeyword, page);
  }, [loadProducts, searchKeyword]);

  useEffect(() => {
    setDisplayMetrics(metrics);
  }, [metrics]);

  /** 根据选中的父类目计算可用子类目 */
  const prodChildCategories = useMemo(() => {
    if (!prodParentId) return [];
    const parent = prodCategoryTree.find(c => c.id === prodParentId);
    return parent?.children || [];
  }, [prodParentId, prodCategoryTree]);

  /** 分页后的员工列表 */
  const pagedStaff = useMemo(() => {
    const start = (staffPage - 1) * pageSize;
    return staffList.slice(start, start + pageSize);
  }, [staffList, staffPage]);

  const totalPages = Math.ceil(staffList.length / pageSize) || 1;

  /** 搜索去抖计时器 */
  const searchTimerRef = useRef<ReturnType<typeof setTimeout> | null>(null);

  /** 加载员工列表（带搜索关键词） */
  const loadStaff = useCallback(async (keyword?: string) => {
    setStaffLoading(true);
    const list = await api.fetchStaffList(keyword || undefined);
    if (list) setStaffList(list);
    setStaffLoading(false);
  }, []);

  /** 初始加载 */
  useEffect(() => { loadStaff(); }, [loadStaff]);

  /** 搜索输入时 debounce 300ms 后查询后端 */
  const handleSearchChange = useCallback((value: string) => {
    setStaffSearch(value);
    setStaffPage(1);
    if (searchTimerRef.current) clearTimeout(searchTimerRef.current);
    searchTimerRef.current = setTimeout(() => {
      loadStaff(value.trim() || undefined);
    }, 300);
  }, [loadStaff]);

  /** 组件卸载时清理计时器 */
  useEffect(() => {
    return () => { if (searchTimerRef.current) clearTimeout(searchTimerRef.current); };
  }, []);

  /** 清理商品主图的本地预览地址，避免重复上传时泄漏对象 URL */
  const clearImagePreview = useCallback(() => {
    if (prodImagePreviewRef.current) {
      URL.revokeObjectURL(prodImagePreviewRef.current);
      prodImagePreviewRef.current = null;
    }
    setProdImagePreview("");
  }, []);

  /** 组件卸载时释放仍在使用的本地图片预览地址 */
  useEffect(() => clearImagePreview, [clearImagePreview]);

  /** 多选角色切换 */
  const toggleRole = useCallback((role: string) => {
    setAddRoleTypes(prev =>
      prev.includes(role) ? prev.filter(r => r !== role) : [...prev, role]
    );
  }, []);

  /** 手机号查找用户 */
  const handleLookup = useCallback(async () => {
    if (!addMobile.trim()) return;
    setLookupLoading(true);
    setLookupError("");
    setLookupResult(null);
    const user = await api.lookupUser(addMobile.trim());
    if (user) {
      setLookupResult(user);
      setAddStaffName(user.nickname);
    } else {
      setLookupError("未找到该手机号的用户");
    }
    setLookupLoading(false);
  }, [addMobile]);

  /** 提交添加员工 */
  const handleAddStaff = useCallback(async () => {
    if (!lookupResult || !addStaffName.trim()) return;
    if (addRoleTypes.length === 0) { setStaffError("请至少选择一个角色"); return; }
    setSubmitLoading(true);
    setStaffError("");
    try {
      await api.createStaff({
        mobile: lookupResult.mobile,
        staffName: addStaffName.trim(),
        roleType: addRoleTypes.join(","),
      });
      setShowAddModal(false);
      setAddMobile("");
      setAddStaffName("");
      setAddRoleTypes(["OPERATOR"]);
      setLookupResult(null);
      setLookupError("");
      loadStaff();
    } catch (err) {
      setStaffError(err instanceof Error ? err.message : "添加员工失败");
    }
    setSubmitLoading(false);
  }, [lookupResult, addStaffName, addRoleTypes, loadStaff]);

  /** 打开编辑弹窗 */
  const openEdit = useCallback((staff: Staff) => {
    setEditStaff(staff);
    setEditStaffName(staff.staffName);
    setEditRoleTypes((staff.roleType || "").split(",").map(r => r.trim()).filter(Boolean));
    setStaffError("");
  }, []);

  /** 提交编辑员工 */
  const handleEditStaff = useCallback(async () => {
    if (!editStaff || !editStaffName.trim()) return;
    if (editRoleTypes.length === 0) { setStaffError("请至少选择一个角色"); return; }
    setSubmitLoading(true);
    setStaffError("");
    try {
      await api.updateStaff(editStaff.id, {
        staffName: editStaffName.trim(),
        roleType: editRoleTypes.join(","),
      });
      setEditStaff(null);
      loadStaff();
    } catch (err) {
      setStaffError(err instanceof Error ? err.message : "更新失败");
    }
    setSubmitLoading(false);
  }, [editStaff, editStaffName, editRoleTypes, loadStaff]);

  /** 切换员工状态 */
  const handleToggleStatus = useCallback(async (staffId: string) => {
    try {
      await api.toggleStaffStatus(staffId);
      loadStaff();
    } catch {
      // 静默失败，列表维持原样
    }
  }, [loadStaff]);

  /** 打开发布商品弹窗时加载类目 */
  const loadProductCategories = useCallback(async () => {
    setProdCategoryLoading(true);
    setProdCategoryError("");
    try {
      const tree = await api.fetchCategories();
      setProdCategoryTree(tree);
      if (tree.length === 0) {
        setProdCategoryError("暂无可用类目，请先初始化类目数据");
      }
    } catch (err) {
      setProdCategoryError(err instanceof Error ? err.message : "类目加载失败，请重试");
    } finally {
      setProdCategoryLoading(false);
    }
  }, []);

  const openProductModal = useCallback(() => {
    setShowProductModal(true);
    setProdError("");
    setProdCategoryError("");
  }, []);

  useEffect(() => {
    if (showProductModal && prodCategoryTree.length === 0 && !prodCategoryLoading && !prodCategoryError) {
      loadProductCategories();
    }
  }, [loadProductCategories, prodCategoryError, prodCategoryLoading, prodCategoryTree.length, showProductModal]);

  const setMerchantAuditingMetric = useCallback((nextProducts: Product[]) => {
    setDisplayMetrics((currentMetrics) => currentMetrics.map((metric) => {
      if (!metric.label.includes("审核中商品")) return metric;
      return {
        ...metric,
        value: String(nextProducts.filter((product) => product.status === "AUDITING").length)
      };
    }));
  }, []);

  /** 上传商品图片 */
  const handleImageUpload = useCallback(async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
    clearImagePreview();
    const previewUrl = URL.createObjectURL(file);
    prodImagePreviewRef.current = previewUrl;
    setProdImagePreview(previewUrl);
    setProdImage("");
    setProdImageUploading(true);
    try {
      const url = await api.uploadImage(file);
      setProdImage(url);
    } catch (err) {
      setProdError(err instanceof Error ? err.message : "图片上传失败");
      setProdImage("");
      clearImagePreview();
    }
    setProdImageUploading(false);
    // 清空 input 值以便重复选择同一文件
    e.target.value = "";
  }, [clearImagePreview]);

  /** 删除已选择的商品主图 */
  const removeProductImage = useCallback(() => {
    setProdImage("");
    clearImagePreview();
  }, [clearImagePreview]);

  /** 提交发布商品 */
  const handlePublishProduct = useCallback(async () => {
    if (!prodName.trim() || !prodPrice.trim() || !prodStock.trim() || !prodCategoryId) {
      setProdError("请填写商品名称、类目、价格和库存");
      return;
    }
    setProdSubmitting(true);
    setProdError("");
    try {
      await api.createMerchantProduct({
        productName: prodName.trim(),
        categoryId: prodCategoryId,
        price: prodPrice.trim(),
        stockTotal: parseInt(prodStock, 10) || 0,
        mainImageUrl: prodImage.trim(),
        detailHtml: prodDesc.trim(),
      });
      const freshResult = await api.fetchMerchantProducts();
      setDisplayProducts(freshResult.items);
      setProductTotal(freshResult.total);
      setProductPage(1);
      setMerchantAuditingMetric(freshResult.items);
      setShowProductModal(false);
      setProdName(""); setProdCategoryId(0); setProdPrice(""); setProdStock("");
      setProdImage(""); clearImagePreview(); setProdDesc("");
    } catch (err) {
      setProdError(err instanceof Error ? err.message : "发布商品失败");
    }
    setProdSubmitting(false);
  }, [prodName, prodCategoryId, prodPrice, prodStock, prodImage, prodDesc, clearImagePreview, setMerchantAuditingMetric]);

  /** 判断区域是否可见 */
  const canShow = useCallback((section: string) => {
    return !visibleSections || visibleSections.includes(section);
  }, [visibleSections]);

  /** 角色类型中文名 */
  const roleTypeLabel: Record<string, string> = {
    ADMIN: "管理员",
    OPERATOR: "运营",
    CUSTOMER_SERVICE: "客服",
  };

  /** 可选角色列表 */
  const roleOptions = [
    { value: "ADMIN", label: "管理员 — 全部菜单权限" },
    { value: "OPERATOR", label: "运营 — 商品/订单/售后" },
    { value: "CUSTOMER_SERVICE", label: "客服 — 售后处理" },
  ];

  return (
    <div className="portal-page merchant-page">
      {/* Hero 区域：商家端简介与经营概要 */}
      <section id="merchant-dashboard" className="hero merchant-hero">
        <div className="hero-copy">
          <span className="eyebrow">商家端 Web</span>
          <h1>商品、履约、售后、资金都围绕店铺工作台展开</h1>
          <p>冻结状态下仍保留已支付订单履约能力，财务动作和员工权限独立管控。</p>
          <div className="hero-actions">
            <button className="primary-button" type="button" onClick={openProductModal}>
              <Boxes size={16} />
              发布商品
            </button>
            <button className="secondary-button" type="button" onClick={() => onNavigate?.("orders")}>
              <Truck size={16} />
              处理发货
            </button>
          </div>
        </div>
        {/* 商家经营概要卡片 */}
        <div id="merchant-store" className="merchant-summary">
          <div>
            <span>店铺状态</span>
            <strong>正常经营</strong>
          </div>
          <div>
            <span>可提现余额</span>
            <strong>¥{withdrawableAmount}</strong>
          </div>
          <div>
            <span>员工账号</span>
            <strong>老板 + {staffList.filter(s => s.status === "ENABLED").length} 员工</strong>
          </div>
        </div>
      </section>

      <MetricGrid metrics={displayMetrics} />

      {canShow("products") || canShow("orders") ? (
        <section className="content-grid two-col">
          {canShow("products") && (
            <Card id="merchant-products">
              <SectionHeader title="商品管理" description="来源：GET /api/v1/merchant/products" />
              <Toolbar>
                <input className="search-input" placeholder="搜索商品名称" value={searchKeyword}
                  onChange={e => setSearchKeyword(e.target.value)}
                  onKeyDown={e => { if (e.key === "Enter") handleProductSearch(); }} />
                <button className="primary-button compact" type="button" onClick={handleProductSearch} disabled={productLoading}>
                  {productLoading ? <Loader2 size={14} className="spin" /> : <Search size={14} />} 搜索
                </button>
              </Toolbar>
              <DataTable
                columns={["商品", "价格", "库存", "状态", "操作"]}
                rows={displayProducts.length === 0
                  ? [["", <span className="muted">暂无商品，点击"发布商品"创建</span>, "", "", ""]]
                  : displayProducts.map((product) => [
                    <div className="table-product">{product.image ? <img src={product.image} alt="" /> : null}<span>{product.name}</span></div>,
                    <AmountText value={product.price} />,
                    product.stock,
                    <StatusTag {...productStatusMap[product.status]} />,
                    <button className="link-button">{product.status === "AUDITING" ? "审核中" : product.status === "REJECTED" ? "已驳回" : "编辑"}</button>
                  ])
                }
              />
              {productTotal > productPageSize && (
                <div className="pagination" style={{display:'flex', justifyContent:'flex-end', gap:4, padding:'8px 0'}}>
                  <button className="secondary-button compact" type="button" disabled={productPage <= 1 || productLoading} onClick={() => handleProductPageChange(productPage - 1)}>上一页</button>
                  <span className="pagination-info" style={{lineHeight:'28px', fontSize:13, padding:'0 8px'}}>第 {productPage} / {Math.ceil(productTotal / productPageSize)} 页</span>
                  <button className="secondary-button compact" type="button" disabled={productPage >= Math.ceil(productTotal / productPageSize) || productLoading} onClick={() => handleProductPageChange(productPage + 1)}>下一页</button>
                </div>
              )}
            </Card>
          )}
          {canShow("orders") && (
            <Card id="merchant-orders">
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
          )}
        </section>
      ) : null}

      {canShow("after-sales") || canShow("finance") ? (
        <section className="content-grid two-col">
          {canShow("after-sales") && (
            <Card id="merchant-after-sales">
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
          )}
          {canShow("finance") && (
            <Card id="merchant-finance">
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
          )}
        </section>
      ) : null}

      {/* 员工管理 */}
      {canShow("staffs") && <Card id="merchant-staffs">
        <SectionHeader
          title="员工管理"
          description="可添加、启用/停用商家员工，员工登录后按 RBAC 角色获取权限"
        />
        <div className="staff-toolbar">
          <div className="staff-search">
            <input
              type="text"
              className="search-input"
              placeholder="搜索员工姓名..."
              value={staffSearch}
              onChange={e => handleSearchChange(e.target.value)}
            />
          </div>
          <button className="primary-button compact" type="button" onClick={() => setShowAddModal(true)}>
            <UserPlus size={16} />
            添加员工
          </button>
        </div>
        {staffLoading ? (
          <div className="loading-inline"><Loader2 size={18} className="spin" /> 加载中...</div>
        ) : staffList.length === 0 ? (
          <div className="empty-state">{staffSearch ? "未找到匹配的员工" : '暂无员工，点击"添加员工"按钮添加'}</div>
        ) : (
          <DataTable
            columns={["姓名", "角色", "状态", "操作"]}
            rows={pagedStaff.map((staff) => [
              <div><div className="staff-name">{staff.staffName}</div></div>,
              <div className="role-tags">
                {((staff.roleType || "").split(",")).map(r => r.trim()).filter(Boolean).map(r => (
                  <span key={r} className="role-tag">{roleTypeLabel[r] || r}</span>
                ))}
              </div>,
              <StatusTag tone={staff.status === "ENABLED" ? "success" : "neutral"} label={staff.status === "ENABLED" ? "正常" : "停用"} />,
              <div className="action-buttons">
                <button className="link-button" onClick={() => openEdit(staff)}>修改</button>
                <button className="link-button" onClick={() => handleToggleStatus(staff.id)}>
                  {staff.status === "ENABLED" ? "停用" : "启用"}
                </button>
              </div>
            ])}
          />
        )}
        {staffList.length > pageSize && (
          <div className="pagination">
            <button className="page-btn" disabled={staffPage <= 1} onClick={() => setStaffPage(p => p - 1)}>上一页</button>
            <span className="page-info">{staffPage} / {totalPages}</span>
            <button className="page-btn" disabled={staffPage >= totalPages} onClick={() => setStaffPage(p => p + 1)}>下一页</button>
          </div>
        )}
      </Card>}

      {/* 编辑员工模态框 */}
      {editStaff && (
        <div className="modal-overlay" onClick={() => setEditStaff(null)}>
          <div className="modal-content" onClick={e => e.stopPropagation()}>
            <div className="modal-header">
              <h2><UserPlus size={20} /> 修改员工</h2>
              <button type="button" className="modal-close" onClick={() => setEditStaff(null)}>
                <X size={20} />
              </button>
            </div>
            <div className="modal-body">
              <div className="form-group">
                <label>员工姓名</label>
                <input type="text" placeholder="输入员工姓名" value={editStaffName} onChange={e => setEditStaffName(e.target.value)} disabled={submitLoading} />
              </div>
              <div className="form-group">
                <label>角色类型（可多选）</label>
                <div className="role-checkboxes">
                  {roleOptions.map(opt => (
                    <label key={opt.value} className="role-checkbox">
                      <input type="checkbox" checked={editRoleTypes.includes(opt.value)} onChange={() => setEditRoleTypes(prev => prev.includes(opt.value) ? prev.filter(r => r !== opt.value) : [...prev, opt.value])} disabled={submitLoading} />
                      <span>{opt.label}</span>
                    </label>
                  ))}
                </div>
              </div>
              {staffError && <p className="form-error">{staffError}</p>}
            </div>
            <div className="modal-footer">
              <button className="secondary-button" type="button" onClick={() => setEditStaff(null)} disabled={submitLoading}>取消</button>
              <button className="primary-button" type="button" onClick={handleEditStaff} disabled={submitLoading}>
                {submitLoading ? <Loader2 size={16} className="spin" /> : <Plus size={16} />}
                保存修改
              </button>
            </div>
          </div>
        </div>
      )}

      {/* 发布商品模态框 */}
      {showProductModal && (
        <div className="modal-overlay" onClick={() => setShowProductModal(false)}>
          <div className="modal-content" onClick={e => e.stopPropagation()} style={{maxWidth: 500}}>
            <div className="modal-header">
              <h2><Boxes size={20} /> 发布商品</h2>
              <button type="button" className="modal-close" onClick={() => setShowProductModal(false)}>
                <X size={20} />
              </button>
            </div>
            <div className="modal-body">
              <div className="form-group">
                <label>商品名称 <span className="required">*</span></label>
                <input placeholder="输入商品名称" value={prodName} onChange={e => { setProdName(e.target.value); setProdError(""); }} disabled={prodSubmitting} />
              </div>
              <div className="form-row" style={{display:'flex', gap:12}}>
                <div className="form-group" style={{flex:1}}>
                  <label>一级类目 <span className="required">*</span></label>
                  <select value={prodParentId} onChange={e => { setProdParentId(Number(e.target.value)); setProdCategoryId(0); setProdCategoryError(""); }} disabled={prodSubmitting || prodCategoryLoading || prodCategoryTree.length === 0}>
                    <option value={0}>{prodCategoryLoading ? "类目加载中..." : "请选择类目"}</option>
                    {prodCategoryTree.map((c: any) => <option key={c.id} value={c.id}>{c.categoryName}</option>)}
                  </select>
                </div>
                <div className="form-group" style={{flex:1}}>
                  <label>二级类目</label>
                  <select value={prodCategoryId} onChange={e => setProdCategoryId(Number(e.target.value))} disabled={prodSubmitting || prodCategoryLoading || !prodParentId}>
                    <option value={0}>请选择子类目</option>
                    {prodChildCategories.map((c: any) => <option key={c.id} value={c.id}>{c.categoryName}</option>)}
                  </select>
                </div>
              </div>
              {prodCategoryError && (
                <p className="form-error">
                  {prodCategoryError}
                  <button className="link-button" type="button" onClick={loadProductCategories} disabled={prodCategoryLoading || prodSubmitting}>
                    重新加载类目
                  </button>
                </p>
              )}
              <div className="form-row" style={{display:'flex', gap:12}}>
                <div className="form-group" style={{flex:1}}>
                  <label>售价 <span className="required">*</span></label>
                  <input type="number" step="0.01" min="0" placeholder="0.00" value={prodPrice} onChange={e => { setProdPrice(e.target.value); setProdError(""); }} disabled={prodSubmitting} />
                </div>
                <div className="form-group" style={{flex:1}}>
                  <label>库存 <span className="required">*</span></label>
                  <input type="number" min="0" placeholder="0" value={prodStock} onChange={e => { setProdStock(e.target.value); setProdError(""); }} disabled={prodSubmitting} />
                </div>
              </div>
              <div className="form-group">
                <label>主图</label>
                <div className="upload-area">
                  {prodImagePreview || prodImage ? (
                    <div className="upload-preview">
                      <img src={prodImagePreview || prodImage} alt="商品主图" />
                      <button type="button" className="upload-remove" onClick={removeProductImage} disabled={prodSubmitting}>删除</button>
                    </div>
                  ) : (
                    <label className={`upload-btn ${prodImageUploading ? "uploading" : ""}`}>
                      <input type="file" accept="image/*" onChange={handleImageUpload} hidden disabled={prodSubmitting || prodImageUploading} />
                      {prodImageUploading ? <Loader2 size={18} className="spin" /> : <Plus size={18} />}
                      {prodImageUploading ? "上传中..." : "选择图片"}
                    </label>
                  )}
                </div>
                {prodImage && <span className="upload-hint">{prodImage}</span>}
              </div>
              <div className="form-group">
                <label>商品描述</label>
                <textarea rows={3} placeholder="输入商品描述" value={prodDesc} onChange={e => setProdDesc(e.target.value)} disabled={prodSubmitting} style={{padding:'10px 12px', border:'1px solid var(--color-border)', borderRadius:'var(--radius-md)', fontSize:14, outline:'none', resize:'vertical', fontFamily:'inherit'}} />
              </div>
              {prodError && <p className="form-error">{prodError}</p>}
            </div>
            <div className="modal-footer">
              <button className="secondary-button" onClick={() => setShowProductModal(false)} disabled={prodSubmitting}>取消</button>
              <button className="primary-button" onClick={handlePublishProduct} disabled={prodSubmitting || !prodName.trim() || !prodCategoryId || !prodPrice.trim() || !prodStock.trim()}>
                {prodSubmitting ? <Loader2 size={16} className="spin" /> : <Plus size={16} />}
                提交审核
              </button>
            </div>
          </div>
        </div>
      )}

      {/* 添加员工模态框 */}
      {showAddModal && (
        <div className="modal-overlay" onClick={() => setShowAddModal(false)}>
          <div className="modal-content" onClick={e => e.stopPropagation()}>
            <div className="modal-header">
              <h2><UserPlus size={20} /> 添加员工</h2>
              <button type="button" className="modal-close" onClick={() => setShowAddModal(false)}>
                <X size={20} />
              </button>
            </div>
            <div className="modal-body">
              {/* 第一步：手机号查找 */}
              <div className="form-group">
                <label>员工手机号</label>
                <div className="input-row">
                  <input
                    type="text"
                    placeholder="输入手机号查找用户"
                    value={addMobile}
                    onChange={e => { setAddMobile(e.target.value); setLookupResult(null); setLookupError(""); }}
                    disabled={submitLoading}
                  />
                  <button className="secondary-button compact" type="button" onClick={handleLookup} disabled={lookupLoading || !addMobile.trim()}>
                    {lookupLoading ? <Loader2 size={14} className="spin" /> : <Search size={14} />}
                    查找
                  </button>
                </div>
                {lookupError && <p className="form-error">{lookupError}</p>}
                {lookupResult && (
                  <div className="lookup-result">
                    <Check size={14} />
                    已找到用户：{lookupResult.nickname}（{lookupResult.mobile}）
                  </div>
                )}
              </div>

              {/* 第二步：填写员工信息 */}
              <div className="form-group">
                <label>员工姓名</label>
                <input
                  type="text"
                  placeholder="输入员工姓名"
                  value={addStaffName}
                  onChange={e => setAddStaffName(e.target.value)}
                  disabled={submitLoading}
                />
              </div>

              <div className="form-group">
                <label>角色类型（可多选）</label>
                <div className="role-checkboxes">
                  {roleOptions.map(opt => (
                    <label key={opt.value} className="role-checkbox">
                      <input
                        type="checkbox"
                        checked={addRoleTypes.includes(opt.value)}
                        onChange={() => toggleRole(opt.value)}
                        disabled={submitLoading}
                      />
                      <span>{opt.label}</span>
                    </label>
                  ))}
                </div>
              </div>

              {staffError && <p className="form-error">{staffError}</p>}
            </div>
            <div className="modal-footer">
              <button className="secondary-button" type="button" onClick={() => setShowAddModal(false)} disabled={submitLoading}>
                取消
              </button>
              <button className="primary-button" type="button" onClick={handleAddStaff} disabled={submitLoading || !lookupResult}>
                {submitLoading ? <Loader2 size={16} className="spin" /> : <Plus size={16} />}
                确认添加
              </button>
            </div>
          </div>
        </div>
      )}
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
