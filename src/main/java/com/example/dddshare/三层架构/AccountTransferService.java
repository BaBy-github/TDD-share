package com.example.dddshare.三层架构;

import java.math.BigDecimal;

public class AccountTransferService {
    public void transfer(Account sourceAccount, Account targerAccount, BigDecimal amount) {
        sourceAccount.withdraw(amount);
        targerAccount.deposit(amount);
    }
}
