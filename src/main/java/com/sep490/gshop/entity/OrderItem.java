package com.sep490.gshop.entity;

import com.sep490.gshop.entity.subclass.ProductSnapshot;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem extends BaseEntity{

    @Embedded
    private ProductSnapshot product;
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

}
