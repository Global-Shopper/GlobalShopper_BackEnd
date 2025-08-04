package com.sep490.gshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hs_code")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HsCode {
    @Id
    @Column(name = "hs_code", length = 20)
    private String hsCode;

    @Column(length = 500)
    private String description;

    @Column(length = 50)
    private String unit;

    @Column(name = "parent_code", length = 20)
    private String parentCode;

    @OneToMany(mappedBy = "hsCode", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaxRate> taxRates = new ArrayList<>();
}