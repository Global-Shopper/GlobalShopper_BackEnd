package com.sep490.gshop.entity.subclass;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipientInformation {
    private String recipientStreetLine;
    private String recipientCity;
    private String recipientCountryCode;
    private String recipientPostalCode;
    private String recipientPhone;
    private String recipientName;
}
