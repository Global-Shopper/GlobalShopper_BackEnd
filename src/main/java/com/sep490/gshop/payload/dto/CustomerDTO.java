package com.sep490.gshop.payload.dto;

import com.sep490.gshop.common.enums.GenderEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDTO {
    private String id;
    private String name;
    private String email;
    private String phone;
    private GenderEnum gender;
    private long dateOfBirth;
    private String avatar;
    private WalletDTO wallet;
    private boolean isActive;
    private long createdAt;
    private long updatedAt;
}
