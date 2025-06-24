package com.sep490.gshop.payload.request;

import com.sep490.gshop.common.GenderEnum;
import com.sep490.gshop.common.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminRequest {
        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 50, message = "Name must be between 2-50 characters")
        private String name;

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        private String email;

        @NotNull(message = "Date of birth is required")
        private long dateOfBirth;

        @NotBlank(message = "Nation is required")
        private String nation;

        @NotBlank(message = "Phone is required")
        @Pattern(
                regexp = "^(?:\\+84|0084|0)(?:3[2-9]|5[2689]|7[06-9]|8[1-689]|9[0-9]|2[0-9]|8[0-9]|5[6-9]|7[0-9]|9[0-46-9])\\d{7,8}$",
                message = "Invalid phone number format. Ex: +84909123456 or 0912345678"
        )
        private String phone;

        @NotBlank(message = "Address is required")
        private String address;

        @NotNull(message = "Gender is required")
        @Enumerated(EnumType.STRING)
        private GenderEnum gender;


        private String avatar;
        private boolean isActive = true;
}
