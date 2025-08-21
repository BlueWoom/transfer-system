package com.infrastructure.account_distributed.database.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Check;

import java.math.BigDecimal;

@Table
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Check(constraints = "balance >= 0")
public class AccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(name = "owner_id", nullable = false, unique = true, updatable = false)
    private Long ownerId;

    @Column(nullable = false, updatable = false)
    private String currency;

    @Column(nullable = false)
    private BigDecimal balance;
}
