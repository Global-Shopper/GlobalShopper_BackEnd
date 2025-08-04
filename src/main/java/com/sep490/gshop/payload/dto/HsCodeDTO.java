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
public class HsCodeDTO {
    private String hsCode;
    private String description;
    private String unit;
    private String parentCode;
    private List<TaxRateSnapshotDTO> taxRates;
}
