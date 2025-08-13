package com.sep490.gshop.payload.request.purchaserequest;

import com.sep490.gshop.entity.subclass.AddressSnapshot;
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
    private String shippingAddressId;
    private AddressSnapshot shippingAddress;
    private List<String> contactInfo;
    private List<UpdateRequestItemModel> items;
}
