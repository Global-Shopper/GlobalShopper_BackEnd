package com.sep490.gshop.payload.request.quotation;

import com.sep490.gshop.entity.subclass.Fee;
import com.sep490.gshop.payload.request.FeeRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OnlineQuotationRequest {
    private String subRequestId;
    private double shippingEstimate;
    private long expiredDate;
    private String currency;
    private String note;
    private List<FeeRequest> fees;
    private List<OnlineQuotationDetailRequest> details;
}
