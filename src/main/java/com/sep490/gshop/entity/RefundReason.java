package com.sep490.gshop.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "refund_reasons")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RefundReason extends BaseEntity {
    private String reason;
    private Double rate;
    private Boolean active;
}
