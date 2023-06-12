package com.example.dddshare.tdd.domain;

import java.math.BigDecimal;

public class AccountTransferService {
    public void transfer(Account sourceAccount, Account targerAccount, BigDecimal amount) {
        sourceAccount.withdraw(amount);
        targerAccount.deposit(amount);
    }
}
