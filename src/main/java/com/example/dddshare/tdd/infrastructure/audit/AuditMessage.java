package com.example.dddshare.tdd.infrastructure.audit;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AuditMessage {
    private String userId;
    private String storeAccountId;
    private BigDecimal amount;
}
