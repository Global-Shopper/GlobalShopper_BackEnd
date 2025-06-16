package com.sep490.gshop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
    private String URL;
    private String seller;
    private String ecommercePlatform;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;


}
