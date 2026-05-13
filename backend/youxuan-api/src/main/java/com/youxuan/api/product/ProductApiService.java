package com.youxuan.api.product;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品服务 Dubbo API 接口
 *
 * <p>提供商品查询、库存管理、商品校验等跨服务调用的能力，供订单、商家等模块通过 Dubbo 远程调用。</p>
 */
public interface ProductApiService {

    /**
     * 根据商品 ID 获取商品名称。
     *
     * @param productId 商品 ID
     * @return 商品名称，若不存在则返回 null
     */
    String getProductName(Long productId);

    /**
     * 获取指定 SKU 的售价。
     *
     * @param skuId SKU ID
     * @return 售价金额
     */
    BigDecimal getSalePrice(Long skuId);

    /**
     * 校验商品是否可售。
     * <p>判断条件：商品审核通过、已上架、指定 SKU 库存充足。</p>
     *
     * @param productId 商品 ID
     * @param skuId     SKU ID
     * @param quantity  需要购买的数量
     * @return true 表示可售，false 表示不可售
     */
    boolean isProductSaleable(Long productId, Long skuId, int quantity);

    /**
     * 锁定指定 SKU 的库存。
     * <p>下单时调用，防止超卖。若库存不足则锁定失败。</p>
     *
     * @param skuId   SKU ID
     * @param quantity 需要锁定的数量
     * @return true 表示锁定成功，false 表示锁定失败（库存不足）
     */
    boolean lockStock(Long skuId, int quantity);

    /**
     * 释放指定 SKU 的库存。
     * <p>订单取消或超时未支付时调用，回退已锁定的库存。</p>
     *
     * @param skuId   SKU ID
     * @param quantity 需要释放的数量
     * @return true 表示释放成功，false 表示释放失败
     */
    boolean releaseStock(Long skuId, int quantity);

    /**
     * 查询指定商家名下的商品总数。
     *
     * @param merchantId 商家 ID
     * @return 商品数量
     */
    int countMerchantProducts(Long merchantId);
}
