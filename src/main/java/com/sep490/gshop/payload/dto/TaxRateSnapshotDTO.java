package com.sep490.gshop.payload.dto;

import com.sep490.gshop.common.enums.TaxRegion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaxRateSnapshotDTO {
    private TaxRegion region;
    private String taxType;
    private Double rate;
}