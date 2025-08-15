package com.infrastructure.monolith.database.repository;

import com.infrastructure.monolith.database.entity.TransferEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferRepository extends JpaRepository<TransferEntity, Long> { }
