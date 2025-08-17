package com.infrastructure.monolith.usecase.account;

import com.domain.account.model.Account;
import com.domain.account.port.AccountPort;
import com.domain.account.port.query.AccountPageQuery;
import com.domain.account.usecase.GetAccountPage;
import com.domain.account.usecase.request.PageResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetAccountPageService extends GetAccountPage {

    public GetAccountPageService(AccountPort accountPort) {
        super(accountPort);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<Account> execute(AccountPageQuery request) {
        return super.execute(request);
    }
}
