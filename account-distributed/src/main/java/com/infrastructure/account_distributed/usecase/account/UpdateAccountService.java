package com.infrastructure.account_distributed.usecase.account;

import com.domain.account.exception.AccountDomainErrorCode;
import com.domain.account.exception.AccountDomainException;
import com.domain.account.usecase.request.AccountUpdateRequest;
import com.infrastructure.account_distributed.database.entity.AccountEntity;
import com.infrastructure.account_distributed.database.repository.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateAccountService{

    private final AccountService accountService;

    @Transactional
    public void execute(AccountUpdateRequest request) {
        AccountEntity account = accountService.findByOwnerId(request.ownerId())
                .orElseThrow(() -> new AccountDomainException(AccountDomainErrorCode.ACCOUNT_NOT_FOUND, String.format("Account with owner id: %s not found", request.ownerId())));

        account.setBalance(request.amount());
        accountService.save(account);
    }
}
