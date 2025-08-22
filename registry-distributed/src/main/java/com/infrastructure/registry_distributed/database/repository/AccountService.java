package com.infrastructure.registry_distributed.database.repository;

import com.infrastructure.registry_distributed.database.entity.AccountEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    @Transactional
    public Optional<AccountEntity> findByOwnerIdForUpdate(Long ownerId) {
        return accountRepository.findByOwnerIdForUpdate(ownerId);
    }

    @Transactional(readOnly = true)
    public Optional<AccountEntity> findByOwnerId(Long ownerId) {
        return accountRepository.findByOwnerId(ownerId);
    }

    @Transactional(readOnly = true)
    public Page<AccountEntity> findAll(Pageable pageable) {
        return accountRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Long count() {
        return accountRepository.count();
    }

    @Transactional
    public AccountEntity save(AccountEntity originator) {
        return accountRepository.save(originator);
    }

    @Transactional
    public void saveAll(List<AccountEntity> accountEntities) {
        accountRepository.saveAll(accountEntities);
    }
}
