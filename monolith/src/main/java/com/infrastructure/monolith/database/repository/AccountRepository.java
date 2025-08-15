package com.infrastructure.monolith.database.repository;

import com.infrastructure.monolith.database.entity.AccountEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    Optional<AccountEntity> findByOwnerId(Long ownerId);

    Page<AccountEntity> findAll(Pageable pageable);
}
