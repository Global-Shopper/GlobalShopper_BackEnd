package com.sep490.gshop.entity;

import com.sep490.gshop.common.enums.TaxRegion;
import com.sep490.gshop.common.enums.TaxType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "tax_rate")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaxRate {
    @Id
    @UuidGenerator
    @GeneratedValue
    UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hs_code", referencedColumnName = "hs_code")
    private HsCode hsCode;
    @Enumerated(EnumType.STRING)
    private TaxRegion region;

    @Enumerated(EnumType.STRING)
    private TaxType taxType;

    @Column(name = "rate")
    private Double rate;
    private String taxName;
}