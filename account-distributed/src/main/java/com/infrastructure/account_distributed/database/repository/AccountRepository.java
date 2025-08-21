package com.infrastructure.account_distributed.database.repository;

import com.infrastructure.account_distributed.database.entity.AccountEntity;
import jakarta.annotation.Nonnull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    Optional<AccountEntity> findByOwnerId(Long ownerId);

    @Nonnull
    Page<AccountEntity> findAll(@Nonnull Pageable pageable);
}
