-- 类目种子数据
-- 先清空旧数据
DELETE FROM categories;

-- 一级类目
INSERT INTO categories (id, parent_id, category_name, level, sort_no, status, created_at, updated_at, version) VALUES
(1001, 0, '手机数码',   1, 1, 'ENABLED', NOW(), NOW(), 0),
(1002, 0, '电脑办公',   1, 2, 'ENABLED', NOW(), NOW(), 0),
(1003, 0, '家用电器',   1, 3, 'ENABLED', NOW(), NOW(), 0),
(1004, 0, '服装鞋帽',   1, 4, 'ENABLED', NOW(), NOW(), 0),
(1005, 0, '食品饮料',   1, 5, 'ENABLED', NOW(), NOW(), 0),
(1006, 0, '美妆个护',   1, 6, 'ENABLED', NOW(), NOW(), 0),
(1007, 0, '家居生活',   1, 7, 'ENABLED', NOW(), NOW(), 0),
(1008, 0, '母婴玩具',   1, 8, 'ENABLED', NOW(), NOW(), 0),
(1009, 0, '图书文具',   1, 9, 'ENABLED', NOW(), NOW(), 0),
(1010, 0, '运动户外',   1, 10, 'ENABLED', NOW(), NOW(), 0);

-- 二级类目（手机数码）
INSERT INTO categories (id, parent_id, category_name, level, sort_no, status, created_at, updated_at, version) VALUES
(1101, 1001, '手机',         2, 1, 'ENABLED', NOW(), NOW(), 0),
(1102, 1001, '平板电脑',     2, 2, 'ENABLED', NOW(), NOW(), 0),
(1103, 1001, '智能手表',     2, 3, 'ENABLED', NOW(), NOW(), 0),
(1104, 1001, '耳机音箱',     2, 4, 'ENABLED', NOW(), NOW(), 0),
(1105, 1001, '手机配件',     2, 5, 'ENABLED', NOW(), NOW(), 0);

-- 二级类目（电脑办公）
INSERT INTO categories (id, parent_id, category_name, level, sort_no, status, created_at, updated_at, version) VALUES
(1201, 1002, '笔记本电脑',   2, 1, 'ENABLED', NOW(), NOW(), 0),
(1202, 1002, '台式机',       2, 2, 'ENABLED', NOW(), NOW(), 0),
(1203, 1002, '显示器',       2, 3, 'ENABLED', NOW(), NOW(), 0),
(1204, 1002, '外设设备',     2, 4, 'ENABLED', NOW(), NOW(), 0);

-- 二级类目（家用电器）
INSERT INTO categories (id, parent_id, category_name, level, sort_no, status, created_at, updated_at, version) VALUES
(1301, 1003, '厨房电器',     2, 1, 'ENABLED', NOW(), NOW(), 0),
(1302, 1003, '生活电器',     2, 2, 'ENABLED', NOW(), NOW(), 0),
(1303, 1003, '个护电器',     2, 3, 'ENABLED', NOW(), NOW(), 0);

-- 二级类目（服装鞋帽）
INSERT INTO categories (id, parent_id, category_name, level, sort_no, status, created_at, updated_at, version) VALUES
(1401, 1004, '男装',         2, 1, 'ENABLED', NOW(), NOW(), 0),
(1402, 1004, '女装',         2, 2, 'ENABLED', NOW(), NOW(), 0),
(1403, 1004, '运动鞋',       2, 3, 'ENABLED', NOW(), NOW(), 0),
(1404, 1004, '配饰',         2, 4, 'ENABLED', NOW(), NOW(), 0);

-- 二级类目（食品饮料）
INSERT INTO categories (id, parent_id, category_name, level, sort_no, status, created_at, updated_at, version) VALUES
(1501, 1005, '休闲零食',     2, 1, 'ENABLED', NOW(), NOW(), 0),
(1502, 1005, '饮料冲调',     2, 2, 'ENABLED', NOW(), NOW(), 0),
(1503, 1005, '茶酒礼品',     2, 3, 'ENABLED', NOW(), NOW(), 0);

-- 二级类目（美妆个护）
INSERT INTO categories (id, parent_id, category_name, level, sort_no, status, created_at, updated_at, version) VALUES
(1601, 1006, '面部护肤',     2, 1, 'ENABLED', NOW(), NOW(), 0),
(1602, 1006, '彩妆',         2, 2, 'ENABLED', NOW(), NOW(), 0),
(1603, 1006, '洗发护发',     2, 3, 'ENABLED', NOW(), NOW(), 0);
