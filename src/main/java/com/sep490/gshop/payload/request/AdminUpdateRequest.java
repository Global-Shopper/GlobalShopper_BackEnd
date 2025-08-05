package com.sep490.gshop.payload.request;

import com.sep490.gshop.common.enums.GenderEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminUpdateRequest {
    @Size(min = 2, max = 50, message = "Name must be between 2-50 characters")
    private String name;
    @Email(message = "Invalid email format")
    private String email;
    private long dateOfBirth;
    private String nation;
    @Pattern(
            regexp = "^(?:\\+84|0084|0)(?:3[2-9]|5[2689]|7[06-9]|8[1-689]|9[0-9]|2[0-9]|8[0-9]|5[6-9]|7[0-9]|9[0-46-9])\\d{7,8}$",
            message = "Invalid phone number format. Ex: +84909123456 or 0912345678"
    )
    private String phone;
    private String address;
    @Enumerated(EnumType.STRING)
    private GenderEnum gender;
    private boolean isActive;
}
