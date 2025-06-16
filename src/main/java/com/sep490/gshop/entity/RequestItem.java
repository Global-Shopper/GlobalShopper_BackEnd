package com.sep490.gshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "request_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestItem extends BaseEntity {
    @Column(columnDefinition = "TEXT")
    private String productURL;
    private String productName;
    private String contactInfo;
    private String productSpecification;
    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToOne(mappedBy = "requestItem", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Quotation quotation;

    @ManyToOne
    @JoinColumn(name = "purchase_request_id", nullable = false)
    private PurchaseRequest purchaseRequest;

    @ManyToOne
    @JoinColumn(name = "sub_request_id", nullable = false)
    private SubRequest subRequest;
}
