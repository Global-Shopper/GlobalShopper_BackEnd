package com.sep490.gshop.entity;

import com.sep490.gshop.common.enums.TransactionStatus;
import com.sep490.gshop.common.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    private TransactionType type;
    private double amount;
    private double balanceBefore;
    private double balanceAfter;
    private String referenceCode;


    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

}
