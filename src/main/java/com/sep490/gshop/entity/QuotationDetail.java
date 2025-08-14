package com.sep490.gshop.entity;

import com.sep490.gshop.common.enums.QuotationType;
import com.sep490.gshop.entity.converter.StringListConverter;
import com.sep490.gshop.entity.subclass.TaxRateSnapshot;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class QuotationDetail extends BaseEntity {
    private double basePrice;
    private double serviceFee;
    private String currency;
    private double ExchangeRate;
    private double totalVNDPrice;
    private String hsCode;
    private Double ServiceRate;

    @ElementCollection
    @CollectionTable(name = "tax_rate_snapshots", joinColumns = @JoinColumn(name = "quotation_detail_id"))
    private List<TaxRateSnapshot> taxRates = new ArrayList<>();
    @Column(columnDefinition = "TEXT")
    private String note;

    @ManyToOne
    @JoinColumn(name = "quotation_id")
    private Quotation quotation;
    @OneToOne
    @JoinColumn(name = "request_item_id")
    private RequestItem requestItem;
}
