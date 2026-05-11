package com.youxuan.order.dto;

/**
 * 收货地址快照
 * <p>
 * 下单时从用户收货地址中提取的地址信息快照，序列化为 JSON 后存储在 orders 表的 receiver_snapshot 字段中。
 * 后续发货时以该快照为准，不受用户后续修改地址的影响。
 * </p>
 */
public class AddressSnapshot {

    /** 收货人姓名 */
    private String receiverName;

    /** 收货人手机号 */
    private String receiverMobile;

    /** 省份 */
    private String province;

    /** 城市 */
    private String city;

    /** 区县 */
    private String district;

    /** 详细地址 */
    private String detailAddress;

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverMobile() {
        return receiverMobile;
    }

    public void setReceiverMobile(String receiverMobile) {
        this.receiverMobile = receiverMobile;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getDetailAddress() {
        return detailAddress;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }
}
