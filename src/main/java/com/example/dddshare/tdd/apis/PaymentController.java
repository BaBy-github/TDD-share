package com.example.dddshare.tdd.apis;

import com.example.dddshare.tdd.application.PaymentService;
import com.example.dddshare.tdd.common.exception.InvalidOperException;
import com.example.dddshare.tdd.common.exception.NoMoneyException;

import java.math.BigDecimal;

public class PaymentController {
    private PaymentService paymentService;

    public boolean pay(String storeAccountId, BigDecimal price) throws InvalidOperException, NoMoneyException {
        // mock get from Session
         String userId = "myUserId";

        return paymentService.pay(userId, storeAccountId, price);
    }
}
