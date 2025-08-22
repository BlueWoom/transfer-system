package com.infrastructure.registry_distributed.database.repository;

import com.infrastructure.registry_distributed.database.entity.TransferEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final TransferRepository transferRepository;

    @Transactional(readOnly = true)
    public Optional<TransferEntity> getByTransferId(UUID uuid) {
        return transferRepository.getByTransferId(uuid);
    }

    @Transactional
    public void save(TransferEntity transferEntity) {
        transferRepository.save(transferEntity);
    }
}
