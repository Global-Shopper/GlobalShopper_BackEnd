package com.sep490.gshop.payload.dto;

import com.sep490.gshop.common.enums.PackageType;
import com.sep490.gshop.common.enums.QuotationType;
import com.sep490.gshop.common.enums.SubRequestStatus;
import com.sep490.gshop.entity.subclass.Fee;
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
public class OfflineQuotationDTO {
    private String id;
    private String note;
    private String subRequestId;
    private long expiredDate;
    private List<OfflineQuotationDetailDTO> details;
    private double shippingEstimate;
    private double totalPriceEstimate;
    private SubRequestStatus subRequestStatus;
    private String region;
    private String currency;
    private Double totalWeightEstimate;
    private PackageType packageType;
    private QuotationType quotationType;
    private ShipperInformation shipper;
    private RecipientInformation recipient;
    private List<Fee> fees;
}
