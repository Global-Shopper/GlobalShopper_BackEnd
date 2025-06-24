package com.sep490.gshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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


    @ManyToOne
    @JoinColumn(name = "purchase_request_id", nullable = false)
    private PurchaseRequest purchaseRequest;

    @ManyToOne
    @JoinColumn(name = "sub_request_id")
    private SubRequest subRequest;
}
