package com.sep490.gshop.payload.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {
    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(max = 100, message = "Tên sản phẩm không quá 100 ký tự")
    private String name;

    @DecimalMin(value = "0.0", inclusive = false, message = "Giá phải lớn hơn 0")
    private double price;

    @NotBlank(message = "Thương hiệu không được để trống")
    @Size(max = 50, message = "Thương hiệu không quá 50 ký tự")
    private String brand;

    @Size(max = 1000, message = "Mô tả không quá 1000 ký tự")
    private String description;

    @NotBlank(message = "Ảnh sản phẩm không được để trống")
    @Size(max = 255, message = "Link ảnh không quá 255 ký tự")
    private String image;

    @Size(max = 100, message = "Xuất xứ không quá 100 ký tự")
    private String origin;

    // Khi tạo mới, rating thường để mặc định, không nhận từ request
    // private double rating;

    @NotBlank(message = "Tên người bán không được để trống")
    @Size(max = 100, message = "Tên người bán không quá 100 ký tự")
    private String seller;

    @NotNull(message = "CategoryId không được để trống")
    private String categoryId;
}
