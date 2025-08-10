package com.sep490.gshop.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CancelModel {
    @NotBlank(message = "Lý do từ chối không được để trống")
    private String rejectionReason;
}
