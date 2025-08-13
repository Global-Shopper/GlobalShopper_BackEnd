package com.sep490.gshop.entity.subclass;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipperInformation {
    private String shipmentStreetLine;
    private String shipmentCity;
    private String shipmentCountryCode;
    private String shipmentPostalCode;
    private String shipmentPhone;
    private String shipmentName;
}
