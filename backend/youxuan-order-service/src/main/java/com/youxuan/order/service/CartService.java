package com.youxuan.order.service;

import com.youxuan.common.api.ErrorCode;
import com.youxuan.common.exception.BizException;
import com.youxuan.common.id.IdGenerator;
import com.youxuan.order.constant.OrderStatusConstants;
import com.youxuan.order.model.CartDO;
import com.youxuan.order.repository.CartRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 购物车业务服务
 * <p>
 * 提供购物车项的增删改查及批量操作功能。
 * 同一用户的同一 SKU 在购物车中只有一条记录，重复添加时累加数量。
 * </p>
 */
@Service
public class CartService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CartService.class);

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private IdGenerator idGenerator;

    /**
     * 获取用户的购物车列表
     *
     * @param userId 用户ID
     * @return 购物车项列表
     */
    public List<CartDO> getCart(Long userId) {
        return cartRepository.findByUserId(userId);
    }

    /**
     * 添加商品到购物车
     * <p>
     * 如果购物车中已存在同一 SKU，则累加数量；否则新增一条购物车记录。
     * </p>
     *
     * @param userId    用户ID
     * @param storeId   店铺ID
     * @param productId 商品ID
     * @param skuId     SKU ID
     * @param quantity  数量
     * @return 购物车项
     */
    @Transactional(rollbackFor = Exception.class)
    public CartDO addItem(Long userId, Long storeId, Long productId, Long skuId, Integer quantity) {
        // 查找是否已存在同一 SKU
        CartDO existing = cartRepository.findByUserAndSku(userId, skuId);
        if (existing != null) {
            // 累加数量
            int newQuantity = existing.getQuantity() + quantity;
            cartRepository.updateQuantity(existing.getId(), newQuantity);
            LOGGER.info("购物车SKU已存在，累加数量. userId={}, skuId={}, oldQuantity={}, newQuantity={}",
                    userId, skuId, existing.getQuantity(), newQuantity);
            return cartRepository.findByUserAndSku(userId, skuId);
        }

        // 新增购物车记录
        CartDO cart = new CartDO();
        cart.setId(idGenerator.nextId());
        cart.setUserId(userId);
        cart.setStoreId(storeId);
        cart.setProductId(productId);
        cart.setSkuId(skuId);
        cart.setQuantity(quantity);
        cart.setChecked(1); // 默认选中
        cart.setStatus(OrderStatusConstants.ENABLED);
        cartRepository.insert(cart);
        LOGGER.info("新增购物车项. userId={}, skuId={}, quantity={}", userId, skuId, quantity);
        return cart;
    }

    /**
     * 更新购物车项数量
     *
     * @param userId   用户ID（用于校验归属）
     * @param itemId   购物车项ID
     * @param quantity 新数量
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateQuantity(Long userId, Long itemId, Integer quantity) {
        CartDO cart = cartRepository.findById(itemId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new BizException(ErrorCode.RESOURCE_NOT_FOUND, "购物车项不存在");
        }
        cartRepository.updateQuantity(itemId, quantity);
        LOGGER.info("更新购物车数量. itemId={}, newQuantity={}", itemId, quantity);
    }

    /**
     * 移除购物车项（软删除）
     *
     * @param userId 用户ID（用于校验归属）
     * @param itemId 购物车项ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeItem(Long userId, Long itemId) {
        CartDO cart = cartRepository.findById(itemId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new BizException(ErrorCode.RESOURCE_NOT_FOUND, "购物车项不存在");
        }
        cartRepository.deleteById(itemId);
        LOGGER.info("移除购物车项. itemId={}", itemId);
    }

    /**
     * 批量更新购物车项选中状态
     *
     * @param userId  用户ID（用于校验归属）
     * @param itemIds 购物车项ID列表
     * @param checked 是否选中
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchCheck(Long userId, List<Long> itemIds, boolean checked) {
        for (Long itemId : itemIds) {
            CartDO cart = cartRepository.findById(itemId);
            if (cart == null || !cart.getUserId().equals(userId)) {
                throw new BizException(ErrorCode.RESOURCE_NOT_FOUND, "购物车项不存在: " + itemId);
            }
            cartRepository.updateChecked(itemId, checked ? 1 : 0);
        }
        LOGGER.info("批量更新购物车选中状态. userId={}, itemIds={}, checked={}", userId, itemIds, checked);
    }
}
