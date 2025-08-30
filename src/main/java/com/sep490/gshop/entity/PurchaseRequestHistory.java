package com.sep490.gshop.entity;

import com.sep490.gshop.common.enums.PurchaseRequestStatus;
import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "purchase_request_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseRequestHistory extends BaseEntity{

    private String description;
    @Enumerated(EnumType.STRING)
    private PurchaseRequestStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_request_id", nullable = false)
    private PurchaseRequest purchaseRequest;

    public PurchaseRequestHistory(PurchaseRequest purchaseRequest, String description) {
        this.purchaseRequest = purchaseRequest;
        this.description = description;
        this.status = purchaseRequest.getStatus();
    }
}
