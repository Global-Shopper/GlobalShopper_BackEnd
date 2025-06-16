package com.sep490.gshop.entity;

import com.sep490.gshop.common.GenderEnum;
import com.sep490.gshop.common.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email")
        })
@Inheritance(strategy = InheritanceType.JOINED)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User extends BaseEntity {

    @Id
    private UUID id;
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
    @NotBlank(message = "Date of birth is required")
    private long dateOfBirth;

    @Pattern(
            regexp = "^(?:\\+84|0084|0)(?:3[2-9]|5[2689]|7[06-9]|8[1-689]|9[0-9]|2[0-9]|8[0-9]|5[6-9]|7[0-9]|9[0-46-9])\\d{7,8}$",
            message = "Invalid phone number format. Ex: +84909123456 or 0912345678"
    )
    private String phone;

    @Size(max = 255, message = "Address must be at most 255 characters")
    private String address;
    @NotBlank(message = "Giới tính không được bỏ trống")
    @Enumerated(EnumType.STRING)
    private GenderEnum gender;

    @NotNull(message = "Role is required")
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(columnDefinition = "TEXT")
    private String avatar;

    private boolean isActive = true;

}
