package com.infrastructure.monolith.api;

import com.domain.accept.model.AcceptedTransfer;
import com.domain.registry.model.SuccessfulTransfer;
import com.infrastructure.monolith.api.dto.TransferDTO;
import com.infrastructure.monolith.api.dto.TransferRequestDTO;
import com.infrastructure.monolith.api.mapper.AcceptTransferMapper;
import com.infrastructure.monolith.api.mapper.RegistryMapper;
import com.infrastructure.monolith.usecase.accept.AcceptTransferService;
import com.infrastructure.monolith.usecase.registry.ProcessTransferService;
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

    private final AcceptTransferService acceptTransferService;

    private final ProcessTransferService processTransferService;

    @PostMapping("/transfer")
    public ResponseEntity<TransferDTO> performTransfer(@RequestHeader("Idempotency-Key") UUID idempotencyKey, @RequestBody TransferRequestDTO dto) {
        AcceptedTransfer acceptedTransfer = acceptTransferService.execute(AcceptTransferMapper.INSTANCE.mapFromDtoToModel(dto, idempotencyKey));
        SuccessfulTransfer successfulTransfer = processTransferService.execute(RegistryMapper.INSTANCE.mapFromDtoToModel(acceptedTransfer));
        return ResponseEntity.ok(RegistryMapper.INSTANCE.mapFromModelToDto(successfulTransfer));
    }
}
