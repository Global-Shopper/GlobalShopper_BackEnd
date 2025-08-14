package com.sep490.gshop.payload.request.purchaserequest;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequestModel {
    private String productName;
    private String productURL;
    private List<String> variants;
    @Size(min = 1,message = "Phải có ít nhất một hình ảnh cho từng sản phẩm")
    private List<String> images;
    private int quantity;
    private String description;
    private String seller;
    private String ecommercePlatform;
}
