package com.example.dddshare.tdd.acl;

import com.example.dddshare.mock.RiskCheckService;

import java.math.BigDecimal;

public class BizSafeService {
    private RiskCheckService riskCheckService;

    public boolean checkBizSafe(String userId, String storeAccountId, BigDecimal amount) {
        String riskCode = riskCheckService.checkPayment(userId, storeAccountId, amount);
        return "0000".equals(riskCode);
    }
}
