package com.sep490.gshop.payload.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestItemDTO {
    private String id;
    private String productURL;
    private String productName;
    private List<String> variants;
    private List<String> images;
    private String description;
    private int quantity;
    private QuotationDetailDTO quotationDetail;
}
