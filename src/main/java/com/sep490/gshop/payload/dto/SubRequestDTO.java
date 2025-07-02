package com.sep490.gshop.payload.dto;

import com.sep490.gshop.entity.RequestItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubRequestDTO {
    private List<String> contactInfo;
    private String seller;
    private String ecommercePlatform;
    private List<RequestItemDTO> requestItems;
}
