package com.sep490.gshop.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private String id;
    private String name;
    private double price;
    private String brand;
    private String description;
    private String image;
    private String origin;
    private double rating;
    private String seller;
    private CategoryDTO category;
}
