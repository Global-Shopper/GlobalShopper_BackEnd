package com.sep490.gshop.payload.request.quotation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RejectQuotationRequest {
    @NotNull(message = "subRequestId không được để trống")
    private String subRequestId;
    @NotBlank(message = "Lý do từ chối không được để trống")
    private String rejectionReason;
}
