package com.sep490.gshop.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminDTO {
    private String id;
    private String name;
    private String email;
    private String nation;
    private String phone;
    private String address;
    private String avatar;
    private boolean isActive;
}
