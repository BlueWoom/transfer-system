package com.infrastructure.account_distributed.database.repository;

import com.infrastructure.account_distributed.database.entity.AccountEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    @Transactional(readOnly = true)
    public Optional<AccountEntity> findByOwnerId(Long ownerId) {
        return accountRepository.findByOwnerId(ownerId);
    }

    @Transactional(readOnly = true)
    public Page<AccountEntity> findAll(Pageable pageable) {
        return accountRepository.findAll(pageable);
    }

    public AccountEntity save(AccountEntity account) {
        return accountRepository.save(account);
    }
}
