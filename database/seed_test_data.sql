-- 插入测试类目
INSERT IGNORE INTO categories (id, parent_id, category_name, level, sort_no, status, remark, created_at, updated_at, version)
VALUES
  (1001, NULL, '电子产品', 1, 1, 'ENABLED', '', NOW(), NOW(), 0),
  (1002, NULL, '服装鞋帽', 1, 2, 'ENABLED', '', NOW(), NOW(), 0),
  (1003, NULL, '食品饮料', 1, 3, 'ENABLED', '', NOW(), NOW(), 0),
  (1101, 1001, '手机', 2, 1, 'ENABLED', '', NOW(), NOW(), 0),
  (1102, 1001, '电脑', 2, 2, 'ENABLED', '', NOW(), NOW(), 0),
  (1201, 1002, '男装', 2, 1, 'ENABLED', '', NOW(), NOW(), 0),
  (1202, 1002, '女装', 2, 2, 'ENABLED', '', NOW(), NOW(), 0);

-- 插入测试用户
INSERT IGNORE INTO users (id, mobile, nickname, status, created_at, updated_at, version)
VALUES (100001, '13800000001', '优选自营', 'ENABLED', NOW(), NOW(), 0);

-- 插入测试商家
INSERT IGNORE INTO merchants (id, merchant_no, owner_user_id, company_name, license_no, contact_name, contact_mobile, audit_status, status, created_at, updated_at, version)
VALUES (80001, 'M202605110001', 100001, '优选自营', '91330100MA00000001', '优选', '13800000001', 'APPROVED', 'ENABLED', NOW(), NOW(), 0);

-- 插入测试店铺
INSERT IGNORE INTO stores (id, merchant_id, store_no, store_name, status, created_at, updated_at, version)
VALUES (90001, 80001, 'S202605110001', '优选自营旗舰店', 'ENABLED', NOW(), NOW(), 0);

-- 插入测试商品 (使用 CONCAT + HEX 避免编码问题)
INSERT IGNORE INTO products (id, product_no, merchant_id, store_id, category_id, product_name, main_image_url, detail_html, audit_status, sale_status, created_at, updated_at, version)
VALUES
  (50001, 'P202605110001', 80001, 90001, 1101, X'E982A9E98089E6898B69BAE5908E2050726F204D6178', 'https://picsum.photos/seed/phone1/400/300', '<p>旗舰手机</p>', 'APPROVED', 'ON_SALE', NOW(), NOW(), 0),
  (50002, 'P202605110002', 80001, 90001, 1102, X'E982A9E98089E7AC94E8AEB0E69CAC20416972', 'https://picsum.photos/seed/laptop1/400/300', '<p>轻薄本</p>', 'APPROVED', 'ON_SALE', NOW(), NOW(), 0),
  (50003, 'P202605110003', 80001, 90001, 1201, X'E982A9E98089E59586E58AA1E794B7E8A385', 'https://picsum.photos/seed/suit1/400/300', '<p>商务套装</p>', 'APPROVED', 'ON_SALE', NOW(), NOW(), 0),
  (50004, 'P202605110004', 80001, 90001, 1202, X'E982A9E98089E8BF9EE8A1A3E8A399', 'https://picsum.photos/seed/dress1/400/300', '<p>时尚连衣裙</p>', 'APPROVED', 'ON_SALE', NOW(), NOW(), 0),
  (50005, 'P202605110005', 80001, 90001, 1003, X'E982A9E98089E59D9AE69E9CE7A4BCE79B92', 'https://picsum.photos/seed/nuts1/400/300', '<p>坚果礼盒</p>', 'APPROVED', 'ON_SALE', NOW(), NOW(), 0);

-- 插入 SKU
INSERT IGNORE INTO product_skus (id, sku_no, product_id, sku_name, sale_price, original_price, sku_attrs, status, created_at, updated_at, version)
VALUES
  (30001, 'SKU202605110001', 50001, '标准版 256G', 5999.00, 6999.00, '{"color":"silver","storage":"256G"}', 'ENABLED', NOW(), NOW(), 0),
  (30002, 'SKU202605110002', 50001, '高配版 512G', 7999.00, 8999.00, '{"color":"black","storage":"512G"}', 'ENABLED', NOW(), NOW(), 0),
  (30003, 'SKU202605110003', 50002, 'M3 16G+512G', 8999.00, 9999.00, '{"chip":"M3","memory":"16G"}', 'ENABLED', NOW(), NOW(), 0),
  (30004, 'SKU202605110004', 50003, '175/92A', 499.00, 699.00, '{"size":"L"}', 'ENABLED', NOW(), NOW(), 0),
  (30005, 'SKU202605110005', 50004, 'M 码', 299.00, 399.00, '{"size":"M"}', 'ENABLED', NOW(), NOW(), 0),
  (30006, 'SKU202605110006', 50005, '1kg 装', 99.00, 129.00, '{"spec":"1kg"}', 'ENABLED', NOW(), NOW(), 0);

-- 插入库存
INSERT IGNORE INTO sku_inventories (id, sku_id, total_stock, locked_stock, available_stock, status, created_at, updated_at, version)
VALUES
  (40001, 30001, 1000, 0, 1000, 'ENABLED', NOW(), NOW(), 0),
  (40002, 30002, 500, 0, 500, 'ENABLED', NOW(), NOW(), 0),
  (40003, 30003, 300, 0, 300, 'ENABLED', NOW(), NOW(), 0),
  (40004, 30004, 200, 0, 200, 'ENABLED', NOW(), NOW(), 0),
  (40005, 30005, 500, 0, 500, 'ENABLED', NOW(), NOW(), 0),
  (40006, 30006, 1000, 0, 1000, 'ENABLED', NOW(), NOW(), 0);
