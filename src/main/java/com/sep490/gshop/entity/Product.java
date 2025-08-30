package com.sep490.gshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product extends BaseEntity {

    private String name;
    private double price;
    private String brand;
    private String description;
    @ElementCollection
    private List<String> images;
    private String origin;
    @Column(columnDefinition = "TEXT")
    private String productURL;
    private String seller;
    private String ecommercePlatform;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;


}
