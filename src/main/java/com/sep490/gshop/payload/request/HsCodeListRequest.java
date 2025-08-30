package com.sep490.gshop.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HsCodeListRequest {
    @NotBlank(message = "HSCode không được để trống")
    @Size(min = 4, max = 8, message = "HSCode có độ dài từ 4 đến 8 ký tự theo quy định của nhà nước")
    private String hsCode;

    @NotBlank(message = "Mô tả không được để trống")
    private String description;

    @NotBlank(message = "Đơn vị tính không được để trống")
    private String unit;

    private String parentCode;
}
