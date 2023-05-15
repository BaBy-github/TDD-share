package com.example.dddshare.三层架构;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AccountEntity {
    private String id;
    private BigDecimal amount;
}
