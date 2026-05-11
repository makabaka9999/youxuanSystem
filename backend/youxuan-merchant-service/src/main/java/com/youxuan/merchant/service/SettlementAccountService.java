package com.youxuan.merchant.service;

import com.youxuan.common.exception.BizException;
import com.youxuan.common.id.IdGenerator;
import com.youxuan.common.api.ErrorCode;
import com.youxuan.merchant.dto.CreateSettlementAccountRequest;
import com.youxuan.merchant.model.SettlementAccountDO;
import com.youxuan.merchant.repository.SettlementAccountRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 商家结算账户管理服务。
 */
@Service
public class SettlementAccountService {

    private final SettlementAccountRepository accountRepository;
    private final IdGenerator idGenerator;

    public SettlementAccountService(SettlementAccountRepository accountRepository, IdGenerator idGenerator) {
        this.accountRepository = accountRepository;
        this.idGenerator = idGenerator;
    }

    /**
     * 查询商家所有结算账户。
     *
     * @param merchantId 商家 ID
     * @return 结算账户列表
     */
    public List<SettlementAccountDO> listAccounts(Long merchantId) {
        return accountRepository.findByMerchantId(merchantId);
    }

    /**
     * 创建结算账户。
     *
     * @param merchantId 商家 ID
     * @param request    创建结算账户请求
     * @return 结算账户对象
     */
    @Transactional
    public SettlementAccountDO createAccount(Long merchantId, CreateSettlementAccountRequest request) {
        SettlementAccountDO account = new SettlementAccountDO();
        account.setId(idGenerator.nextId());
        account.setMerchantId(merchantId);
        account.setAccountType(request.getAccountType());
        account.setAccountName(request.getAccountName());
        // 这里对账号做简单脱敏处理，实际生产环境应使用加密算法
        String accountNo = request.getAccountNo();
        account.setAccountNoEncrypted(accountNo);
        if (accountNo != null && accountNo.length() >= 4) {
            account.setAccountNoMasked("****" + accountNo.substring(accountNo.length() - 4));
        } else {
            account.setAccountNoMasked(accountNo);
        }
        account.setBankName(request.getBankName());
        account.setAuditStatus("PENDING");
        account.setStatus("ENABLED");
        account.setRemark(request.getRemark());
        accountRepository.insert(account);
        return account;
    }
}
