package com.sep490.gshop.payload.request.quotation;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OnlineQuotationDetailRequest {
    private String requestItemId;
    @DecimalMin(value = "0.1", inclusive = false, message = "basePrice phải lớn hơn 0")
    @Positive(message = "basePrice phải lớn hơn 0")
    private double basePrice;
}
