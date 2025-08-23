package com.infrastructure.transfer_distributed.api;

import com.domain.accept.model.AcceptedTransfer;
import com.domain.accept.usecase.AcceptTransfer;
import com.infrastructure.transfer_distributed.api.dto.TransferDTO;
import com.infrastructure.transfer_distributed.api.dto.TransferRequestDTO;
import com.infrastructure.transfer_distributed.api.mapper.AcceptTransferMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TransferController {

    private final AcceptTransfer acceptTransfer;

    @PostMapping("/send-request-transfer")
    public ResponseEntity<TransferDTO> performTransfer(@RequestHeader("Idempotency-Key") UUID idempotencyKey, @RequestBody TransferRequestDTO dto) {
        AcceptedTransfer acceptedRequest = acceptTransfer.execute(AcceptTransferMapper.INSTANCE.mapFromDtoToModel(dto, idempotencyKey));
        return ResponseEntity.ok(AcceptTransferMapper.INSTANCE.mapFromModelToDto(acceptedRequest));
    }
}
