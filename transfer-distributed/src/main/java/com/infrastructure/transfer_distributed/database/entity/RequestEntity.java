package com.infrastructure.transfer_distributed.database.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        indexes = {
                @Index(name = "idx_request", columnList = "requestId")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID transferId;

    @Column(nullable = false, updatable = false)
    private UUID requestId;
}
