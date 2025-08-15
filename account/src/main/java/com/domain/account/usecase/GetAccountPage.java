package com.domain.account.usecase;

import com.domain.account.model.Account;
import com.domain.account.model.PageResult;
import com.domain.account.port.AccountPort;
import com.domain.account.port.query.AccountPageQuery;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class GetAccountPage implements Usecase<PageResult<Account>, AccountPageQuery> {

    private final AccountPort accountPort;

    @Override
    public PageResult<Account> execute(AccountPageQuery request) {
        return accountPort.getAllAccounts(request);
    }
}
