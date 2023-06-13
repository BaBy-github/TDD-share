package com.example.dddshare.tdd.application;

import com.example.dddshare.tdd.acl.BizSafeService;
import com.example.dddshare.tdd.common.exception.InvalidOperException;
import com.example.dddshare.tdd.common.exception.NoMoneyException;
import com.example.dddshare.tdd.domain.Account;
import com.example.dddshare.tdd.domain.AccountTransferService;
import com.example.dddshare.tdd.infrastructure.audit.AuditMessage;
import com.example.dddshare.tdd.infrastructure.audit.AuditMessageProducer;
import com.example.dddshare.tdd.infrastructure.persistent.AccountRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

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
    @Captor // 参数捕获器
    ArgumentCaptor<Account> accountArgumentCaptor;

    private String userId;
    private String storeAccountId;
    private BigDecimal transferAmount;
    private BigDecimal myAccountOriginalAmount;
    private BigDecimal storeAccountOriginalAmount;
    private Account myAccountFormDB;
    private Account storeAccountFormDB;

    @BeforeEach
    public void beforeEach() {
        userId = "1";
        storeAccountId = "2";
        transferAmount = BigDecimal.valueOf(300);
        myAccountOriginalAmount = BigDecimal.valueOf(1000);
        storeAccountOriginalAmount = BigDecimal.valueOf(1000);
        myAccountFormDB = Account.builder()
                .id(userId)
                .amount(myAccountOriginalAmount)
                .build();
        storeAccountFormDB = Account.builder()
                .id(storeAccountId)
                .amount(storeAccountOriginalAmount)
                .build();
    }

    @AfterEach
    public void afterEach() {
        userId = "";
        storeAccountId = "";
        transferAmount = null;
        myAccountFormDB = null;
        storeAccountFormDB = null;
    }

    @Test
    void shouldReturnTrue_whenTransferSuccess_givenUserIdAndStoreAccountIdAndAmount() throws InvalidOperException, NoMoneyException {
        // given
        // when
        when(accountRepository.find(userId)).thenReturn(myAccountFormDB);
        when(accountRepository.find(storeAccountId)).thenReturn(storeAccountFormDB);
        // Mock void method
        doCallRealMethod().when(accountTransferService).transfer(myAccountFormDB, storeAccountFormDB, transferAmount);
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
                () -> {
                    verify(accountRepository, times(2)).save(accountArgumentCaptor.capture());
                    List<Account> accountRepositorySaveArguments = accountArgumentCaptor.getAllValues();
                    accountRepositorySaveArguments.stream()
                            .filter(account -> Objects.equals(account.getId(), userId))
                            .findFirst()
                            .ifPresent(myAccount -> assertThat(myAccount.getAmount()).isEqualTo(myAccountOriginalAmount.subtract(transferAmount)));
                    accountRepositorySaveArguments.stream()
                            .filter(account -> Objects.equals(account.getId(), storeAccountId))
                            .findFirst()
                            .ifPresent(storeAccount -> assertThat(storeAccount.getAmount()).isEqualTo(storeAccountOriginalAmount.add(transferAmount)));
                },
                () -> verify(auditMessageProducer, times(1)).send(isA(AuditMessage.class))
        );
    }

    @Test
    void shouldThrowNoMoneyException_whenTransfer_givenUserAccountNotEnoughMoney() {
        // given
        myAccountFormDB.setAmount(BigDecimal.valueOf(0));

        // when
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
        // when
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