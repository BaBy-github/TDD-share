package com.example.dddshare.DDD四层架构.apis;

import com.example.dddshare.DDD四层架构.application.PaymentService;
import com.example.dddshare.DDD四层架构.common.exception.InvalidOperException;
import com.example.dddshare.DDD四层架构.common.exception.NoMoneyException;

import java.math.BigDecimal;

public class PaymentController {
    private PaymentService paymentService;

    public boolean pay(String storeAccountId, BigDecimal price) throws InvalidOperException, NoMoneyException {
        // mock get from Session
         String userId = "myUserId";

        return paymentService.pay(userId, storeAccountId, price);
    }
}
