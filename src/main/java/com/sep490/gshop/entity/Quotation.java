package com.sep490.gshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "quotations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Quotation extends BaseEntity{
    private double basePrice;
    private double serviceFee;
    private double tax;
    @Column(columnDefinition = "TEXT")
    private String taxDetails;
    @Column(columnDefinition = "TEXT")
    private String note;
    private boolean isAccepted;

    @OneToOne
    @JoinColumn(name = "request_item_id", nullable = false)
    private RequestItem requestItem;

}
