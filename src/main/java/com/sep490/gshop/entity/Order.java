package com.sep490.gshop.entity;

import com.sep490.gshop.common.enums.OrderStatus;
import com.sep490.gshop.entity.subclass.AddressSnapshot;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order extends BaseEntity {

    private String trackingNumber;
    private String orderCode;
    @Column(columnDefinition = "TEXT")
    private String note;
    private OrderStatus status;
    private double totalPrice;
    private double shippingFee;
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;

    @Embedded
    private AddressSnapshot shippingAddress;

}
