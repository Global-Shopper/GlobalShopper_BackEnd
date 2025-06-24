package com.sep490.gshop.payload.request;

import com.sep490.gshop.common.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    private String name;
    private String email;
    private String password;
    private String phone;
    private String address;
    private UserRole role;
    private String avatar;
    private boolean isActive;
}
