package com.sep490.gshop.entity;

import com.sep490.gshop.common.enums.RefundStatus;
import com.sep490.gshop.entity.subclass.BankAccountSnapshot;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
    @Enumerated(EnumType.STRING)
    private RefundStatus status;
    private BankAccountSnapshot bankAccount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;


    public RefundTicket(List<String> evidence, String reason, double amount, RefundStatus status, BankAccountSnapshot bankAccount){
        this.evidence = evidence;
        this.reason = reason;
        this.amount = amount;
        this.status = status;
        this.bankAccount = bankAccount;
    }
}
