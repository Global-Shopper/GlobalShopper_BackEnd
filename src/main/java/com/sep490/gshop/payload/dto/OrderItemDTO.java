package com.sep490.gshop.payload.dto;

import com.sep490.gshop.entity.subclass.TaxRateSnapshot;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    private String id;
    private double basePrice;
    private String currency;
    private double totalVNDPrice;
    private double serviceFee;
    private List<TaxRateSnapshot> taxRates = new ArrayList<>();

    //RequestItem data
    private String productURL;
    private String productName;
    private String contactInfo;
    private List<String> images;
    private List<String> variants;
    private String description;
    private int quantity;
}
