package com.sep490.gshop.entity;

import com.sep490.gshop.common.enums.PurchaseRequestStatus;
import com.sep490.gshop.common.enums.RequestType;
import com.sep490.gshop.entity.subclass.AddressSnapshot;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "purchase_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseRequest extends BaseEntity {

    private long expiredAt;
    
    @Column(columnDefinition = "TEXT")
    private String correctionNote;

    @Embedded
    private AddressSnapshot shippingAddress;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Admin admin;

    @OneToMany(mappedBy = "purchaseRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RequestItem> requestItems;

    @Enumerated(EnumType.STRING)
    private PurchaseRequestStatus status;

    @Enumerated(EnumType.STRING)
    private RequestType requestType;

    @OneToMany(mappedBy = "purchaseRequest", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("createdAt DESC")
    private List<PurchaseRequestHistory> history = new ArrayList<>();

}
