package com.sep490.gshop.entity;

import com.sep490.gshop.common.enums.OrderStatus;
import com.sep490.gshop.common.enums.RequestType;
import com.sep490.gshop.entity.converter.StringListConverter;
import com.sep490.gshop.entity.subclass.AddressSnapshot;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = false)
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
    private String currency;
    private String region;
    @Column(columnDefinition = "TEXT")
    private String note;
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.ORDER_REQUESTED;

    @Enumerated(EnumType.STRING)
    private RequestType type;


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
    private List<OrderHistory> history = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("createdAt DESC")
    private List<ShipmentTrackingEvent> shipmentTrackingEvents = new ArrayList<>();

}
