package com.example.dddshare.DDD四层架构.infrastructure.persistent;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AccountEntity {
    private String id;
    private BigDecimal amount;
}
