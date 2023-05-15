package com.example.dddshare.三层架构;

import com.example.dddshare.mock.InvalidOperException;
import com.example.dddshare.mock.NoMoneyException;

import java.math.BigDecimal;

public class PaymentController {
    private PaymentService paymentService;

    public boolean pay(String storeAccountId, BigDecimal price) throws InvalidOperException, NoMoneyException {
        // mock get from Session
         String userId = "myUserId";

        return paymentService.pay(userId, storeAccountId, price);
    }
}
