package com.domain.registry.port;

import com.domain.registry.model.*;
import com.domain.registry.port.query.AccountQuery;
import com.domain.registry.port.query.TransferRequestQuery;

import java.math.BigDecimal;
import java.util.Optional;

public interface RegistryPort {

    // Transfer operations

    boolean checkIfRequestExist(TransferRequestQuery transferRequestQuery);

    void createFailedTransfer(FailedTransfer transfer);

    void createSuccessfulTransfer(SuccessfulTransfer transfer);

    // Account operations

    Optional<Account> getAccountByIdForUpdate(AccountQuery query);

    void updateAccount(Account account);

    // Exchange rate operations

    Optional<BigDecimal> getExchangeRate(Currency source, Currency destination);
}
