package com.sep490.gshop.payload.dto;

import com.sep490.gshop.common.enums.ShipmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentTrackingEventDto {

    private String id;
    private String trackingNumber;
    private String eventCode;
    private String eventDescription;
    private Long eventTime;
    private String city;
    private String country;
    private ShipmentStatus shipmentStatus;
    private Long createdAt;

}