package com.example.dddshare.DDD四层架构.domain;

import com.example.dddshare.DDD四层架构.domain.Account;

import java.math.BigDecimal;

public class AccountTransferService {
    public void transfer(Account sourceAccount, Account targerAccount, BigDecimal amount) {
        sourceAccount.withdraw(amount);
        targerAccount.deposit(amount);
    }
}
