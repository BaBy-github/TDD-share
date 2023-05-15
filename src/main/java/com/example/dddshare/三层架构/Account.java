package com.example.dddshare.三层架构;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class Account {
    private String id;
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

    public static Account parse(AccountEntity accountEntity) {
        return Account.builder()
                .amount(accountEntity.getAmount())
                .id(accountEntity.getId())
                .build();
    }

    public AccountEntity toEntity() {
        return AccountEntity.builder()
                .id(this.getId())
                .amount(this.amount)
                .build();
    }
}
