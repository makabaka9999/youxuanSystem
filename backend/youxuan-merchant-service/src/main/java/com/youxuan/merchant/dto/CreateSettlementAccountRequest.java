package com.youxuan.merchant.dto;

/**
 * 创建结算账户请求 DTO。
 */
public class CreateSettlementAccountRequest {

    /** 账户类型：BANK_CARD / ALIPAY / WECHAT */
    private String accountType;

    /** 开户名称 */
    private String accountName;

    /** 账号（明文，服务端加密存储） */
    private String accountNo;

    /** 开户银行名称 */
    private String bankName;

    /** 备注 */
    private String remark;

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }
    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }
    public String getAccountNo() { return accountNo; }
    public void setAccountNo(String accountNo) { this.accountNo = accountNo; }
    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
