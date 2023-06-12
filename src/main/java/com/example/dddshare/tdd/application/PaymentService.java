package com.example.dddshare.tdd.application;

import com.example.dddshare.tdd.acl.BizSafeService;
import com.example.dddshare.tdd.infrastructure.audit.AuditMessage;
import com.example.dddshare.tdd.domain.Account;
import com.example.dddshare.tdd.domain.AccountTransferService;
import com.example.dddshare.tdd.infrastructure.persistent.AccountRepository;
import com.example.dddshare.tdd.infrastructure.audit.AuditMessageProducer;
import com.example.dddshare.tdd.common.exception.InvalidOperException;
import com.example.dddshare.tdd.common.exception.NoMoneyException;

import java.math.BigDecimal;

public class PaymentService {
    private AccountRepository accountRepository;
    private AuditMessageProducer auditMessageProducer;
    private BizSafeService bizSafeService;
    private AccountTransferService accountTransferService;

    public boolean pay(String userId, String storeAccountId, BigDecimal amount) throws NoMoneyException, InvalidOperException {
        // 1．从数据库读取数据
        Account myAccount = accountRepository.find(userId);
        Account storeAccount = accountRepository.find(storeAccountId);
        // 2．业务参数校验
        if (amount.compareTo(myAccount.getAmount()) > 0) {
            throw new NoMoneyException();
        }
        // 3. 调用风控微服务
        if (!bizSafeService.checkBizSafe(userId, storeAccountId, amount)) {
            throw new InvalidOperException();
        }
        // 5. 计算新值，并更新
        accountTransferService.transfer(myAccount, storeAccount, amount);
        // 6. 更新到数据库
        accountRepository.save(myAccount);
        accountRepository.save(storeAccount);
        // 7. 发送审计消息
        auditMessageProducer.send(AuditMessage.builder()
                .userId(userId)
                .storeAccountId(storeAccountId)
                .amount(amount)
                .build());
        return true;
    }
}