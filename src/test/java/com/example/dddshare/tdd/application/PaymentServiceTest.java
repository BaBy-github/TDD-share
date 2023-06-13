package com.example.dddshare.tdd.application;

import com.example.dddshare.tdd.acl.BizSafeService;
import com.example.dddshare.tdd.common.exception.InvalidOperException;
import com.example.dddshare.tdd.common.exception.NoMoneyException;
import com.example.dddshare.tdd.domain.Account;
import com.example.dddshare.tdd.domain.AccountTransferService;
import com.example.dddshare.tdd.infrastructure.audit.AuditMessage;
import com.example.dddshare.tdd.infrastructure.audit.AuditMessageProducer;
import com.example.dddshare.tdd.infrastructure.persistent.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // 自动初始化@Mock注释的对象
class PaymentServiceTest {
    @InjectMocks // 这个Mock可以调用真实代码的方法
    private PaymentService paymentService;
    @Mock // 将会注入到@InjectMocks声明的实例中
    private AccountRepository accountRepository;
    @Mock
    private AccountTransferService accountTransferService;
    @Mock
    private BizSafeService bizSafeService;
    @Mock
    private AuditMessageProducer auditMessageProducer;

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
        when(bizSafeService.checkBizSafe(userId, storeAccountId, transferAmount)).thenReturn(true);
        when(auditMessageProducer.send(any())).thenReturn(true);

        boolean result = paymentService.pay(userId, storeAccountId, transferAmount);
        // then
        assertAll(
                () -> assertThat(result).isEqualTo(true),
                () -> verify(accountRepository, times(1)).find(userId),
                () -> verify(accountRepository, times(1)).find(storeAccountId),
                () -> assertDoesNotThrow(() -> NoMoneyException.class),
                () -> verify(accountTransferService, times(1)).transfer(myAccountFormDB, storeAccountFormDB, transferAmount),
                () -> verify(accountRepository, times(1)).save(myAccountFormDB),
                () -> verify(accountRepository, times(1)).save(storeAccountFormDB),
                () -> verify(auditMessageProducer, times(1)).send(isA(AuditMessage.class))
        );
    }

    @Test
    void shouldThrowNoMoneyException_whenTransfer_givenUserAccountNotEnoughMoney() {
        // given
        String userId = "1";
        String storeAccountId = "2";
        BigDecimal transferAmount = BigDecimal.valueOf(300);

        // when
        Account myAccountFormDB = Account.builder()
                .id(userId)
                .amount(BigDecimal.valueOf(0))
                .build();
        Account storeAccountFormDB = Account.builder()
                .id(storeAccountId)
                .amount(BigDecimal.valueOf(1000))
                .build();
        when(accountRepository.find(userId)).thenReturn(myAccountFormDB);
        when(accountRepository.find(storeAccountId)).thenReturn(storeAccountFormDB);

        assertThrows(NoMoneyException.class, () -> paymentService.pay(userId, storeAccountId, transferAmount));
        // then
        assertAll(
                () -> verify(accountRepository, times(1)).find(userId),
                () -> verify(accountRepository, times(1)).find(storeAccountId),
                () -> assertDoesNotThrow(() -> InvalidOperException.class),
                () -> verify(accountTransferService, times(0)).transfer(myAccountFormDB, storeAccountFormDB, transferAmount),
                () -> verify(accountRepository, times(0)).save(myAccountFormDB),
                () -> verify(accountRepository, times(0)).save(storeAccountFormDB),
                () -> verify(auditMessageProducer, times(0)).send(any())
        );
    }

    @Test
    void shouldThrowInvalidOperException_whenTransferNotSafe_givenUserAccountNotEnoughMoney() throws InvalidOperException, NoMoneyException {
        // given
        String userId = "1";
        String storeAccountId = "2";
        BigDecimal transferAmount = BigDecimal.valueOf(300);

        // when
        Account myAccountFormDB = Account.builder().id(userId).amount(BigDecimal.valueOf(1000)).build();
        Account storeAccountFormDB = Account.builder().id(storeAccountId).amount(BigDecimal.valueOf(1000)).build();
        when(accountRepository.find(userId)).thenReturn(myAccountFormDB);
        when(accountRepository.find(storeAccountId)).thenReturn(storeAccountFormDB);
        when(bizSafeService.checkBizSafe(userId, storeAccountId, transferAmount)).thenReturn(false);

        assertThrows(InvalidOperException.class, () -> paymentService.pay(userId, storeAccountId, transferAmount));
        // then
        assertAll(
                () -> verify(accountRepository, times(1)).find(userId),
                () -> verify(accountRepository, times(1)).find(storeAccountId),
                () -> assertDoesNotThrow(() -> NoMoneyException.class),
                () -> verify(accountTransferService, times(0)).transfer(myAccountFormDB, storeAccountFormDB, transferAmount),
                () -> verify(accountRepository, times(0)).save(myAccountFormDB),
                () -> verify(accountRepository, times(0)).save(storeAccountFormDB),
                () -> verify(auditMessageProducer, times(0)).send(any())
        );
    }
}