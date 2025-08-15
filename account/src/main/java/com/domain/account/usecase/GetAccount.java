package com.domain.account.usecase;

import com.domain.account.exception.AccountDomainErrorCode;
import com.domain.account.exception.AccountDomainException;
import com.domain.account.model.Account;
import com.domain.account.port.AccountPort;
import com.domain.account.port.query.AccountQuery;
import com.domain.account.usecase.request.AccountRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class GetAccount implements Usecase<Account, AccountRequest> {

    private final AccountPort accountPort;

    @Override
    public Account execute(AccountRequest request) {
        return accountPort.getAccount(new AccountQuery(request.ownerId())).orElseThrow(() ->
                new AccountDomainException(AccountDomainErrorCode.ACCOUNT_NOT_FOUND, String.format("Account with ownerId %s not found", request.ownerId())));
    }
}
