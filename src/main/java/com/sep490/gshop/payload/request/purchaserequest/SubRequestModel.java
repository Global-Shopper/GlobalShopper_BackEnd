package com.sep490.gshop.payload.request.purchaserequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubRequestModel {
    private String seller;
    private String ecommercePlatform;
    private List<String> contactInfo;
    @Size(min = 1, message = "Phải có ít nhất một mục hàng để tạo nhóm")
    @NotNull(message = "Danh sách mục hàng không được để trống")
    private List<String> itemIds;
}
