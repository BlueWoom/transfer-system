package com.infrastructure.monolith.database.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;

import java.math.BigDecimal;

@Table
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Check(constraints = "balance >= 0")
public class AccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "owner_id", nullable = false, unique = true, updatable = false)
    private Long ownerId;

    @Column(nullable = false, updatable = false)
    private String currency;

    @Builder.Default
    @Column(nullable = false, columnDefinition = "DECIMAL(19, 4) DEFAULT 0.00")
    private BigDecimal balance = BigDecimal.ZERO;
}
