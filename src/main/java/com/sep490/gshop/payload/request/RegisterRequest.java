package com.sep490.gshop.payload.request;

import com.sep490.gshop.common.GenderEnum;
import com.sep490.gshop.common.UserRole;
import jakarta.persistence.Column;
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
public class RegisterRequest {
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must be at most 255 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    @NotNull(message = "Date of birth is required")
    private Long dateOfBirth;

    @Pattern(
            regexp = "^(?:\\+84|0084|0)(?:3[2-9]|5[2689]|7[06-9]|8[1-689]|9[0-9]|2[0-9]|8[0-9]|5[6-9]|7[0-9]|9[0-46-9])\\d{7,8}$",
            message = "Invalid phone number format. Ex: +84909123456 or 0912345678"
    )
    private String phone;

    @Size(max = 255, message = "Address must be at most 255 characters")
    private String address;
    @Enumerated(EnumType.STRING)
    private GenderEnum gender;


    @Column(columnDefinition = "TEXT")
    private String avatar;
}
