package com.sep490.gshop.entity;

import com.sep490.gshop.common.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "order_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderHistory extends BaseEntity{
    private String description;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    public OrderHistory(Order order, String description) {
        this.order = order;
        this.description = description;
        this.status = order.getStatus();
    }
}
