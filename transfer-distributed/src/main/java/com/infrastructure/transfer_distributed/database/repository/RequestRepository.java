package com.infrastructure.transfer_distributed.database.repository;

import com.infrastructure.transfer_distributed.database.entity.RequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface RequestRepository extends JpaRepository<RequestEntity, Long> {

    boolean existsByRequestId(UUID requestId);

    Optional<RequestEntity> findByRequestId(UUID requestId);
}
