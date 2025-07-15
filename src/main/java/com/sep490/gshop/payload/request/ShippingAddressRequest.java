package com.sep490.gshop.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShippingAddressRequest {
    @NotBlank(message = "Tên của người nhận hàng không được bỏ trống")
    @Size(max = 50, message = "Tên không được dài quá 50 kí tự")
    private String name;
    @NotBlank(message = "Tag không được bỏ trống")
    @Size(max = 20, message = "Tag không được dài quá 20 kí tự")
    private String tag;
    @NotBlank(message = "Số điện thoại không được bỏ trống")
    @Pattern(
            regexp = "^(?:\\+84|0084|0)(?:3[2-9]|5[2689]|7[06-9]|8[1-689]|9[0-9]|2[0-9]|8[0-9]|5[6-9]|7[0-9]|9[0-46-9])\\d{7,8}$",
            message = "định dạng số điện thoại không đúng, ví dụ: +84909123456 hoặc 0912345678"
    )
    private String phoneNumber;
    @NotBlank(message = "Địa chỉ không được bỏ trống")
    private String location;
    private boolean isDefault;
    @NotBlank(message = "Tỉnh không được bỏ trống")
    private String provinceCode;
    @NotBlank(message = "Quận/Huyện không được bỏ trống")
    private String districtCode;
    @NotBlank(message = "Phường/Xã không được bỏ trống")
    private String wardCode;
    @NotBlank(message = "Địa chỉ chi tiết không được bỏ trống")
    private String addressLine;
}
