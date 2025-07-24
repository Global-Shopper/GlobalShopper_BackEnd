package com.sep490.gshop.entity.subclass;

import com.sep490.gshop.common.enums.TaxRegion;
import com.sep490.gshop.common.enums.TaxType;
import com.sep490.gshop.entity.TaxRate;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class TaxRateSnapshot {
    @Enumerated(EnumType.STRING)
    private TaxRegion region;
    @Enumerated(EnumType.STRING)
    private TaxType taxType;
    private Double rate;
    public TaxRateSnapshot(TaxRate taxRate){
        this.region = taxRate.getRegion();
        this.taxType = taxRate.getTaxType();
        this.rate = taxRate.getRate();
    }
}
