package com.sep490.gshop.payload.request;

import com.sep490.gshop.common.OrderStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {

    @Size(max = 255, message = "Note is not exceed 255 characters")
    private String note;

    @DecimalMin(value = "0.0", inclusive = true, message = "Total price need to bigger than 0 VNĐ")
    private double totalPrice;

    @NotNull(message = "Order item need to bigger than 0")
    private List<OrderItemRequest> orderItems;

    @NotNull(message = "Shipping address is required")
    private String shippingAddressId;

}
