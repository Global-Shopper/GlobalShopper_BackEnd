package com.sep490.gshop.payload.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSnapshotDTO {
    private String productId;
    private String name;
    private String sku;
    private String imageUrl;
    private double price;
    private String brand;
    private String category;
    // Thêm các trường khác nếu ProductSnapshot entity có
}
