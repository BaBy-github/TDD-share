package com.example.dddshare.tdd.application;

import java.math.BigDecimal;
import com.example.dddshare.tdd.domain.Account;
import com.example.dddshare.tdd.domain.AccountTransferService;
import com.example.dddshare.tdd.infrastructure.persistent.AccountRepository;

public class PaymentService {
    private AccountRepository accountRepository;
    private AccountTransferService accountTransferService;

    public boolean pay(String userId, String storeAccountId, BigDecimal amount) {
        // 从数据库读取数据
        Account myAccount = accountRepository.find(userId);
        Account storeAccount = accountRepository.find(storeAccountId);

        // 计算新值，并更新
        accountTransferService.transfer(myAccount, storeAccount, amount);

        // 更新到数据库
        accountRepository.save(myAccount);
        accountRepository.save(storeAccount);

        return true;
    }
}