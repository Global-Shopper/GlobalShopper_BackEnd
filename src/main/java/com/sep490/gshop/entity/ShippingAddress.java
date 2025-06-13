package com.sep490.gshop.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "shipping_addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingAddress extends BaseEntity {
    private String name;
    private String phoneNumber;
    private String location;
    private boolean isDefault;
}
