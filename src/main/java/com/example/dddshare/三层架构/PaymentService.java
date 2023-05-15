package com.example.dddshare.三层架构;

import com.example.dddshare.mock.InvalidOperException;
import com.example.dddshare.mock.KafkaTemplate;
import com.example.dddshare.mock.NoMoneyException;
import com.example.dddshare.mock.RiskCheckService;

import java.math.BigDecimal;

import static com.example.dddshare.mock.KafkaTemplate.TOPIC_AUDIT_LOG;

public class PaymentService {
    private AccountDao accountDao;
    private KafkaTemplate kafkaTemplate;
    private RiskCheckService riskCheckService;

    public boolean pay(String userId, String storeAccountId, BigDecimal amount) throws NoMoneyException, InvalidOperException {
        // 1．从数据库读取数据
        AccountEntity myAccount = accountDao.findByUserId(userId);
        AccountEntity storeAccount = accountDao.findByMerchantAccountId(storeAccountId);
        // 2．业务参数校验
        if (amount.compareTo(myAccount.getAmount()) > 0) {
            throw new NoMoneyException();
        }
        // 3. 调用风控微服务
        String riskCode = riskCheckService.checkPayment(userId, storeAccountId, amount);
        // 4. 检查交易合法性
        if (!"0000".equals(riskCode)) {
            throw new InvalidOperException();
        }
        // 5. 计算新值，并更新
        BigDecimal newSource = myAccount.getAmount().subtract(amount);
        BigDecimal newTarget = myAccount.getAmount().add(amount);
        myAccount.setAmount(newSource);
        storeAccount.setAmount(newTarget);
        // 6. 更新到数据库
        accountDao.update(myAccount);
        accountDao.update(storeAccount);
        // 7. 发送审计消息
        String message = myAccount.getId() + "," + storeAccount.getId() + "," + amount;
        kafkaTemplate.send(TOPIC_AUDIT_LOG, message);
        return true;
    }
}