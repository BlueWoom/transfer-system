package com.infrastructure.transfer_distributed.database.repository;

import com.infrastructure.transfer_distributed.database.entity.RequestEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;

    @Transactional(readOnly = true)
    public boolean existsByRequestId(UUID uuid) {
        return requestRepository.existsByRequestId(uuid);
    }

    @Transactional(readOnly = true)
    public Optional<RequestEntity> findByRequestId(UUID requestId) {
        return requestRepository.findByRequestId(requestId);
    }

    @Transactional
    public RequestEntity save(RequestEntity request) {
        return requestRepository.save(request);
    }
}
