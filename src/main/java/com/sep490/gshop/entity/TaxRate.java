package com.sep490.gshop.entity;

import com.sep490.gshop.common.enums.TaxRegion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "tax_rate")
@Data
@AllArgsConstructor
@NoArgsConstructor
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

    @Column(name = "tax_type", length = 50)
    private String taxType;

    @Column(name = "rate")
    private Double rate;
}