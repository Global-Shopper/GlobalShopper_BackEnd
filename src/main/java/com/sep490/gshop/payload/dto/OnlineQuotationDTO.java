package com.sep490.gshop.payload.dto;

import com.sep490.gshop.common.enums.QuotationType;
import com.sep490.gshop.common.enums.SubRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OnlineQuotationDTO {
    private String id;
    private String subRequestId;
    private double shippingEstimate;
    private long expiredDate;
    private String note;
    private String currency;
    private double totalPriceEstimate;
    private List<String> fees;
    private List<OnlineQuotationDetailDTO> details;
    private SubRequestStatus subRequestStatus;
    private QuotationType quotationType;
}
