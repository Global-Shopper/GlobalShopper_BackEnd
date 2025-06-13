package com.sep490.gshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "wallets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Wallet extends BaseEntity {

    private double balance;

    @OneToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

}
