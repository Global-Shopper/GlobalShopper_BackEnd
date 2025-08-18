package com.sep490.gshop.entity;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Variant extends BaseEntity {
    private String name;
    private boolean isActive;

}
