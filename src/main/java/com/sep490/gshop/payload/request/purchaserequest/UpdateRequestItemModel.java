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
public class UpdateRequestItemModel {
    private String id;
    private String productURL;
    private String productName;
    private List<String> variants;
    private List<String> images;
    private int quantity;
    private String description;
}
