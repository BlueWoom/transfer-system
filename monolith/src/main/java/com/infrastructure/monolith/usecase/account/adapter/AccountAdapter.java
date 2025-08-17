package com.infrastructure.monolith.usecase.account.adapter;

import com.domain.account.model.Account;
import com.domain.account.port.AccountPort;
import com.domain.account.port.query.AccountPageQuery;
import com.domain.account.port.query.AccountQuery;
import com.domain.account.usecase.request.PageResult;
import com.infrastructure.monolith.database.repository.AccountRepository;
import com.infrastructure.monolith.usecase.account.mapper.AccountDomainMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AccountAdapter implements AccountPort {

    private final AccountRepository accountRepository;

    @Override
    public Optional<Account> getAccount(AccountQuery query) {
        return accountRepository.findByOwnerId(query.ownerId())
                .map(AccountDomainMapper.INSTANCE::mapFromEntityToModel);
    }

    @Override
    public PageResult<Account> getAllAccounts(AccountPageQuery request) {
        Pageable pageable = PageRequest.of(request.pageNumber(), request.pageSize());

        Page<Account> accountPage = accountRepository.findAll(pageable)
                .map(AccountDomainMapper.INSTANCE::mapFromEntityToModel);

        return new PageResult<>(accountPage.getContent(), accountPage.getTotalElements(), accountPage.getTotalPages());
    }
}
