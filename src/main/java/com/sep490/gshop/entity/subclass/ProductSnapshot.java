package com.sep490.gshop.entity.subclass;

import com.sep490.gshop.entity.Product;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSnapshot {

    private UUID productId;
    private String name;
    private String brand;
    private String description;
    private List<String> images;
    private String origin;
    private double price;
    private String seller;

    public ProductSnapshot(Product product) {
        this.productId = product.getId();
        this.name = product.getName();
        this.brand = product.getBrand();
        this.description = product.getDescription();
        this.images = product.getImages();
        this.origin = product.getOrigin();
        this.seller = product.getSeller();
        this.price = product.getPrice();
    }

}
