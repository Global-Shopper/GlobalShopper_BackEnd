package com.sep490.gshop.payload.dto;

import com.sep490.gshop.common.enums.TaxRegion;
import com.sep490.gshop.common.enums.TaxType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaxRateSnapshotDTO {
    private TaxRegion region;
    private TaxType taxType;
    private Double rate;
    private String taxName;
}