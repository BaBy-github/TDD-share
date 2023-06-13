package com.example.dddshare.tdd.application;

import com.example.dddshare.tdd.common.exception.InvalidOperException;
import com.example.dddshare.tdd.common.exception.NoMoneyException;
import com.example.dddshare.tdd.domain.Account;
import com.example.dddshare.tdd.domain.AccountTransferService;
import com.example.dddshare.tdd.infrastructure.persistent.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // 自动初始化@Mock注释的对象
class PaymentServiceTest {
    @InjectMocks // 这个Mock可以调用真实代码的方法
    private PaymentService paymentService;
    @Mock // 将会注入到@InjectMocks声明的实例中
    private AccountRepository accountRepository;
    @Mock
    private AccountTransferService accountTransferService;

    @Test
    void shouldReturnTrue_whenTransferSuccess_givenUserIdAndStoreAccountIdAndAmount() throws InvalidOperException, NoMoneyException {
        // given
        String userId = "1";
        String storeAccountId = "2";
        BigDecimal transferAmount = BigDecimal.valueOf(300);

        // when
        Account myAccountFormDB = Account.builder()
                .id(userId)
                .amount(BigDecimal.valueOf(1000))
                .build();
        Account storeAccountFormDB = Account.builder()
                .id(storeAccountId)
                .amount(BigDecimal.valueOf(1000))
                .build();
        when(accountRepository.find(userId)).thenReturn(myAccountFormDB);
        when(accountRepository.find(storeAccountId)).thenReturn(storeAccountFormDB);
        // Mock void method
        doNothing().when(accountTransferService).transfer(myAccountFormDB, storeAccountFormDB, transferAmount);

        boolean result = paymentService.pay(userId, storeAccountId, transferAmount);
        // then
        assertThat(result).isEqualTo(true);
    }
}