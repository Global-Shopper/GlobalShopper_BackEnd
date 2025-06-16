package com.sep490.gshop.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    private String id;
    private ProductSnapshotDTO product;
    private int quantity;
    private String orderId;
}
