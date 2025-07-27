package com.sep490.gshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(exclude = "subRequest", callSuper = false)
@Entity
@Table(name = "quotations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Quotation extends BaseEntity {

    @Column(columnDefinition = "TEXT")
    private String note;
    private double shippingEstimate;
    private long expiredDate;
    private double totalPriceEstimate;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_request_id", nullable = false, unique = true)
    private SubRequest subRequest;

    @OneToMany(mappedBy = "quotation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<QuotationDetail> details = new ArrayList<>();
}

