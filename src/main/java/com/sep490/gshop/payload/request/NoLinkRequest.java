package com.sep490.gshop.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NoLinkRequest {
    private String addressId;
    private List<ItemRequest> itemRequest;
}
