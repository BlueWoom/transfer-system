package com.domain.transfer.port;

import com.domain.transfer.model.Account;
import com.domain.transfer.model.Currency;
import com.domain.transfer.model.PendingTransfer;
import com.domain.transfer.port.query.AccountQuery;
import com.domain.transfer.port.query.TransferQuery;

import java.math.BigDecimal;
import java.util.Optional;

public interface TransferPort {

    // Transfer operations

    boolean checkIfRequestExist(TransferQuery transferQuery);

    void createPendingTransfer(PendingTransfer transfer);

    // Account operations

    Optional<Account> getAccount(AccountQuery query);

    // Exchange rate operations

    Optional<BigDecimal> getExchangeRate(Currency source, Currency destination);
}
