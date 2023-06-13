package com.example.dddshare.tdd.application;

import java.math.BigDecimal;

import com.example.dddshare.tdd.acl.BizSafeService;
import com.example.dddshare.tdd.common.exception.InvalidOperException;
import com.example.dddshare.tdd.common.exception.NoMoneyException;
import com.example.dddshare.tdd.domain.Account;
import com.example.dddshare.tdd.domain.AccountTransferService;
import com.example.dddshare.tdd.infrastructure.persistent.AccountRepository;

public class PaymentService {
    private AccountRepository accountRepository;
    private AccountTransferService accountTransferService;
    private BizSafeService bizSafeService;

    public boolean pay(String userId, String storeAccountId, BigDecimal amount) throws NoMoneyException, InvalidOperException {
        // 从数据库读取数据
        Account myAccount = accountRepository.find(userId);
        Account storeAccount = accountRepository.find(storeAccountId);

        // 业务参数校验
        if (amount.compareTo(myAccount.getAmount()) > 0) {
            throw new NoMoneyException();
        }
        // 调用风控微服务
        if (!bizSafeService.checkBizSafe(userId, storeAccountId, amount)) {
            throw new InvalidOperException();
        }
        // 计算新值，并更新
        accountTransferService.transfer(myAccount, storeAccount, amount);

        // 更新到数据库
        accountRepository.save(myAccount);
        accountRepository.save(storeAccount);

        return true;
    }
}