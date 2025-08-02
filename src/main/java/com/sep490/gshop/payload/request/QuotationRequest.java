package com.sep490.gshop.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.*;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuotationRequest {
    @NotNull(message = "subRequestId không được để trống")
    private String subRequestId;

    @Size(max = 500, message = "Note không được vượt quá 500 ký tự")
    private String note;

    @NotNull(message = "Details không được để trống")
    @Size(min = 1, message = "Phải có ít nhất một phần tử trong details")
    private List<QuotationDetailRequest> details;

    @PositiveOrZero(message = "shippingEstimate phải là số dương hoặc bằng 0")
    private Double shippingEstimate;

    @Positive(message = "expiredDate phải lớn hơn 0")
    private long expiredDate;


}