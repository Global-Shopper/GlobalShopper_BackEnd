package com.sep490.gshop.entity;

import com.sep490.gshop.common.enums.PackageType;
import com.sep490.gshop.common.enums.QuotationType;
import com.sep490.gshop.entity.converter.FeeListConverter;
import com.sep490.gshop.entity.subclass.Fee;
import com.sep490.gshop.entity.subclass.RecipientInformation;
import com.sep490.gshop.entity.subclass.ShipperInformation;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(exclude = "subRequest", callSuper = false)
@Entity
@Table(name = "quotations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Quotation extends BaseEntity {

    @Column(columnDefinition = "TEXT")
    private String note;
    private double shippingEstimate;
    private long expiredDate;
    private double totalPriceEstimate;
    private Double totalWeightEstimate;
    private PackageType packageType;
    private String currency;
    private String region;
    @Embedded
    private ShipperInformation shipper;
    @Embedded
    private RecipientInformation recipient;


    @Convert(converter = FeeListConverter.class)
    private List<Fee> fees;


    private QuotationType quotationType;
    private Double totalPriceBeforeExchange;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_request_id", nullable = false, unique = true)
    private SubRequest subRequest;
    @OneToMany(mappedBy = "quotation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<QuotationDetail> details = new ArrayList<>();
}

