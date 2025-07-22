package com.sep490.gshop.payload.dto;

import com.sep490.gshop.payload.request.QuotationDetailRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuotationDTO {
    private String id;
    private String note;
    private String subRequestId;
    private long expiredDate;
    private List<QuotationDetailDTO> details;
    private double shippingEstimate;
    private double totalPriceEstimate;
}
