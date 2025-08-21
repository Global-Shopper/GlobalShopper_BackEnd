package com.sep490.gshop.entity;

import com.sep490.gshop.common.enums.ShipmentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "shipment_tracking_event")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentTrackingEvent extends BaseEntity {

    @Column(name = "tracking_number", nullable = false, length = 50)
    private String trackingNumber;

    @Column(name = "event_code", length = 10)
    private String eventCode; // IT, AR, DL...

    @Column(name = "event_description", length = 255)
    private String eventDescription;

    @Column(name = "event_time")
    private Long eventTime;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "country", length = 10)
    private String country;

    @Enumerated(EnumType.STRING)
    private ShipmentStatus shipmentStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
}
