package com.infrastructure.monolith.usecase.registry.adapter;

import com.domain.registry.model.Account;
import com.domain.registry.model.Currency;
import com.domain.registry.model.FailedTransfer;
import com.domain.registry.model.SuccessfulTransfer;
import com.domain.registry.port.RegistryPort;
import com.domain.registry.port.query.AccountQuery;
import com.domain.registry.port.query.TransferRequestQuery;
import com.infrastructure.monolith.database.entity.AccountEntity;
import com.infrastructure.monolith.database.entity.TransferEntity;
import com.infrastructure.monolith.database.repository.AccountRepository;
import com.infrastructure.monolith.database.repository.TransferRepository;
import com.infrastructure.monolith.external.ExchangeRateService;
import com.infrastructure.monolith.usecase.registry.mapper.RegistryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegistryAdapter implements RegistryPort {

    private final AccountRepository accountRepository;

    private final TransferRepository transferRepository;

    private final ExchangeRateService exchangeRateService;

    @Override
    public boolean checkIfRequestExist(TransferRequestQuery transferRequestQuery) {
        return transferRepository.existsByRequestId(transferRequestQuery.requestId());
    }

    @Override
    public void createFailedTransfer(FailedTransfer transfer) {
        transferRepository.save(RegistryMapper.INSTANCE.mapFromModelToEntity(transfer));
    }

    @Override
    public void createSuccessfulTransfer(SuccessfulTransfer transfer) {
        TransferEntity toCreate = RegistryMapper.INSTANCE.mapFromModelToEntity(transfer);

        AccountEntity originatorTransfer = accountRepository.findByOwnerId(transfer.getOriginator().ownerId())
                .orElseThrow(() -> new IllegalArgumentException("Originator account not found"));

        AccountEntity beneficiaryTransfer = accountRepository.findByOwnerId(transfer.getBeneficiary().ownerId())
                .orElseThrow(() -> new IllegalArgumentException("Beneficiary account not found"));

        toCreate.setOriginator(originatorTransfer);
        toCreate.setBeneficiary(beneficiaryTransfer);
        transferRepository.save(toCreate);
    }

    @Override
    public Optional<Account> getAccountByIdForUpdate(AccountQuery query) {
        return accountRepository.findByOwnerIdForUpdate(query.ownerId())
                .map(RegistryMapper.INSTANCE::mapFromModelToEntity);
    }

    @Override
    public void updateAccount(Account account) {
        AccountEntity entity = RegistryMapper.INSTANCE.mapFromModelToEntity(account);

        AccountEntity toUpdate = accountRepository.findByOwnerIdForUpdate(account.ownerId())
                        .orElseThrow(() -> new IllegalArgumentException("Account not found"));


        toUpdate.setBalance(entity.getBalance());
        accountRepository.save(toUpdate);
    }

    // Exchange rate operations

    @Override
    @Cacheable(value = "${exchange-api.cache.name}", key = "#source.value + '-' + #destination.value")
    public Optional<BigDecimal> getExchangeRate(Currency source, Currency destination) {
        return exchangeRateService.getExchangeRate(source.getValue(), destination.getValue());
    }
}

