package com.infrastructure.monolith.usecase.account;

import com.domain.account.model.Account;
import com.domain.account.port.AccountPort;
import com.domain.account.usecase.GetAccount;
import com.domain.account.usecase.request.AccountRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetAccountUsecase extends GetAccount {

    public GetAccountUsecase(AccountPort accountPort) {
        super(accountPort);
    }

    @Override
    @Transactional(readOnly = true)
    public Account execute(AccountRequest request) {
        return super.execute(request);
    }
}
