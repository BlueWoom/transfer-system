package com.infrastructure.monolith.database.repository;

import com.infrastructure.monolith.database.entity.TransferEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface TransferRepository extends JpaRepository<TransferEntity, Long> {

    Optional<TransferEntity> getByTransferId(UUID uuid);
}
