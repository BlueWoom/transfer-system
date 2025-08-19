package com.infrastructure.account_distributed.usecase.account;

import com.domain.account.model.Account;
import com.domain.account.port.AccountPort;
import com.domain.account.usecase.UpdateAccount;
import com.domain.account.usecase.request.AccountUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateAccountService extends UpdateAccount {

    public UpdateAccountService(AccountPort accountPort, AccountPort accountPort1) {
        super(accountPort);
    }

    @Override
    @Transactional
    public Account execute(AccountUpdateRequest request) {
        return super.execute(request);
    }
}
