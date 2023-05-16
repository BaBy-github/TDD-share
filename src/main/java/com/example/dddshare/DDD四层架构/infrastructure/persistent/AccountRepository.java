package com.example.dddshare.DDD四层架构.infrastructure.persistent;

import com.example.dddshare.DDD四层架构.domain.Account;

public class AccountRepository {
    private AccountDao accountDao;

    public Account find(String userId) {
        AccountEntity accountEntity = accountDao.findByUserId(userId);
        return Account.parse(accountEntity);
    }

    public Account save(Account account) {
        AccountEntity accountEntity = account.toEntity();
        if (accountEntity.getId() == null) {
            accountDao.insert(accountEntity);
        } else {
            accountDao.update(accountEntity);
        }
        return Account.parse(accountEntity);
    }
}
