package com.domain.account.port;

import com.domain.account.model.Account;
import com.domain.account.usecase.request.PageResult;
import com.domain.account.port.query.AccountPageQuery;
import com.domain.account.port.query.AccountQuery;

import java.util.Optional;

public interface AccountPort {

    Optional<Account> getAccount(AccountQuery query);

    PageResult<Account> getAllAccounts(AccountPageQuery request);
}
