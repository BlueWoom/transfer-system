package com.infrastructure.transfer_distributed.api;

import com.domain.accept.model.AcceptedTransfer;
import com.infrastructure.transfer_distributed.api.dto.TransferDTO;
import com.infrastructure.transfer_distributed.api.dto.TransferRequestDTO;
import com.infrastructure.transfer_distributed.api.mapper.AcceptTransferMapper;
import com.infrastructure.transfer_distributed.queue.TransferRequestProducer;
import com.infrastructure.transfer_distributed.queue.message.TransferRequestMessage;
import com.infrastructure.transfer_distributed.usecase.accept.AcceptTransferService;
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

    private final AcceptTransferService acceptTransferService;

    private final TransferRequestProducer transferRequestProducer;

    @PostMapping("/send-request-transfer")
    public ResponseEntity<TransferDTO> performTransfer(@RequestHeader("Idempotency-Key") UUID idempotencyKey, @RequestBody TransferRequestDTO dto) {
        AcceptedTransfer acceptedRequest = acceptTransferService.execute(AcceptTransferMapper.INSTANCE.mapFromDtoToModel(dto, idempotencyKey));
        transferRequestProducer.sendTransferRequest(AcceptTransferMapper.INSTANCE.mapFromModelToMessage(acceptedRequest));
        return ResponseEntity.ok(AcceptTransferMapper.INSTANCE.mapFromModelToDto(acceptedRequest));
    }
}
