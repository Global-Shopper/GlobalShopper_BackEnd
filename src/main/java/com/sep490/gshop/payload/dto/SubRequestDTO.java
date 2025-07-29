package com.sep490.gshop.payload.dto;

import com.sep490.gshop.common.enums.SubRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubRequestDTO {
    private UUID id;
    private List<String> contactInfo;
    private String seller;
    private String ecommercePlatform;
    private List<RequestItemDTO> requestItems;
    private QuotationForPurchaseRequestDTO quotationForPurchase;
    private SubRequestStatus status;

}
