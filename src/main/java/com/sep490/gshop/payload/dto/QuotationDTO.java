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
public class QuotationDTO {
    private String id;
    private String note;
    private boolean accepted;
    private String subRequestId;
    private List<QuotationDetailDTO> details;
}
