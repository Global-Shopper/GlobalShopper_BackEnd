package com.sep490.gshop.payload.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestItemDTO {
    private String id;
    private String productURL;
    private String productName;
    private String contactInfo;
    private String variants;
    private String description;
    private int quantity;
}
