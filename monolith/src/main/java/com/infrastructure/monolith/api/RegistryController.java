package com.infrastructure.monolith.api;

import com.domain.registry.model.SuccessfulTransfer;
import com.domain.registry.usecase.request.ProcessTransferRequest;
import com.infrastructure.monolith.api.dto.TransferDTO;
import com.infrastructure.monolith.api.dto.TransferRequestDTO;
import com.infrastructure.monolith.api.mapper.RegistryMapper;
import com.infrastructure.monolith.usecase.AcceptAndProcessTransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class RegistryController {

    private final AcceptAndProcessTransferService acceptAndProcessTransferService;

    @PostMapping("/transfer")
    public ResponseEntity<TransferDTO> performTransfer(@RequestHeader("Idempotency-Key") UUID idempotencyKey, @RequestBody TransferRequestDTO transferRequestDTO) {
        ProcessTransferRequest transferRequest = RegistryMapper.INSTANCE.mapFromDtoToModel(transferRequestDTO, idempotencyKey);
        SuccessfulTransfer successfulTransfer = acceptAndProcessTransferService.execute(transferRequest);
        return ResponseEntity.ok(RegistryMapper.INSTANCE.mapFromModelToDto(successfulTransfer));
    }
}
