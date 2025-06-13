package com.sep490.gshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

}
