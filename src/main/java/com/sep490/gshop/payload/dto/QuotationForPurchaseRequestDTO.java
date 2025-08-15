package com.sep490.gshop.payload.dto;

import com.sep490.gshop.common.enums.PackageType;
import com.sep490.gshop.common.enums.QuotationType;
import com.sep490.gshop.common.enums.SubRequestStatus;
import com.sep490.gshop.entity.subclass.RecipientInformation;
import com.sep490.gshop.entity.subclass.ShipperInformation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuotationForPurchaseRequestDTO {
    private String id;
    private String note;
    private long expiredDate;
    private double shippingEstimate;
    private double totalPriceEstimate;
    private List<String> fees;

    private Double totalWeightEstimate;
    private PackageType packageType;
    private QuotationType quotationType;
    private ShipperInformation shipper;
    private RecipientInformation recipient;
}
