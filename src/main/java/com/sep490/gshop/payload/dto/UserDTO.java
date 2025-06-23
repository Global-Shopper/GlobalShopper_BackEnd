package com.sep490.gshop.payload.dto;

import com.sep490.gshop.common.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private UserRole role;
    private String avatar;
    private boolean isActive = true;
}
