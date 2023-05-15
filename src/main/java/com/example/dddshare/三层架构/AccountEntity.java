package com.example.dddshare.三层架构;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountEntity {
    private Integer id;
    private BigDecimal amount;

    /**
     * 转出操作
     */
    public void withdraw(BigDecimal amount) {
        this.amount = this.amount.subtract(amount);
    }

    /**
     * 转入操作
     */
    public void deposit(BigDecimal amount) {
        this.amount = this.amount.add(amount);
    }
}
