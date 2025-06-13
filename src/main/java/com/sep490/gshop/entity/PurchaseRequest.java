package com.sep490.gshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "purchase_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseRequest extends BaseEntity {
    @Column(columnDefinition = "TEXT")
    private String productURL;
    private String productName;
    private String contactInfo;
    private String productSpecification;
    @Column(columnDefinition = "TEXT")
    private String description;

    private long expiredAt;

    @ManyToOne
    @JoinColumn(name = "shipping_address_id", nullable = false)
    private ShippingAddress shippingAddress;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @OneToOne(mappedBy = "purchaseRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Quotation quotation;
}
