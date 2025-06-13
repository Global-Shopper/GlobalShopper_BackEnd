package com.sep490.gshop.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String image;
    private String origin;
    private double rating;
    private String seller;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;


}
