package com.sep490.gshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "request_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestItem extends BaseEntity {
    @Column(columnDefinition = "TEXT")
    private String productURL;
    private String productName;
    @Column(columnDefinition = "TEXT")
    private String contactInfo;
    @ElementCollection
    private List<String> images;
    private String variants;
    @Column(columnDefinition = "TEXT")
    private String description;
    private int quantity;


    @ManyToOne
    @JoinColumn(name = "purchase_request_id", nullable = false)
    private PurchaseRequest purchaseRequest;

    @ManyToOne
    @JoinColumn(name = "sub_request_id")
    private SubRequest subRequest;
}
