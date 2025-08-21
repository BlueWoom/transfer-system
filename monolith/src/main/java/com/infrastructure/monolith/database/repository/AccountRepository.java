package com.infrastructure.monolith.database.repository;

import com.infrastructure.monolith.database.entity.AccountEntity;
import jakarta.annotation.Nonnull;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM AccountEntity a WHERE a.ownerId = :ownerId")
    Optional<AccountEntity> findByOwnerIdForUpdate(Long ownerId);

    Optional<AccountEntity> findByOwnerId(Long ownerId);

    @Nonnull
    Page<AccountEntity> findAll(@Nonnull Pageable pageable);
}
