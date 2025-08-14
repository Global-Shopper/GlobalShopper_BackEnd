package com.sep490.gshop.payload.request.purchaserequest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
public class OnlineRequest {
    @NotNull(message = "Thông tin địa chỉ giao hàng không được để trống")
    private String shippingAddressId;
    @NotNull(message = "Thông tin sản phẩm không được để trống")
    @Size(min = 1, message = "Danh sách sản phẩm không được để trống")
    @Valid
    private List<ItemRequestModel> requestItems;
}
