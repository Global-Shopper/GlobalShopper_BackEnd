package com.sep490.gshop.entity;

import com.sep490.gshop.entity.converter.StringListConverter;

import com.sep490.gshop.entity.subclass.TaxRateSnapshot;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem extends BaseEntity{

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;


    //QuotationDetail data
    private double basePrice;
    private String currency;

    private Double totalVNDPrice;
    private double serviceFee;
    @ElementCollection
    @CollectionTable(name = "order_item_tax_rate", joinColumns = @JoinColumn(name = "order_item_id"))
    private List<TaxRateSnapshot> taxRates = new ArrayList<>();

    //RequestItem data
    @Column(columnDefinition = "TEXT")
    private String productURL;
    private String productName;
    @Column(columnDefinition = "TEXT")
    private String contactInfo;
    @ElementCollection
    private List<String> images;
    @Convert(converter = StringListConverter.class)
    private List<String> variants;
    @Column(columnDefinition = "TEXT")
    private String description;
    private int quantity;

    public OrderItem(RequestItem requestItem) {
        this.productURL = requestItem.getProductURL();
        this.productName = requestItem.getProductName();
        this.contactInfo = requestItem.getContactInfo();
        this.images = new ArrayList<>(requestItem.getImages());
        this.variants = requestItem.getVariants();
        this.description = requestItem.getDescription();
        this.quantity = requestItem.getQuantity();
        this.totalVNDPrice = requestItem.getQuotationDetail().getTotalVNDPrice();
        this.basePrice = requestItem.getQuotationDetail().getBasePrice();
        this.serviceFee = requestItem.getQuotationDetail().getServiceFee();
        this.taxRates = new ArrayList<>(requestItem.getQuotationDetail().getTaxRates());
    }

}
