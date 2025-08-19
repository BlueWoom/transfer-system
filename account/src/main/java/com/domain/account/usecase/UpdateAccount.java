package com.domain.account.usecase;

import com.domain.account.exception.AccountDomainErrorCode;
import com.domain.account.exception.AccountDomainException;
import com.domain.account.model.Account;
import com.domain.account.port.AccountPort;
import com.domain.account.port.query.AccountQuery;
import com.domain.account.port.query.UpdateAccountQuery;
import com.domain.account.usecase.request.AccountUpdateRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class UpdateAccount implements Usecase<Account, AccountUpdateRequest> {

    private final AccountPort accountPort;

    @Override
    public Account execute(AccountUpdateRequest request) {
        Account account = accountPort.getAccount(new AccountQuery(request.ownerId())).orElseThrow(() ->
                new AccountDomainException(AccountDomainErrorCode.ACCOUNT_NOT_FOUND, String.format("Account with ownerId %s not found", request.ownerId())));

        Account toUpdate = new Account(account.ownerId(), account.currency(), request.amount());
        accountPort.updateAccount(new UpdateAccountQuery(toUpdate.ownerId(), request.amount()));
        return toUpdate;
    }
}
