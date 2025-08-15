package com.domain.transfer.port;

import com.domain.transfer.model.Account;
import com.domain.transfer.port.query.AccountQuery;

import java.util.Optional;

public interface AccountPort {

    Optional<Account> getAccount(AccountQuery accountQuery);

    Optional<Account> getAccountByIdForUpdate(AccountQuery query);

    void save(Account updatedOriginator);
}
