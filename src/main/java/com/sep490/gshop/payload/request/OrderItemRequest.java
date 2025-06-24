package com.sep490.gshop.payload.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequest {
    @NotBlank(message = "Product cannot null")
    private String productId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity need to bigger than 0")
    private Integer quantity;
}
