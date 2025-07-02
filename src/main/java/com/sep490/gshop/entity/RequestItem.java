package com.sep490.gshop.entity;

import com.sep490.gshop.entity.converter.StringListConverter;
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
    @Convert(converter = StringListConverter.class)
    private List<String> variants;
    @Column(columnDefinition = "TEXT")
    private String description;
    private int quantity;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_request_id", nullable = false)
    private PurchaseRequest purchaseRequest;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_request_id")
    private SubRequest subRequest;
}
