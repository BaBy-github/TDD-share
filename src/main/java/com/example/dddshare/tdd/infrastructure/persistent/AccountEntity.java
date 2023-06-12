package com.example.dddshare.tdd.infrastructure.persistent;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AccountEntity {
    private String id;
    private BigDecimal amount;
}
