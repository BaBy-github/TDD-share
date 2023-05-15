package com.example.dddshare.三层架构;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountEntity {
    private Integer id;
    private BigDecimal amount;
}
