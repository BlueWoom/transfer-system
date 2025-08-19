package com.infrastructure.monolith.database.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Check;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
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
@Check(constraints = "originator_id <> beneficiary_id")
public class TransferEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID transferId;

    @Column(nullable = false, updatable = false)
    private UUID requestId;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(precision = 19, scale = 4, updatable = false)
    private BigDecimal transferAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "originator_id", referencedColumnName = "owner_id", updatable = false)
    private AccountEntity originator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "beneficiary_id", referencedColumnName = "owner_id", updatable = false)
    private AccountEntity beneficiary;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransferStatus status;

    @Column
    private OffsetDateTime processedAt;

    @Column(precision = 19, scale = 10)
    private BigDecimal exchangeRate;

    @Column(precision = 19, scale = 4)
    private BigDecimal debit;

    @Column(precision = 19, scale = 4)
    private BigDecimal credit;

    @Column
    private String errorCode;
}
