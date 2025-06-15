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
    @NotBlank(message = "Name is required")
    @Size(max = 50, message = "Name cannot longer than 50 characters")
    private String name;
    @NotBlank(message = "Phone number is require")
    @Pattern(
            regexp = "^(?:\\+84|0084|0)(?:3[2-9]|5[2689]|7[06-9]|8[1-689]|9[0-9]|2[0-9]|8[0-9]|5[6-9]|7[0-9]|9[0-46-9])\\d{7,8}$",
            message = "Invalid phone number format. Ex: +84909123456 or 0912345678"
    )
    private String phoneNumber;
    @NotBlank(message = "Location is required")
    private String location;
    private boolean isDefault;
}
