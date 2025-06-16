package com.sep490.gshop.entity;

import com.sep490.gshop.common.RefundStatus;
import com.sep490.gshop.entity.subclass.BankAccountSnapshot;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "refund_tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefundTicket extends BaseEntity {
    private String evidence;
    private String reason;
    private double amount;
    private RefundStatus status;
    private BankAccountSnapshot bankAccount;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;


    public RefundTicket(String evidence, String reason, double amount, RefundStatus status, BankAccountSnapshot bankAccount){
        this.evidence = evidence;
        this.reason = reason;
        this.amount = amount;
        this.status = status;
        this.bankAccount = bankAccount;
    }
}
