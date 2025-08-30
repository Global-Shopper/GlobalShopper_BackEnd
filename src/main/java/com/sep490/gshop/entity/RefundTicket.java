package com.sep490.gshop.entity;

import com.sep490.gshop.common.enums.RefundStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "refund_tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundTicket extends BaseEntity {
    @ElementCollection
    @CollectionTable(name = "refund_ticket_evidence", joinColumns = @JoinColumn(name = "refund_ticket_id"))
    @Column(name = "evidence", columnDefinition = "TEXT")
    private List<String> evidence;
    private String reason;
    private double amount;
    @DecimalMin(value = "0.0", inclusive = false)
    @DecimalMax(value = "1.0", inclusive = true)
    private Double refundRate;
    private String rejectionReason;
    @Enumerated(EnumType.STRING)
    private RefundStatus status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
}
