package com.sep490.gshop.entity;

import com.sep490.gshop.common.enums.OrderStatus;
import com.sep490.gshop.entity.converter.StringListConverter;
import com.sep490.gshop.entity.subclass.AddressSnapshot;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends BaseEntity {

    // Order tracking information
    private String trackingNumber;
    private String orderCode;
    private String shippingCarrier;

    //Store information
    @Convert(converter = StringListConverter.class)
    private List<String> contactInfo;
    private String seller;
    private String ecommercePlatform;

    @Column(columnDefinition = "TEXT")
    private String note;
    private OrderStatus status;


    // Order financial information
    private double totalPrice;
    private double shippingFee;

    // Relationships
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;

    @Embedded
    private AddressSnapshot shippingAddress;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("createdAt DESC")
    private List<OrderHistory> history;

}
