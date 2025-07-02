package com.sep490.gshop.payload.dto;

import com.sep490.gshop.common.enums.GenderEnum;
import com.sep490.gshop.entity.ShippingAddress;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
}
