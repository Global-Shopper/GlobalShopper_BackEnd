package com.sep490.gshop.payload.request.purchaserequest;

import jakarta.validation.constraints.NotBlank;
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
public class UpdateRequestModel {
    @NotBlank(message = "Thông tin địa chỉ giao hàng không được để trống")
    private String shippingAddressId;
    private List<String> contactInfo;
    @NotNull(message = "Thông tin sản phẩm không được để trống")
    @Size(min = 1,message = "Thông tin cửa hàng không được để trống")
    private List<UpdateRequestItemModel> items;
}
