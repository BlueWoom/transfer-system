package com.infrastructure.transfer_distributed.api;

import com.infrastructure.transfer_distributed.api.dto.TransferDTO;
import com.infrastructure.transfer_distributed.queue.message.TransferRequestMessage;
import com.infrastructure.transfer_distributed.api.dto.TransferRequestDTO;
import com.infrastructure.transfer_distributed.api.mapper.RegistryMapper;
import com.infrastructure.transfer_distributed.queue.TransferRequestProducer;
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

    private final TransferRequestProducer transferRequestProducer;

    @PostMapping("/send-request-transfer")
    public ResponseEntity<TransferDTO> performTransfer(@RequestHeader("Idempotency-Key") UUID idempotencyKey, @RequestBody TransferRequestDTO transferRequestDTO) {
        TransferRequestMessage transferRequestMessage = RegistryMapper.INSTANCE.mapFromDtoToMessage(transferRequestDTO, idempotencyKey);
        transferRequestProducer.sendTransferRequest(transferRequestMessage);
        TransferDTO transfer = RegistryMapper.INSTANCE.mapFromMessageToDTO(transferRequestMessage);
        return ResponseEntity.ok(transfer);
    }
}
