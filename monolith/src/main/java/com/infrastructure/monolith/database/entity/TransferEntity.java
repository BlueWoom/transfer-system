package com.infrastructure.monolith.database.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        indexes = {
                @Index(name = "idx_request_status", columnList = "requestId, status"),
                @Index(name = "idx_transfer_status", columnList = "transferId, status")
        }
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Check(constraints = "originator_id <> beneficiary_id")
public class TransferEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID transferId;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID requestId;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false, precision = 19, scale = 4, updatable = false)
    private BigDecimal transferAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "originator_id", referencedColumnName = "owner_id", nullable = false, updatable = false)
    private AccountEntity originator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "beneficiary_id", referencedColumnName = "owner_id", nullable = false, updatable = false)
    private AccountEntity beneficiary;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransferStatus status;

    @Column(updatable = false)
    private OffsetDateTime processedAt;

    @Column(precision = 19, scale = 10, updatable = false)
    private BigDecimal exchangeRate;

    @Column(precision = 19, scale = 4, updatable = false)
    private BigDecimal debit;

    @Column(precision = 19, scale = 4, updatable = false)
    private BigDecimal credit;
}
