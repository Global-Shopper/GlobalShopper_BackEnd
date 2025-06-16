package com.sep490.gshop.payload.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressSnapshotDTO {
    private String name;
    private String phoneNumber;
    private String location;
    private boolean isDefault;
}
