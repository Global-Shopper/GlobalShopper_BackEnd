package com.sep490.gshop.entity;

import com.sep490.gshop.common.RefundStatus;
import jakarta.persistence.Entity;
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
}
