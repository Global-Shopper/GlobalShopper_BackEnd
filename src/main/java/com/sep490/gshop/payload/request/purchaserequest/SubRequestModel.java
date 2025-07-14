package com.sep490.gshop.payload.request.purchaserequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubRequestModel {
    private String seller;
    private String ecommercePlatform;
    private List<String> contactInfo;
    private List<String> itemIds;
}
